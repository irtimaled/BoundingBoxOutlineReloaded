package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.events.*;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.VillageColorCache;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.config.ConfigManager;
import com.irtimaled.bbor.config.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static com.irtimaled.bbor.client.Constants.CHUNK_SIZE;

public class ClientProxy extends CommonProxy {
    public static final String Name = "Bounding Box Outline Reloaded";
    public static boolean active;

    static {
        KeyListener.register("Toggle Active", 0x42, Name)
                .onKeyPressHandler(ClientProxy::toggleActive)
                .onLongKeyPressHandler(60, SettingsScreen::show);
        KeyListener.register("Toggle Outer Box Only", 0x4f, Name)
                .onKeyPressHandler(ClientProxy::toggleOuterBoxesOnly);
    }

    public static void toggleActive() {
        active = !active;
        if (active)
            PlayerData.setActiveY();
    }

    private static void toggleOuterBoxesOnly() {
        Setting<Boolean> outerBoxesOnly = ConfigManager.outerBoxesOnly;
        outerBoxesOnly.set(!outerBoxesOnly.get());
    }

    private ClientRenderer renderer;

    @Override
    public void init() {
        super.init();
        EventBus.subscribe(Render.class, e -> render(e.getPartialTicks()));
        EventBus.subscribe(ConnectedToRemoteServer.class, e -> connectedToServer(e.getNetworkManager()));
        EventBus.subscribe(DisconnectedFromRemoteServer.class, e -> disconnectedFromServer());
        EventBus.subscribe(InitializeClientReceived.class, e -> setWorldData(e.getSeed(), e.getSpawnX(), e.getSpawnZ()));
        EventBus.subscribe(AddBoundingBoxReceived.class, e -> runOnCache(e.getDimensionType(), cache -> cache.addBoundingBoxes(e.getKey(), e.getBoundingBoxes())));
        EventBus.subscribe(RemoveBoundingBoxReceived.class, e -> removeBoundingBox(e.getDimensionType(), e.getKey()));

        renderer = new ClientRenderer(this::getCache);
        KeyListener.init();
    }

    private void render(float partialTicks) {
        EntityPlayer entityPlayer = Minecraft.getInstance().player;
        PlayerData.setPlayerPosition(partialTicks, entityPlayer);

        if (active) {
            renderer.render(DimensionType.getById(entityPlayer.dimension), ConfigManager.outerBoxesOnly.get());
        }
    }

    private void connectedToServer(NetworkManager networkManager) {
        SocketAddress remoteAddress = networkManager.getRemoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) remoteAddress;
            NBTFileParser.loadLocalDatFiles(socketAddress.getHostName(), socketAddress.getPort(), this::setWorldData, this::getOrCreateCache);
        }
    }

    private void disconnectedFromServer() {
        active = false;
        if (ConfigManager.keepCacheBetweenSessions.get()) return;
        VillageColorCache.clear();
        clearCaches();
    }

    @Override
    protected void setWorldData(long seed, int spawnX, int spawnZ) {
        super.setWorldData(seed, spawnX, spawnZ);
        addSpawnChunkBoundingBoxes(spawnX, spawnZ);
    }

    private void addSpawnChunkBoundingBoxes(int spawnX, int spawnZ) {
        BoundingBox worldSpawnBoundingBox = getWorldSpawnBoundingBox(spawnX, spawnZ);
        BoundingBox spawnChunksBoundingBox = buildSpawnChunksBoundingBox(spawnX, spawnZ, 12, BoundingBoxType.SpawnChunks);
        BoundingBox lazySpawnChunksBoundingBox = buildSpawnChunksBoundingBox(spawnX, spawnZ, 16, BoundingBoxType.LazySpawnChunks);

        runOnCache(DimensionType.OVERWORLD, cache -> {
            cache.addBoundingBox(worldSpawnBoundingBox);
            cache.addBoundingBox(spawnChunksBoundingBox);
            cache.addBoundingBox(lazySpawnChunksBoundingBox);
        });
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
