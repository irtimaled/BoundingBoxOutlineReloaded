package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.common.events.DataPackReloaded;
import com.irtimaled.bbor.common.events.PlayerLoggedIn;
import com.irtimaled.bbor.common.events.PlayerLoggedOut;
import com.irtimaled.bbor.common.events.PlayerSubscribed;
import com.irtimaled.bbor.common.events.ServerTick;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import com.irtimaled.bbor.common.events.WorldLoaded;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.PayloadBuilder;
import com.irtimaled.bbor.common.messages.StructureListSync;
import com.irtimaled.bbor.common.messages.servux.RegistryUtil;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CommonProxy {
    private final Map<Integer, ServerPlayer> players = new ConcurrentHashMap<>();
    private final Map<Integer, Set<AbstractBoundingBox>> playerBoundingBoxesCache = new HashMap<>();
    private final Map<DimensionId, StructureProcessor> structureProcessors = new HashMap<>();
    private final Map<DimensionId, BoundingBoxCache> dimensionCache = new ConcurrentHashMap<>();
    private Long seed = null;
    private Integer spawnX = null;
    private Integer spawnZ = null;

    public CommonProxy(){
        ConfigManager.loadConfig();
    }

    public void init() {
        BoundingBoxType.registerTypes();
        EventBus.subscribe(WorldLoaded.class, this::worldLoaded);
        EventBus.subscribe(StructuresLoaded.class, this::structuresLoaded);
        EventBus.subscribe(PlayerLoggedIn.class, this::playerLoggedIn);
        EventBus.subscribe(PlayerLoggedOut.class, this::playerLoggedOut);
        EventBus.subscribe(PlayerSubscribed.class, this::onPlayerSubscribed);
        EventBus.subscribe(ServerTick.class, e -> serverTick());
        EventBus.subscribe(DataPackReloaded.class, e -> dataPackReloaded());
        CompletableFuture.runAsync(RegistryUtil::init).thenRun(() -> System.out.println("BBOR Dynamic Registry loaded"));
    }

    protected void setSeed(long seed) {
        this.seed = seed;
    }

    protected void setWorldSpawn(int spawnX, int spawnZ) {
        this.spawnX = spawnX;
        this.spawnZ = spawnZ;
    }

    private void worldLoaded(WorldLoaded event) {
        DimensionId dimensionId = event.getDimensionId();
        long seed = event.getSeed();
        if (dimensionId == DimensionId.OVERWORLD) {
            setSeed(seed);
            setWorldSpawn(event.getSpawnX(), event.getSpawnZ());
        }
        Logger.info("create world dimension: %s (seed: %d)", dimensionId, seed);
    }

    private void structuresLoaded(StructuresLoaded event) {
        DimensionId dimensionId = event.getDimensionId();
        StructureProcessor structureProcessor = getStructureProcessor(dimensionId);
        structureProcessor.process(event.getStructures());
    }

    private StructureProcessor getStructureProcessor(DimensionId dimensionId) {
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

    private void onPlayerSubscribed(PlayerSubscribed event) {
        int playerId = event.getPlayerId();
        ServerPlayer player = event.getPlayer();
        players.put(playerId, player);
        player.sendPacket(StructureListSync.getPayload());
        sendToPlayer(playerId, player);
    }

    private void sendToPlayer(int playerId, ServerPlayer player) {
        for (Map.Entry<DimensionId, BoundingBoxCache> entry : dimensionCache.entrySet()) {
            DimensionId dimensionId = entry.getKey();
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

    private void dataPackReloaded() {
        for (Map.Entry<Integer, ServerPlayer> playerEntry : players.entrySet()) {
            playerEntry.getValue().sendPacket(StructureListSync.getPayload());
        }
    }

    private void serverTick() {
        for (Map.Entry<Integer, ServerPlayer> playerEntry : players.entrySet()) {
            int playerId = playerEntry.getKey();
            ServerPlayer player = playerEntry.getValue();

            sendToPlayer(playerId, player);
        }
    }

    protected BoundingBoxCache getCache(DimensionId dimensionId) {
        return dimensionCache.get(dimensionId);
    }

    protected BoundingBoxCache getOrCreateCache(DimensionId dimensionId) {
        return dimensionCache.computeIfAbsent(dimensionId, dt -> new BoundingBoxCache());
    }

    protected void clearCaches() {
        structureProcessors.clear();
        for (BoundingBoxCache cache : dimensionCache.values()) {
            cache.clear();
        }
        dimensionCache.clear();
    }
}
