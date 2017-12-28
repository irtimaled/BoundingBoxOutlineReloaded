package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.DimensionCache;
import com.irtimaled.bbor.common.IVillageEventHandler;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.forge.messages.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ForgeCommonProxy implements IVillageEventHandler {
    private Map<EntityPlayerMP, DimensionType> playerDimensions = new ConcurrentHashMap<>();
    private Map<EntityPlayerMP, Set<BoundingBox>> playerBoundingBoxesCache = new HashMap<>();
    private HashSet<EntityPlayerMP> registeredPlayers = new HashSet<>();

    protected CommonProxy getProxy() {
        if (commonProxy == null)
            commonProxy = new CommonProxy();
        return commonProxy;
    }

    @SubscribeEvent
    public void packetRegistrationEvent(FMLNetworkEvent.CustomPacketRegistrationEvent event) {
        if (event.getOperation().equals("REGISTER") &&
                event.getRegistrations().contains("bbor") &&
                event.getHandler() instanceof NetHandlerPlayServer) {
            registeredPlayers.add(((NetHandlerPlayServer) event.getHandler()).player);
        }
    }

    protected SimpleNetworkWrapper network;
    private CommonProxy commonProxy;

    void init() {
        CommonProxy proxy = getProxy();
        proxy.setEventHandler(this);
        proxy.init();
        network = NetworkRegistry.INSTANCE.newSimpleChannel("bbor");
        network.registerMessage(AddBoundingBoxMessageHandler.class, AddBoundingBoxMessage.class, 0, Side.CLIENT);
        network.registerMessage(RemoveBoundingBoxMessageHandler.class, RemoveBoundingBoxMessage.class, 1, Side.CLIENT);
        network.registerMessage(InitializeClientMessageHandler.class, InitializeClientMessage.class, 2, Side.CLIENT);
    }

    @SubscribeEvent
    public void worldEvent(WorldEvent.Load event) {
        World world = event.getWorld();
        getProxy().worldLoaded(world);
    }

    @SubscribeEvent
    public void chunkEvent(ChunkEvent.Load event) {
        getProxy().chunkLoaded(event.getChunk());
    }

    @SubscribeEvent
    public void playerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent evt) {
        if (playerDimensions.containsKey(evt.player)) {
            EntityPlayerMP player = (EntityPlayerMP) evt.player;
            sendBoundingBoxes(player);
        }
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent evt) {
        if (evt.player instanceof EntityPlayerMP &&
                isRemotePlayer(evt.player)) {
            EntityPlayerMP player = (EntityPlayerMP) evt.player;
            initializeClient(player);
            sendBoundingBoxes(player);
        }
    }

    private void sendBoundingBoxes(EntityPlayerMP player) {
        DimensionType dimensionType = DimensionType.getById(player.dimension);
        playerDimensions.put(player, dimensionType);
        sendToPlayer(player, getDimensionCache().getBoundingBoxes(dimensionType));
    }

    protected boolean isRemotePlayer(EntityPlayer player) {
        return registeredPlayers.contains(player);
    }

    private void initializeClient(EntityPlayerMP player) {
        network.sendTo(InitializeClientMessage.from(getDimensionCache().getWorldData()), player);
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent evt) {
        if (playerDimensions.containsKey(evt.player)) {
            playerDimensions.remove(evt.player);
            playerBoundingBoxesCache.remove(evt.player);
            registeredPlayers.remove(evt.player);
        }
    }

    @SubscribeEvent
    public void tickEvent(TickEvent event) {
        for (EntityPlayerMP player : playerDimensions.keySet()) {
            MinecraftServer mc = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (!mc.getPlayerList().getPlayers().contains(player)) {
                playerDimensions.remove(player);
            } else {
                DimensionType dimensionType = playerDimensions.get(player);
                sendToPlayer(player, getDimensionCache().getBoundingBoxes(dimensionType));
            }
        }
    }

    private void sendToPlayer(EntityPlayerMP player, BoundingBoxCache boundingBoxCache) {
        if (boundingBoxCache == null)
            return;
        Map<BoundingBox, Set<BoundingBox>> cacheSubset = getBoundingBoxMap(player, boundingBoxCache.getBoundingBoxes());

        DimensionType dimensionType = DimensionType.getById(player.dimension);
        if (cacheSubset.keySet().size() > 0) {
            Logger.info("send %d entries to %s (%s)", cacheSubset.keySet().size(), player.getDisplayNameString(), dimensionType);
        }

        for (BoundingBox key : cacheSubset.keySet()) {
            Set<BoundingBox> boundingBoxes = cacheSubset.get(key);
            network.sendTo(AddBoundingBoxMessage.from(dimensionType, key, boundingBoxes), player);

            if (!playerBoundingBoxesCache.containsKey(player)) {
                playerBoundingBoxesCache.put(player, new HashSet<>());
            }
            playerBoundingBoxesCache.get(player).add(key);
        }
    }

    private Map<BoundingBox, Set<BoundingBox>> getBoundingBoxMap(EntityPlayerMP player, Map<BoundingBox, Set<BoundingBox>> boundingBoxMap) {
        Map<BoundingBox, Set<BoundingBox>> cacheSubset = new HashMap<>();
        for (BoundingBox key : boundingBoxMap.keySet()) {
            if (!playerBoundingBoxesCache.containsKey(player) || !playerBoundingBoxesCache.get(player).contains(key)) {
                cacheSubset.put(key, boundingBoxMap.get(key));
            }
        }
        return cacheSubset;
    }

    public void villageRemoved(DimensionType dimensionType, BoundingBox bb) {
        RemoveBoundingBoxMessage message = RemoveBoundingBoxMessage.from(dimensionType, bb);
        for (EntityPlayerMP player : playerDimensions.keySet()) {
            if (DimensionType.getById(player.dimension) == dimensionType) {
                Logger.info("remove 1 entry from %s (%s)", player.getDisplayNameString(), dimensionType);
                network.sendTo(message, player);

                if (playerBoundingBoxesCache.containsKey(player) &&
                        playerBoundingBoxesCache.get(player).contains(bb)) {
                    playerBoundingBoxesCache.get(player).remove(bb);
                }
            }
        }
    }

    public void setWorldData(long seed, int spawnX, int spawnZ) {
        getDimensionCache().setWorldData(seed, spawnX, spawnZ);
    }

    public void addBoundingBox(DimensionType dimensionType, BoundingBox key, Set<BoundingBox> boundingBoxes) {
        DimensionCache dimensionCache = getDimensionCache();
        BoundingBoxCache cache = dimensionCache.getBoundingBoxes(dimensionType);
        if (cache == null) {
            dimensionCache.put(dimensionType, cache = new BoundingBoxCache());
        }
        cache.addBoundingBoxes(key, boundingBoxes);
    }

    public void removeBoundingBox(DimensionType dimensionType, BoundingBox key) {
        BoundingBoxCache cache = getDimensionCache().getBoundingBoxes(dimensionType);
        if (cache != null) {
            cache.removeBoundingBox(key);
        }
    }

    private DimensionCache getDimensionCache() {
        return getProxy().getDimensionCache();
    }
}
