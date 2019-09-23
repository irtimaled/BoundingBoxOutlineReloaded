package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.events.*;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import com.irtimaled.bbor.client.keyboard.Key;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.VillageColorCache;
import com.irtimaled.bbor.config.ConfigManager;

public class ClientProxy extends CommonProxy {
    static {
        Key mainKey = KeyListener.register("Toggle Active", "key.keyboard.b")
                .onKeyPressHandler(ClientRenderer::toggleActive);
        mainKey.register("key.keyboard.g")
                .onKeyPressHandler(SettingsScreen::show);
        mainKey.register("key.keyboard.o")
                .onKeyPressHandler(() -> ConfigManager.Toggle(ConfigManager.outerBoxesOnly));
    }

    private boolean ready;

    private ClientRenderer renderer;

    @Override
    public void init() {
        super.init();
        EventBus.subscribe(Render.class, this::render);
        EventBus.subscribe(DisconnectedFromRemoteServer.class, e -> disconnectedFromServer());
        EventBus.subscribe(InitializeClientReceived.class, this::onInitializeClientReceived);
        EventBus.subscribe(AddBoundingBoxReceived.class, this::addBoundingBox);
        EventBus.subscribe(RemoveBoundingBoxReceived.class, this::onRemoveBoundingBoxReceived);
        EventBus.subscribe(UpdateWorldSpawnReceived.class, this::onUpdateWorldSpawnReceived);
        EventBus.subscribe(SeedCommandTyped.class, this::onSeedCommandTyped);

        renderer = new ClientRenderer(this::getCache);
        KeyListener.init();
    }

    private void render(Render event) {
        if (!ready) return;

        renderer.render(event.getDimensionId());
    }

    private void disconnectedFromServer() {
        ClientRenderer.deactivate();
        if (ConfigManager.keepCacheBetweenSessions.get()) return;
        ready = false;
        VillageColorCache.clear();
        clearCaches();
        renderer.clear();
    }

    private void addBoundingBox(AddBoundingBoxReceived event) {
        BoundingBoxCache cache = getOrCreateCache(event.getDimensionId());
        if (cache == null) return;

        cache.addBoundingBoxes(event.getKey(), event.getBoundingBoxes());
    }

    private void onRemoveBoundingBoxReceived(RemoveBoundingBoxReceived event) {
        super.removeBoundingBox(event.getDimensionId(), event.getKey());
    }

    private void onInitializeClientReceived(InitializeClientReceived event) {
        setWorldSpawn(event.getSpawnX(), event.getSpawnZ());
        setSeed(event.getSeed());
    }

    private void onUpdateWorldSpawnReceived(UpdateWorldSpawnReceived event) {
        setWorldSpawn(event.getSpawnX(), event.getSpawnZ());
    }

    private void onSeedCommandTyped(SeedCommandTyped event) {
        setSeed(event.getSeed());
    }

    @Override
    public void setSeed(long seed) {
        super.setSeed(seed);
        renderer.setSeed(seed);
        ready = true;
    }

    @Override
    protected void setWorldSpawn(int spawnX, int spawnZ) {
        super.setWorldSpawn(spawnX, spawnZ);
        renderer.setWorldSpawn(spawnX, spawnZ);
    }
}
