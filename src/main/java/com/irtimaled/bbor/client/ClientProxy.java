package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.events.*;
import com.irtimaled.bbor.client.gui.LoadSavesScreen;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import com.irtimaled.bbor.client.keyboard.Key;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import com.irtimaled.bbor.client.providers.*;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.EventBus;

public class ClientProxy extends CommonProxy {
    public static void registerKeyBindings() {
        Key mainKey = KeyListener.register("bbor.key.toggleActive", "key.keyboard.b")
                .onKeyPressHandler(ClientRenderer::toggleActive);
        mainKey.register("key.keyboard.g")
                .onKeyPressHandler(SettingsScreen::show);
        mainKey.register("key.keyboard.o")
                .onKeyPressHandler(() -> ConfigManager.Toggle(ConfigManager.outerBoxesOnly));
        mainKey.register("key.keyboard.l")
                .onKeyPressHandler(LoadSavesScreen::show);
    }

    public ClientProxy() {
        ConfigManager.loadConfig();
    }

    @Override
    public void init() {
        super.init();
        EventBus.subscribe(DisconnectedFromRemoteServer.class, e -> disconnectedFromServer());
        EventBus.subscribe(InitializeClientReceived.class, this::onInitializeClientReceived);
        EventBus.subscribe(AddBoundingBoxReceived.class, this::addBoundingBox);
        EventBus.subscribe(UpdateWorldSpawnReceived.class, this::onUpdateWorldSpawnReceived);
        EventBus.subscribe(SaveLoaded.class, e -> clear());

        ClientRenderer.registerProvider(new CacheProvider(this::getCache));

        KeyListener.init();
    }

    private void disconnectedFromServer() {
        ClientRenderer.deactivate();
        if (ConfigManager.keepCacheBetweenSessions.get()) return;
        clear();
    }

    private void clear() {
        SlimeChunkProvider.clear();
        WorldSpawnProvider.clear();
        SpawningSphereProvider.clear();
        CustomBeaconProvider.clear();
        CustomBoxProvider.clear();
        BiomeBorderProvider.clear();
        clearCaches();
    }

    private void addBoundingBox(AddBoundingBoxReceived event) {
        BoundingBoxCache cache = getOrCreateCache(event.getDimensionId());
        if (cache == null) return;

        cache.addBoundingBoxes(event.getKey(), event.getBoundingBoxes());
    }

    private void onInitializeClientReceived(InitializeClientReceived event) {
        setWorldSpawn(event.getSpawnX(), event.getSpawnZ());
        setSeed(event.getSeed());
    }

    private void onUpdateWorldSpawnReceived(UpdateWorldSpawnReceived event) {
        setWorldSpawn(event.getSpawnX(), event.getSpawnZ());
    }

    @Override
    protected void setSeed(long seed) {
        super.setSeed(seed);
        SlimeChunkProvider.setSeed(seed);
    }

    @Override
    protected void setWorldSpawn(int spawnX, int spawnZ) {
        super.setWorldSpawn(spawnX, spawnZ);
        WorldSpawnProvider.setWorldSpawn(spawnX, spawnZ);
    }
}
