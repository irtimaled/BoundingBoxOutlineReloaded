package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.RemoveBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkProviderServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommonProxy {
    private Map<EntityPlayerMP, DimensionType> playerDimensions = new ConcurrentHashMap<>();
    private Map<EntityPlayerMP, Set<BoundingBox>> playerBoundingBoxesCache = new HashMap<>();

    protected DimensionCache dimensionCache;
    protected Set<VillageProcessor> villageProcessors = new HashSet<>();

    public void init() {
        dimensionCache = new DimensionCache();
        registerEventHandlers();
    }

    protected void registerEventHandlers() {
        EventBus.subscribe(WorldLoaded.class, e -> worldLoaded(e.getWorld()));
        EventBus.subscribe(ChunkLoaded.class, e -> chunkLoaded(e.getChunk()));
        EventBus.subscribe(MobSpawnerBroken.class, e -> mobSpawnerBroken(e.getDimensionType(), e.getPos()));
        EventBus.subscribe(PlayerChangedDimension.class, e -> playerChangedDimension(e.getPlayer()));
        EventBus.subscribe(PlayerLoggedIn.class, e -> playerLoggedIn(e.getPlayer()));
        EventBus.subscribe(PlayerLoggedOut.class, e -> playerLoggedOut(e.getPlayer()));
        EventBus.subscribe(VillageRemoved.class, e -> sendRemoveBoundingBox(e.getDimensionType(), e.getBoundingBox()));
        EventBus.subscribe(PlayerSubscribed.class, e -> sendBoundingBoxes(e.getPlayer()));
        EventBus.subscribe(Tick.class, e -> tick());
    }

    protected boolean hasRemoteUsers() {
        return playerDimensions.size() > 0;
    }

    private void worldLoaded(World world) {
        IChunkProvider chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof ChunkProviderServer) {
            dimensionCache.setWorldData(world.getSeed(), world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnZ());
            DimensionType dimensionType = world.dimension.getType();
            Logger.info("create world dimension: %s, %s (seed: %d)", dimensionType, world.getClass().toString(), world.getSeed());
            DimensionProcessor boundingBoxCache = new DimensionProcessor(dimensionType);
            dimensionCache.put(dimensionType, boundingBoxCache);
            if (ConfigManager.drawVillages.getBoolean()) {
                villageProcessors.add(new VillageProcessor(world, dimensionType, boundingBoxCache));
            }
        }
    }

    private void chunkLoaded(Chunk chunk) {
        DimensionType dimensionType = chunk.getWorld().dimension.getType();
        BoundingBoxCache cache = dimensionCache.get(dimensionType);
        if (cache instanceof DimensionProcessor) {
            ((DimensionProcessor) cache).processChunk(chunk);
        }
    }

    private void playerChangedDimension(EntityPlayerMP player) {
        if (playerDimensions.containsKey(player)) {
            sendBoundingBoxes(player);
        }
    }

    private void playerLoggedIn(EntityPlayerMP player) {
        player.connection.sendPacket(InitializeClient.getPayload(dimensionCache.getWorldData()));
    }

    private void playerLoggedOut(EntityPlayerMP player) {
        playerDimensions.remove(player);
        playerBoundingBoxesCache.remove(player);
    }

    private void sendRemoveBoundingBox(DimensionType dimensionType, BoundingBox boundingBox) {
        SPacketCustomPayload payload = RemoveBoundingBox.getPayload(dimensionType, boundingBox);
        for (EntityPlayerMP player : playerDimensions.keySet()) {
            if (DimensionType.getById(player.dimension) == dimensionType) {
                Logger.info("remove 1 entry from %s (%s)", player.getScoreboardName(), dimensionType);
                player.connection.sendPacket(payload);

                if (playerBoundingBoxesCache.containsKey(player)) {
                    playerBoundingBoxesCache.get(player).remove(boundingBox);
                }
            }
        }
    }

    private void sendBoundingBoxes(EntityPlayerMP player) {
        DimensionType dimensionType = DimensionType.getById(player.dimension);
        playerDimensions.put(player, dimensionType);
        sendToPlayer(player, dimensionCache.getBoundingBoxes(dimensionType));
    }

    private void sendToPlayer(EntityPlayerMP player, BoundingBoxCache boundingBoxCache) {
        if (boundingBoxCache == null)
            return;
        Map<BoundingBox, Set<BoundingBox>> cacheSubset = getBoundingBoxMap(player, boundingBoxCache.getBoundingBoxes());

        DimensionType dimensionType = DimensionType.getById(player.dimension);
        if (cacheSubset.keySet().size() > 0) {
            Logger.info("send %d entries to %s (%s)", cacheSubset.keySet().size(), player.getScoreboardName(), dimensionType);
        }

        for (BoundingBox key : cacheSubset.keySet()) {
            Set<BoundingBox> boundingBoxes = cacheSubset.get(key);
            player.connection.sendPacket(AddBoundingBox.getPayload(dimensionType, key, boundingBoxes));

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

    protected void removeBoundingBox(DimensionType dimensionType, BoundingBox key) {
        BoundingBoxCache cache = dimensionCache.getBoundingBoxes(dimensionType);
        if (cache != null) {
            cache.removeBoundingBox(key);
        }
    }

    private void mobSpawnerBroken(DimensionType dimensionType, BlockPos pos) {
        BoundingBox boundingBox = BoundingBoxMobSpawner.from(pos);
        removeBoundingBox(dimensionType, boundingBox);
        sendRemoveBoundingBox(dimensionType, boundingBox);
    }

    protected void tick() {
        villageProcessors.forEach(VillageProcessor::process);
        for (EntityPlayerMP player : playerDimensions.keySet()) {
            DimensionType dimensionType = playerDimensions.get(player);
            sendToPlayer(player, dimensionCache.getBoundingBoxes(dimensionType));
        }
    }
}
