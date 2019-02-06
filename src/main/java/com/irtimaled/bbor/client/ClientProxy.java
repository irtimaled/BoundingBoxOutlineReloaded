package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.events.*;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.config.ConfigManager;
import com.irtimaled.bbor.config.Setting;

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

    private boolean ready;

    public static void toggleActive() {
        active = !active;
        if (active)
            PlayerCoords.setActiveY();
    }

    private static void toggleOuterBoxesOnly() {
        Setting<Boolean> outerBoxesOnly = ConfigManager.outerBoxesOnly;
        outerBoxesOnly.set(!outerBoxesOnly.get());
    }

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
        if (!active || !ready) return;

        renderer.render(event.getDimensionId(), ConfigManager.outerBoxesOnly.get());
    }

    private void disconnectedFromServer() {
        active = false;
        if (ConfigManager.keepCacheBetweenSessions.get()) return;
        ready = false;
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
