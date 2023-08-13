package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.events.AddBoundingBoxReceived;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.GameJoin;
import com.irtimaled.bbor.client.events.InitializeClientReceived;
import com.irtimaled.bbor.client.events.SaveLoaded;
import com.irtimaled.bbor.client.events.UpdateWorldSpawnReceived;
import com.irtimaled.bbor.client.gui.LoadSavesScreen;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import com.irtimaled.bbor.client.keyboard.Key;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import com.irtimaled.bbor.client.providers.CacheProvider;
import com.irtimaled.bbor.client.providers.SlimeChunkProvider;
import com.irtimaled.bbor.client.providers.WorldSpawnProvider;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.servux.RegistryUtil;
import com.irtimaled.bbor.common.messages.servux.ServuxStructurePackets;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.mixin.access.IKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientProxy extends CommonProxy {
    public static void registerKeyBindings() {
        IKeyBinding.getCATEGORY_ORDER_MAP().put(KeyListener.Category, 1000);
        Key mainKey = KeyListener.register("bbor.key.toggleActive", "key.keyboard.b")
                .onKeyPressHandler(ClientRenderer::toggleActive);
        mainKey.register("key.keyboard.g")
                .onKeyPressHandler(SettingsScreen::show);
        mainKey.register("key.keyboard.o")
                .onKeyPressHandler(() -> ConfigManager.Toggle(ConfigManager.outerBoxesOnly));
        mainKey.register("key.keyboard.l")
                .onKeyPressHandler(LoadSavesScreen::show);
    }

    private final Map<BoundingBoxCache.Type, Map<DimensionId, BoundingBoxCache>> cache = new ConcurrentHashMap<>();

    public ClientProxy() {
        ConfigManager.loadConfig();
        CommonInterop.loadStructuresInitial();
    }

    @Override
    public void init() {
        super.init();
        EventBus.subscribe(DisconnectedFromRemoteServer.class, e -> disconnectedFromServer());
        EventBus.subscribe(InitializeClientReceived.class, this::onInitializeClientReceived);
        EventBus.subscribe(AddBoundingBoxReceived.class, this::addBoundingBox);
        EventBus.subscribe(UpdateWorldSpawnReceived.class, this::onUpdateWorldSpawnReceived);
        EventBus.subscribe(SaveLoaded.class, e -> clear());
        EventBus.subscribe(GameJoin.class, e -> {
            final ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) {
                System.err.println("network handler is null");
                return;
            }

            if (!ConfigManager.keepCacheBetweenSessions.get()) {
                clear();
            }
            networkHandler.sendPacket(ServuxStructurePackets.subscribe().build());
        });

        ClientRenderer.registerProvider(new CacheProvider(this::getOrCreateClientCache));

        TaskThread.init();
//        KeyListener.init();
        registerKeyBindings();
        MinecraftForge.EVENT_BUS.register(new Object(){

            @SubscribeEvent
            public void registerBindings(RegisterKeyMappingsEvent event) {
                registerKeyBindings();
                for (KeyBinding keyBinding : KeyListener.keyBindings()) {
                    event.register(keyBinding);
                }
            }

        });

        System.out.println("BBOR Dynamic Registry loading");
        RegistryUtil.init();
        System.out.println("BBOR Dynamic Registry loaded");
    }

    private void disconnectedFromServer() {
        ClientRenderer.deactivate();
        if (ConfigManager.keepCacheBetweenSessions.get()) return;
        clear();
    }

    private void clear() {
        ClientRenderer.clear();
        clearCaches();
        for (Map.Entry<BoundingBoxCache.Type, Map<DimensionId, BoundingBoxCache>> entry : cache.entrySet()) {
            for (Map.Entry<DimensionId, BoundingBoxCache> cacheEntry : entry.getValue().entrySet()) {
                cacheEntry.getValue().clear();
            }
        }
        ServuxStructurePackets.markUnregistered();
    }

    private BoundingBoxCache getOrCreateClientCache(BoundingBoxCache.Type type, DimensionId dimensionId) {
        if (type == BoundingBoxCache.Type.LOCAL) {
            return super.getOrCreateCache(dimensionId);
        }
        return this.cache
                .computeIfAbsent(type, unused -> new ConcurrentHashMap<>())
                .computeIfAbsent(dimensionId, unused -> new BoundingBoxCache());
    }

    private void addBoundingBox(AddBoundingBoxReceived event) {
        if (event.getType() == BoundingBoxCache.Type.LOCAL) {
            new IllegalArgumentException("Received local bounding box from server").printStackTrace();
            return;
        }
        BoundingBoxCache cache = getOrCreateClientCache(event.getType(), event.getDimensionId());
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
//        FlowerForestHelper.setSeed(seed);
    }

    @Override
    protected void setWorldSpawn(int spawnX, int spawnZ) {
        super.setWorldSpawn(spawnX, spawnZ);
        WorldSpawnProvider.setWorldSpawn(spawnX, spawnZ);
    }
}
