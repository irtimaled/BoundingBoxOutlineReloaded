package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.events.*;
import com.irtimaled.bbor.common.*;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

import static com.irtimaled.bbor.client.Constants.CHUNK_SIZE;

public class ClientProxy extends CommonProxy {
    public static final String KeyCategory = "Bounding Box Outline Reloaded";
    public static KeyBinding ActiveHotKey = new KeyBinding("Toggle On/Off", 0x42, KeyCategory);
    public static KeyBinding OuterBoxOnlyHotKey = new KeyBinding("Toggle Display Outer Box Only", 0x4f, KeyCategory);

    private boolean active;
    private boolean outerBoxOnly;
    private ClientRenderer renderer;

    @Override
    public void init() {
        super.init();
        EventBus.subscribe(Render.class, e -> render(e.getPartialTicks()));
        EventBus.subscribe(KeyPressed.class, e -> keyPressed());
        EventBus.subscribe(ConnectedToRemoteServer.class, e -> connectedToServer(e.getNetworkManager()));
        EventBus.subscribe(DisconnectedFromRemoteServer.class, e -> disconnectedFromServer());
        EventBus.subscribe(InitializeClientReceived.class, e -> setWorldData(e.getSeed(), e.getSpawnX(), e.getSpawnZ()));
        EventBus.subscribe(AddBoundingBoxReceived.class, e -> addBoundingBox(e.getDimensionType(), e.getKey(), e.getBoundingBoxes()));
        EventBus.subscribe(RemoveBoundingBoxReceived.class, e -> removeBoundingBox(e.getDimensionType(), e.getKey()));

        renderer = new ClientRenderer(dimensionCache);
    }

    private void render(float partialTicks) {
        EntityPlayer entityPlayer = Minecraft.getInstance().player;
        PlayerData.setPlayerPosition(partialTicks, entityPlayer);

        if (this.active) {
            renderer.render(DimensionType.getById(entityPlayer.dimension), outerBoxOnly);
        }
    }

    private void keyPressed() {
        if (ActiveHotKey.isPressed()) {
            active = !active;
            if (active)
                PlayerData.setActiveY();
        } else if (OuterBoxOnlyHotKey.isPressed()) {
            outerBoxOnly = !outerBoxOnly;
        }
    }

    private void connectedToServer(NetworkManager networkManager) {
        SocketAddress remoteAddress = networkManager.getRemoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) remoteAddress;
            NBTFileParser.loadLocalDatFiles(socketAddress.getHostName(), socketAddress.getPort(), dimensionCache, this::setWorldData);
        }
    }

    private void disconnectedFromServer() {
        active = false;
        if (ConfigManager.keepCacheBetweenSessions.getBoolean()) return;
        VillageColorCache.clear();
        clearCaches();
    }

    private void addBoundingBox(DimensionType dimensionType, BoundingBox key, Set<BoundingBox> boundingBoxes) {
        BoundingBoxCache cache = dimensionCache.get(dimensionType);
        if (cache == null) {
            dimensionCache.put(dimensionType, cache = new BoundingBoxCache());
        }

        cache.addBoundingBoxes(key, boundingBoxes);
    }

    @Override
    protected void setWorldData(long seed, int spawnX, int spawnZ) {
        super.setWorldData(seed, spawnX, spawnZ);
        addSpawnChunkBoundingBoxes(spawnX, spawnZ);
    }

    private void addSpawnChunkBoundingBoxes(int spawnX, int spawnZ) {
        BoundingBoxCache cache = dimensionCache.get(DimensionType.OVERWORLD);
        if (cache == null) return;

        cache.addBoundingBox(getWorldSpawnBoundingBox(spawnX, spawnZ));
        cache.addBoundingBox(buildSpawnChunksBoundingBox(spawnX, spawnZ, 12, BoundingBoxType.SpawnChunks));
        cache.addBoundingBox(buildSpawnChunksBoundingBox(spawnX, spawnZ, 16, BoundingBoxType.LazySpawnChunks));
    }

    private BoundingBox getWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        BlockPos minBlockPos = new BlockPos(spawnX - 10, 0, spawnZ - 10);
        BlockPos maxBlockPos = new BlockPos(spawnX + 10, 0, spawnZ + 10);

        return BoundingBoxWorldSpawn.from(minBlockPos, maxBlockPos, BoundingBoxType.WorldSpawn);
    }

    private BoundingBox buildSpawnChunksBoundingBox(int spawnX, int spawnZ, int size, BoundingBoxType type) {
        double midOffset = CHUNK_SIZE * (size / 2.0);
        double midX = Math.round((float) (spawnX / (double) CHUNK_SIZE)) * (double) CHUNK_SIZE;
        double midZ = Math.round((float) (spawnZ / (double) CHUNK_SIZE)) * (double) CHUNK_SIZE;
        BlockPos minBlockPos = new BlockPos(midX - midOffset, 0, midZ - midOffset);
        if (spawnX / (double) CHUNK_SIZE % 0.5D == 0.0D && spawnZ / (double) CHUNK_SIZE % 0.5D == 0.0D) {
            midX += (double) CHUNK_SIZE;
            midZ += (double) CHUNK_SIZE;
        }
        BlockPos maxBlockPos = new BlockPos(midX + midOffset, 0, midZ + midOffset);
        return BoundingBoxWorldSpawn.from(minBlockPos, maxBlockPos, type);
    }
}