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
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.ServerPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommonProxy {
    private Set<ServerPlayer> players = ConcurrentHashMap.newKeySet();
    private Map<ServerPlayer, Set<AbstractBoundingBox>> playerBoundingBoxesCache = new HashMap<>();
    private Map<Integer, VillageProcessor> villageProcessors = new HashMap<>();
    private Map<Integer, AbstractChunkProcessor> chunkProcessors = new HashMap<>();
    private final Map<Integer, BoundingBoxCache> dimensionCache = new ConcurrentHashMap<>();
    private Long seed = null;
    private Integer spawnX = null;
    private Integer spawnZ = null;

    public void init() {
        EventBus.subscribe(WorldLoaded.class, this::worldLoaded);
        EventBus.subscribe(ChunkLoaded.class, this::chunkLoaded);
        EventBus.subscribe(MobSpawnerBroken.class, this::mobSpawnerBroken);
        EventBus.subscribe(PlayerLoggedIn.class, this::playerLoggedIn);
        EventBus.subscribe(PlayerLoggedOut.class, this::playerLoggedOut);
        EventBus.subscribe(PlayerSubscribed.class, this::onPlayerSubscribed);
        EventBus.subscribe(ServerWorldTick.class, this::serverWorldTick);
        EventBus.subscribe(ServerTick.class, e -> serverTick());
        EventBus.subscribe(VillageRemoved.class, this::onVillageRemoved);
    }

    protected void setSeed(long seed) {
        this.seed = seed;
    }

    protected void setWorldSpawn(int spawnX, int spawnZ) {
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
    }

    private void worldLoaded(WorldLoaded event) {
        int dimensionId = event.getDimensionId();
        long seed = event.getSeed();
        BoundingBoxCache boundingBoxCache = getOrCreateCache(dimensionId);
        AbstractChunkProcessor chunkProcessor = null;
        switch (dimensionId) {
            case Dimensions.OVERWORLD:
                setSeed(seed);
                setWorldSpawn(event.getSpawnX(), event.getSpawnZ());
                chunkProcessor = new OverworldChunkProcessor(boundingBoxCache);
                break;
            case Dimensions.NETHER:
                chunkProcessor = new NetherChunkProcessor(boundingBoxCache);
                break;
            case Dimensions.THE_END:
                chunkProcessor = new EndChunkProcessor(boundingBoxCache);
                break;
        }
        Logger.info("create world dimension: %s (seed: %d)", dimensionId, seed);
        chunkProcessors.put(dimensionId, chunkProcessor);
        villageProcessors.put(dimensionId, new VillageProcessor(dimensionId, boundingBoxCache));
    }

    private void chunkLoaded(ChunkLoaded event) {
        AbstractChunkProcessor chunkProcessor = chunkProcessors.get(event.getDimensionId());
        if (chunkProcessor == null) return;

        chunkProcessor.process(event.getChunk());
    }

    private void playerLoggedIn(PlayerLoggedIn event) {
        if (seed == null || spawnX == null || spawnZ == null) {
            return;
        }
        ServerPlayer player = event.getPlayer();
        player.sendPacket(InitializeClient.getPayload(seed, spawnX, spawnZ));
    }

    private void playerLoggedOut(PlayerLoggedOut event) {
        ServerPlayer player = event.getPlayer();
        players.remove(player);
        playerBoundingBoxesCache.remove(player);
    }

    private void onVillageRemoved(VillageRemoved event) {
        sendRemoveBoundingBox(event.getDimensionId(), event.getVillage());
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

    private void onPlayerSubscribed(PlayerSubscribed event) {
        ServerPlayer player = event.getPlayer();
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

    protected void removeBoundingBox(int dimensionId, AbstractBoundingBox key) {
        BoundingBoxCache cache = getCache(dimensionId);
        if (cache == null) return;

        cache.removeBoundingBox(key);
    }

    private void mobSpawnerBroken(MobSpawnerBroken event) {
        int dimensionId = event.getDimensionId();
        AbstractBoundingBox boundingBox = BoundingBoxMobSpawner.from(event.getPos());
        removeBoundingBox(dimensionId, boundingBox);
        sendRemoveBoundingBox(dimensionId, boundingBox);
    }

    private void serverTick() {
        for (ServerPlayer player : players) {
            sendToPlayer(player, getCache(player.getDimensionId()));
        }
    }

    private void serverWorldTick(ServerWorldTick event) {
        VillageProcessor villageProcessor = villageProcessors.get(event.getDimensionId());
        if (villageProcessor == null) return;

        villageProcessor.process(event.getWorld());
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
