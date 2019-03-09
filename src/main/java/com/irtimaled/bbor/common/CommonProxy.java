package com.irtimaled.bbor.common;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.common.chunkProcessors.ChunkProcessor;
import com.irtimaled.bbor.common.chunkProcessors.EndChunkProcessor;
import com.irtimaled.bbor.common.chunkProcessors.NetherChunkProcessor;
import com.irtimaled.bbor.common.chunkProcessors.OverworldChunkProcessor;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.RemoveBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import com.irtimaled.bbor.common.models.WorldData;
import com.irtimaled.bbor.config.ConfigManager;
import io.netty.channel.local.LocalAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
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
import java.util.function.Consumer;

public class CommonProxy {
    private Set<EntityPlayerMP> players = new HashSet<>();
    private Map<EntityPlayerMP, Set<BoundingBox>> playerBoundingBoxesCache = new HashMap<>();
    private Map<Integer, BoundingBoxVillage> villageCache = new HashMap<>();
    private Map<DimensionType, ChunkProcessor> chunkProcessors = new HashMap<>();
    private WorldData worldData = null;
    private final Map<DimensionType, BoundingBoxCache> dimensionCache = new ConcurrentHashMap<>();

    public void init() {
        EventBus.subscribe(WorldLoaded.class, e -> worldLoaded(e.getWorld()));
        EventBus.subscribe(ChunkLoaded.class, e -> chunkLoaded(e.getChunk()));
        EventBus.subscribe(MobSpawnerBroken.class, e -> mobSpawnerBroken(e.getDimensionType(), e.getPos()));
        EventBus.subscribe(PlayerLoggedIn.class, e -> playerLoggedIn(e.getPlayer()));
        EventBus.subscribe(PlayerLoggedOut.class, e -> playerLoggedOut(e.getPlayer()));
        EventBus.subscribe(PlayerSubscribed.class, e -> sendBoundingBoxes(e.getPlayer()));
        EventBus.subscribe(Tick.class, e -> tick());
        if (ConfigManager.drawVillages.get()) {
            EventBus.subscribe(VillageUpdated.class, e -> villageUpdated(e.getDimensionType(), e.getVillage()));
        }
    }

    protected void setWorldData(long seed, int spawnX, int spawnZ) {
        worldData = new WorldData(seed, spawnX, spawnZ);
    }

    private void worldLoaded(World world) {
        IChunkProvider chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof ChunkProviderServer) {
            DimensionType dimensionType = world.dimension.getType();
            BoundingBoxCache boundingBoxCache = getOrCreateCache(dimensionType);
            ChunkProcessor chunkProcessor = null;
            if (dimensionType == DimensionType.OVERWORLD) {
                setWorldData(world.getSeed(), world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnZ());
                chunkProcessor = new OverworldChunkProcessor(boundingBoxCache);
            }
            if (dimensionType == DimensionType.NETHER) {
                chunkProcessor = new NetherChunkProcessor(boundingBoxCache);
            }
            if (dimensionType == DimensionType.THE_END) {
                chunkProcessor = new EndChunkProcessor(boundingBoxCache);
            }
            Logger.info("create world dimension: %s, %s (seed: %d)", dimensionType, world.getClass().toString(), world.getSeed());
            chunkProcessors.put(dimensionType, chunkProcessor);
        }
    }

    private void chunkLoaded(Chunk chunk) {
        DimensionType dimensionType = chunk.getWorld().dimension.getType();
        ChunkProcessor chunkProcessor = chunkProcessors.get(dimensionType);
        if (chunkProcessor != null) {
            chunkProcessor.process(chunk);
        }
    }

    private void playerLoggedIn(EntityPlayerMP player) {
        if (player.connection.netManager.getRemoteAddress() instanceof LocalAddress) return;
        player.connection.sendPacket(InitializeClient.getPayload(worldData));
    }

    private void playerLoggedOut(EntityPlayerMP player) {
        players.remove(player);
        playerBoundingBoxesCache.remove(player);
    }

    private void sendRemoveBoundingBox(DimensionType dimensionType, BoundingBox boundingBox) {
        SPacketCustomPayload payload = RemoveBoundingBox.getPayload(dimensionType, boundingBox);
        if (payload == null) return;

        for (EntityPlayerMP player : players) {
            if (DimensionType.getById(player.dimension) == dimensionType) {
                player.connection.sendPacket(payload);

                if (playerBoundingBoxesCache.containsKey(player)) {
                    playerBoundingBoxesCache.get(player).remove(boundingBox);
                }
            }
        }
    }

    private void sendBoundingBoxes(EntityPlayerMP player) {
        DimensionType dimensionType = DimensionType.getById(player.dimension);
        players.add(player);
        sendToPlayer(player, getCache(dimensionType));
    }

    private void sendToPlayer(EntityPlayerMP player, BoundingBoxCache boundingBoxCache) {
        if (boundingBoxCache == null) return;

        Map<BoundingBox, Set<BoundingBox>> cacheSubset = getBoundingBoxMap(player, boundingBoxCache.getBoundingBoxes());

        DimensionType dimensionType = DimensionType.getById(player.dimension);

        for (BoundingBox key : cacheSubset.keySet()) {
            Set<BoundingBox> boundingBoxes = cacheSubset.get(key);
            SPacketCustomPayload payload = AddBoundingBox.getPayload(dimensionType, key, boundingBoxes);
            if (payload != null)
                player.connection.sendPacket(payload);

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
        BoundingBoxCache cache = getCache(dimensionType);
        if (cache == null) return;

        cache.removeBoundingBox(key);
    }

    private void mobSpawnerBroken(DimensionType dimensionType, BlockPos pos) {
        BoundingBox boundingBox = BoundingBoxMobSpawner.from(pos);
        removeBoundingBox(dimensionType, boundingBox);
        sendRemoveBoundingBox(dimensionType, boundingBox);
    }

    private void tick() {
        for (EntityPlayerMP player : players) {
            DimensionType dimensionType = DimensionType.getById(player.dimension);
            sendToPlayer(player, getCache(dimensionType));
        }
    }

    private void villageUpdated(DimensionType dimensionType, Village village) {
        BoundingBoxCache cache = getCache(dimensionType);
        if (cache == null) return;

        int villageId = village.hashCode();
        BoundingBoxVillage oldVillage = villageCache.get(villageId);
        if (oldVillage != null && !oldVillage.matches(village)) {
            cache.removeBoundingBox(oldVillage);
            sendRemoveBoundingBox(dimensionType, oldVillage);
            oldVillage = null;
        }
        if (village.isAnnihilated()) {
            villageCache.remove(villageId);
        } else {
            BoundingBoxVillage newVillage = oldVillage == null ? BoundingBoxVillage.from(village) : oldVillage;
            cache.addBoundingBox(newVillage);
            villageCache.put(villageId, newVillage);
        }
    }

    protected BoundingBoxCache getCache(DimensionType dimensionType) {
        return dimensionCache.get(dimensionType);
    }

    protected BoundingBoxCache getOrCreateCache(DimensionType dimensionType) {
        return dimensionCache.computeIfAbsent(dimensionType, dt -> new BoundingBoxCache());
    }

    protected void runOnCache(DimensionType dimensionType, Consumer<BoundingBoxCache> action) {
        action.accept(getOrCreateCache(dimensionType));
    }

    protected void clearCaches() {
        villageCache.clear();
        for (BoundingBoxCache cache : dimensionCache.values()) {
            cache.close();
        }
        dimensionCache.clear();
    }
}
