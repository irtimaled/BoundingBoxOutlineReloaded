package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.*;
import com.irtimaled.bbor.forge.messages.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ForgeCommonProxy implements IEventHandler {

    public Map<EntityPlayerMP, Integer> playerDimensions = new ConcurrentHashMap<EntityPlayerMP, Integer>();
    private Map<EntityPlayerMP, Set<BoundingBox>> playerBoundingBoxesCache = new HashMap<EntityPlayerMP, Set<BoundingBox>>();

    protected CommonProxy getProxy() {
        if (commonProxy == null)
            commonProxy = new CommonProxy();
        return commonProxy;
    }

    protected SimpleNetworkWrapper network;
    private CommonProxy commonProxy;

    public void init(ConfigManager configManager) {
        CommonProxy proxy = getProxy();
        proxy.setEventHandler(this);
        proxy.init(configManager);
        network = NetworkRegistry.INSTANCE.newSimpleChannel("bbor");
        network.registerMessage(AddBoundingBoxMessageHandler.class, AddBoundingBoxMessage.class, 0, Side.CLIENT);
        network.registerMessage(RemoveBoundingBoxMessageHandler.class, RemoveBoundingBoxMessage.class, 1, Side.CLIENT);
        network.registerMessage(InitializeClientMessageHandler.class, InitializeClientMessage.class, 2, Side.CLIENT);
    }

    @SubscribeEvent
    public void worldEvent(WorldEvent.Load event) {
        getProxy().worldLoaded(event.world);
    }

    @SubscribeEvent
    public void chunkEvent(ChunkEvent.Load event) {
        getProxy().chunkLoaded(event.getChunk());
    }

    @SubscribeEvent
    public void playerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent evt) {
        if (playerDimensions.containsKey(evt.player)) {
            EntityPlayerMP player = (EntityPlayerMP) evt.player;
            int dimension = player.dimension;
            playerDimensions.put(player, dimension);

            sendToPlayer(player, getProxy().boundingBoxCacheMap.get(dimension));
        }
    }

    protected boolean isRemotePlayer(EntityPlayer player) {
        return true;
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent evt) {
        if (evt.player instanceof EntityPlayerMP &&
                isRemotePlayer(evt.player)) {
            EntityPlayerMP player = (EntityPlayerMP) evt.player;
            initializeClient(player);
            int dimension = player.dimension;
            playerDimensions.put(player, dimension);
            sendToPlayer(player, getProxy().boundingBoxCacheMap.get(dimension));
        }
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent evt) {
        if (playerDimensions.containsKey(evt.player)) {
            playerDimensions.remove(evt.player);
            playerBoundingBoxesCache.remove(evt.player);
        }
    }

    @SubscribeEvent
    public void tickEvent(TickEvent event) {
        for (EntityPlayerMP player : playerDimensions.keySet()) {

            MinecraftServer mc = MinecraftServer.getServer();
            if (!mc.getConfigurationManager().playerEntityList.contains(player)) {
                playerDimensions.remove(player);
            } else {
                int dimension = playerDimensions.get(player);
                if (getProxy().boundingBoxCacheMap.containsKey(dimension)) {
                    sendToPlayer(player, getProxy().boundingBoxCacheMap.get(dimension));
                }
            }
        }
    }

    private void initializeClient(EntityPlayerMP player) {
        network.sendTo(InitializeClientMessage.from(getProxy().getWorldData()), player);
    }

    private void sendToPlayer(EntityPlayerMP player, BoundingBoxCache boundingBoxCache) {
        Map<BoundingBox, Set<BoundingBox>> cacheSubset = getBoundingBoxMap(player, boundingBoxCache.getBoundingBoxes());

        int dimension = player.dimension;
        if (cacheSubset.keySet().size() > 0) {
            Logger.info("send %d entries to %s (%d)", cacheSubset.keySet().size(), player.getDisplayNameString(), dimension);
        }

        for (BoundingBox key : cacheSubset.keySet()) {
            Set<BoundingBox> boundingBoxes = cacheSubset.get(key);
            network.sendTo(AddBoundingBoxMessage.from(dimension, key, boundingBoxes), player);

            if (!playerBoundingBoxesCache.containsKey(player)) {
                playerBoundingBoxesCache.put(player, new HashSet<BoundingBox>());
            }
            playerBoundingBoxesCache.get(player).add(key);
        }
    }

    private Map<BoundingBox, Set<BoundingBox>> getBoundingBoxMap(EntityPlayerMP player, Map<BoundingBox, Set<BoundingBox>> boundingBoxMap) {
        Map<BoundingBox, Set<BoundingBox>> cacheSubset = new HashMap<BoundingBox, Set<BoundingBox>>();
        for (BoundingBox key : boundingBoxMap.keySet()) {
            if (!playerBoundingBoxesCache.containsKey(player) || !playerBoundingBoxesCache.get(player).contains(key)) {
                cacheSubset.put(key, boundingBoxMap.get(key));
            }
        }
        return cacheSubset;
    }

    public void boundingBoxRemoved(BoundingBox bb) {
        // the only bounding boxes that can change are spawn or villages - both of these are in overworld so I guess
        // hard coding dimension 0 is okay.
        RemoveBoundingBoxMessage message = RemoveBoundingBoxMessage.from(0, bb);
        for (EntityPlayerMP player : playerDimensions.keySet()) {
            if (player.dimension == 0) {
                Logger.info("remove 1 entry from %s (0)", player.getDisplayNameString());
                network.sendTo(message, player);

                if (playerBoundingBoxesCache.containsKey(player) &&
                        playerBoundingBoxesCache.get(player).contains(bb)) {
                    playerBoundingBoxesCache.get(player).remove(bb);
                }
            }
        }
    }

    public void setWorldData(WorldData worldData) {
        getProxy().setWorldData(worldData);
    }

    public void addBoundingBox(int dimension, BoundingBox key, Set<BoundingBox> boundingBoxes) {
        Map<Integer, BoundingBoxCache> boundingBoxCacheMap = getProxy().boundingBoxCacheMap;
        if (!boundingBoxCacheMap.containsKey(dimension)) {
            boundingBoxCacheMap.put(dimension, new BoundingBoxCache());
        }

        boundingBoxCacheMap.get(dimension).addBoundingBoxes(key, boundingBoxes);
    }

    public void removeBoundingBox(int dimension, BoundingBox key) {
        Map<Integer, BoundingBoxCache> boundingBoxCacheMap = getProxy().boundingBoxCacheMap;

        if (boundingBoxCacheMap.containsKey(dimension)) {
            boundingBoxCacheMap.get(dimension).removeBoundingBox(key);
        }
    }
}
