package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.chunkProcessors.AbstractChunkProcessor;
import com.irtimaled.bbor.common.chunkProcessors.EndChunkProcessor;
import com.irtimaled.bbor.common.chunkProcessors.NetherChunkProcessor;
import com.irtimaled.bbor.common.chunkProcessors.OverworldChunkProcessor;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.PayloadBuilder;
import com.irtimaled.bbor.common.messages.RemoveBoundingBox;
import com.irtimaled.bbor.common.models.*;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommonProxy {
    private Set<ServerPlayer> players = new HashSet<>();
    private Map<ServerPlayer, Set<AbstractBoundingBox>> playerBoundingBoxesCache = new HashMap<>();
    private Map<Integer, VillageProcessor> villageProcessors = new HashMap<>();
    private Map<Integer, AbstractChunkProcessor> chunkProcessors = new HashMap<>();
    private WorldData worldData = null;
    private final Map<Integer, BoundingBoxCache> dimensionCache = new ConcurrentHashMap<>();

    public void init() {
        EventBus.subscribe(WorldLoaded.class, e -> worldLoaded(e.getWorld()));
        EventBus.subscribe(ChunkLoaded.class, e -> chunkLoaded(e.getChunk()));
        EventBus.subscribe(MobSpawnerBroken.class, e -> mobSpawnerBroken(e.getDimensionId(), e.getPos()));
        EventBus.subscribe(PlayerLoggedIn.class, e -> playerLoggedIn(e.getPlayer()));
        EventBus.subscribe(PlayerLoggedOut.class, e -> playerLoggedOut(e.getPlayer()));
        EventBus.subscribe(PlayerSubscribed.class, e -> sendBoundingBoxes(e.getPlayer()));
        EventBus.subscribe(ServerWorldTick.class, e -> serverWorldTick(e.getWorld()));
        EventBus.subscribe(ServerTick.class, e -> serverTick());
        EventBus.subscribe(VillageRemoved.class, e -> sendRemoveBoundingBox(e.getDimensionId(), e.getVillage()));
    }

    protected void setWorldData(long seed, int spawnX, int spawnZ) {
        worldData = new WorldData(seed, spawnX, spawnZ);
    }

    private void worldLoaded(WorldServer world) {
        int dimensionId = world.dimension.getType().getId();
        BoundingBoxCache boundingBoxCache = getOrCreateCache(dimensionId);
        AbstractChunkProcessor chunkProcessor = null;
        if (dimensionId == Dimensions.OVERWORLD) {
            setWorldData(world.getSeed(), world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnZ());
            chunkProcessor = new OverworldChunkProcessor(boundingBoxCache);
        }
        if (dimensionId == Dimensions.NETHER) {
            chunkProcessor = new NetherChunkProcessor(boundingBoxCache);
        }
        if (dimensionId == Dimensions.THE_END) {
            chunkProcessor = new EndChunkProcessor(boundingBoxCache);
        }
        Logger.info("create world dimension: %s, %s (seed: %d)", dimensionId, world.getClass().toString(), world.getSeed());
        chunkProcessors.put(dimensionId, chunkProcessor);
        villageProcessors.put(dimensionId, new VillageProcessor(dimensionId, boundingBoxCache));
    }

    private void chunkLoaded(Chunk chunk) {
        int dimensionId = chunk.getWorld().dimension.getType().getId();
        AbstractChunkProcessor chunkProcessor = chunkProcessors.get(dimensionId);
        if (chunkProcessor != null) {
            chunkProcessor.process(chunk);
        }
    }

    private void playerLoggedIn(ServerPlayer player) {
        player.sendPacket(InitializeClient.getPayload(worldData));
    }

    private void playerLoggedOut(ServerPlayer player) {
        players.remove(player);
        playerBoundingBoxesCache.remove(player);
    }

    private void sendRemoveBoundingBox(int dimensionId, AbstractBoundingBox boundingBox) {
        PayloadBuilder payload = RemoveBoundingBox.getPayload(dimensionId, boundingBox);
        if (payload == null) return;

        for (ServerPlayer player : players) {
            if (player.getDimensionId() == dimensionId) {
                player.sendPacket(payload);

                if (playerBoundingBoxesCache.containsKey(player)) {
                    playerBoundingBoxesCache.get(player).remove(boundingBox);
                }
            }
        }
    }

    private void sendBoundingBoxes(ServerPlayer player) {
        players.add(player);
        sendToPlayer(player, getCache(player.getDimensionId()));
    }

    private void sendToPlayer(ServerPlayer player, BoundingBoxCache boundingBoxCache) {
        if (boundingBoxCache == null) return;

        Map<AbstractBoundingBox, Set<AbstractBoundingBox>> cacheSubset = getBoundingBoxMap(player, boundingBoxCache.getBoundingBoxes());

        for (AbstractBoundingBox key : cacheSubset.keySet()) {
            Set<AbstractBoundingBox> boundingBoxes = cacheSubset.get(key);
            PayloadBuilder payload = AddBoundingBox.getPayload(player.getDimensionId(), key, boundingBoxes);
            if (payload != null)
                player.sendPacket(payload);

            if (!playerBoundingBoxesCache.containsKey(player)) {
                playerBoundingBoxesCache.put(player, new HashSet<>());
            }
            playerBoundingBoxesCache.get(player).add(key);
        }
    }

    private Map<AbstractBoundingBox, Set<AbstractBoundingBox>> getBoundingBoxMap(ServerPlayer player, Map<AbstractBoundingBox, Set<AbstractBoundingBox>> boundingBoxMap) {
        Map<AbstractBoundingBox, Set<AbstractBoundingBox>> cacheSubset = new HashMap<>();
        for (AbstractBoundingBox key : boundingBoxMap.keySet()) {
            if (!playerBoundingBoxesCache.containsKey(player) || !playerBoundingBoxesCache.get(player).contains(key)) {
                cacheSubset.put(key, boundingBoxMap.get(key));
            }
        }
        return cacheSubset;
    }

    protected void addBoundingBox(int dimensionId, AbstractBoundingBox key, Set<AbstractBoundingBox> boundingBoxes) {
        BoundingBoxCache cache = getCache(dimensionId);
        if (cache == null) return;

        cache.addBoundingBoxes(key, boundingBoxes);
    }

    protected void removeBoundingBox(int dimensionId, AbstractBoundingBox key) {
        BoundingBoxCache cache = getCache(dimensionId);
        if (cache == null) return;

        cache.removeBoundingBox(key);
    }

    private void mobSpawnerBroken(int dimensionId, Coords pos) {
        AbstractBoundingBox boundingBox = BoundingBoxMobSpawner.from(pos);
        removeBoundingBox(dimensionId, boundingBox);
        sendRemoveBoundingBox(dimensionId, boundingBox);
    }

    private void serverTick() {
        for (ServerPlayer player : players) {
            sendToPlayer(player, getCache(player.getDimensionId()));
        }
    }

    private void serverWorldTick(WorldServer world) {
        int dimensionId = world.dimension.getType().getId();
        VillageProcessor villageProcessor = villageProcessors.get(dimensionId);
        if (villageProcessor == null) return;

        villageProcessor.process(world.getVillageCollection());
    }

    protected BoundingBoxCache getCache(int dimensionId) {
        return dimensionCache.get(dimensionId);
    }

    protected BoundingBoxCache getOrCreateCache(int dimensionId) {
        return dimensionCache.computeIfAbsent(dimensionId, dt -> new BoundingBoxCache());
    }

    protected void clearCaches() {
        for (VillageProcessor villageProcessor : villageProcessors.values()) {
            villageProcessor.clear();
        }
        villageProcessors.clear();
        for (BoundingBoxCache cache : dimensionCache.values()) {
            cache.clear();
        }
        dimensionCache.clear();
    }
}
