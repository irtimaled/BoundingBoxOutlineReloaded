package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.PayloadBuilder;
import com.irtimaled.bbor.common.messages.RemoveBoundingBox;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.ServerPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommonProxy {
    private final Map<Integer, ServerPlayer> players = new ConcurrentHashMap<>();
    private final Map<Integer, Set<AbstractBoundingBox>> playerBoundingBoxesCache = new HashMap<>();
    private final Map<Integer, VillageProcessor> villageProcessors = new HashMap<>();
    private final Map<Integer, StructureProcessor> structureProcessors = new HashMap<>();
    private final Map<Integer, BoundingBoxCache> dimensionCache = new ConcurrentHashMap<>();
    private Long seed = null;
    private Integer spawnX = null;
    private Integer spawnZ = null;

    public void init() {
        BoundingBoxType.registerTypes();
        EventBus.subscribe(WorldLoaded.class, this::worldLoaded);
        EventBus.subscribe(StructuresLoaded.class, this::structuresLoaded);
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
        if (dimensionId == Dimensions.OVERWORLD) {
            setSeed(seed);
            setWorldSpawn(event.getSpawnX(), event.getSpawnZ());
        }
        Logger.info("create world dimension: %s (seed: %d)", dimensionId, seed);
    }

    private void structuresLoaded(StructuresLoaded event) {
        int dimensionId = event.getDimensionId();
        StructureProcessor structureProcessor = getStructureProcessor(dimensionId);
        structureProcessor.process(event.getStructures());
    }

    private StructureProcessor getStructureProcessor(int dimensionId) {
        StructureProcessor structureProcessor = structureProcessors.get(dimensionId);
        if (structureProcessor == null) {
            structureProcessor = new StructureProcessor(getOrCreateCache(dimensionId));
            structureProcessors.put(dimensionId, structureProcessor);
        }
        return structureProcessor;
    }

    private void playerLoggedIn(PlayerLoggedIn event) {
        if (seed == null || spawnX == null || spawnZ == null) {
            return;
        }
        ServerPlayer player = event.getPlayer();
        player.sendPacket(InitializeClient.getPayload(seed, spawnX, spawnZ));
    }

    private void playerLoggedOut(PlayerLoggedOut event) {
        int playerId = event.getPlayerId();
        players.remove(playerId);
        playerBoundingBoxesCache.remove(playerId);
    }

    private void onVillageRemoved(VillageRemoved event) {
        sendRemoveBoundingBox(event.getDimensionId(), event.getVillage());
    }

    private void sendRemoveBoundingBox(int dimensionId, AbstractBoundingBox boundingBox) {
        PayloadBuilder payload = RemoveBoundingBox.getPayload(dimensionId, boundingBox);
        if (payload == null) return;

        for (Map.Entry<Integer, ServerPlayer> playerEntry : players.entrySet()) {
            int playerId = playerEntry.getKey();
            ServerPlayer player = playerEntry.getValue();
            if (player.getDimensionId() == dimensionId) {
                player.sendPacket(payload);

                if (playerBoundingBoxesCache.containsKey(playerId)) {
                    playerBoundingBoxesCache.get(playerId).remove(boundingBox);
                }
            }
        }
    }

    private void onPlayerSubscribed(PlayerSubscribed event) {
        int playerId = event.getPlayerId();
        ServerPlayer player = event.getPlayer();
        players.put(playerId, player);
        sendToPlayer(playerId, player);
    }

    private void sendToPlayer(int playerId, ServerPlayer player) {
        for (Map.Entry<Integer, BoundingBoxCache> entry : dimensionCache.entrySet()) {
            int dimensionId = entry.getKey();
            BoundingBoxCache boundingBoxCache = entry.getValue();
            if (boundingBoxCache == null) return;

            Set<AbstractBoundingBox> playerBoundingBoxes = playerBoundingBoxesCache.computeIfAbsent(playerId, k -> new HashSet<>());

            Map<AbstractBoundingBox, Set<AbstractBoundingBox>> boundingBoxMap = boundingBoxCache.getBoundingBoxes();
            for (AbstractBoundingBox key : boundingBoxMap.keySet()) {
                if (playerBoundingBoxes.contains(key)) {
                    continue;
                }

                Set<AbstractBoundingBox> boundingBoxes = boundingBoxMap.get(key);
                PayloadBuilder payload = AddBoundingBox.getPayload(dimensionId, key, boundingBoxes);
                if (payload != null)
                    player.sendPacket(payload);

                playerBoundingBoxes.add(key);
            }
        }
    }

    protected void removeBoundingBox(int dimensionId, AbstractBoundingBox key) {
        BoundingBoxCache cache = getCache(dimensionId);
        if (cache == null) return;

        cache.removeBoundingBox(key);
    }

    private void serverTick() {
        for (Map.Entry<Integer, ServerPlayer> playerEntry : players.entrySet()) {
            int playerId = playerEntry.getKey();
            ServerPlayer player = playerEntry.getValue();

            sendToPlayer(playerId, player);
        }
    }

    private void serverWorldTick(ServerWorldTick event) {
        int dimensionId = event.getDimensionId();
        VillageProcessor villageProcessor = villageProcessors.get(dimensionId);
        if (villageProcessor == null) {
            villageProcessor = new VillageProcessor(dimensionId, getOrCreateCache(dimensionId));
            villageProcessors.put(dimensionId, villageProcessor);
        }

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
        structureProcessors.clear();
        for (BoundingBoxCache cache : dimensionCache.values()) {
            cache.clear();
        }
        dimensionCache.clear();
    }
}
