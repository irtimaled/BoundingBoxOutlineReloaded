package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.events.*;
import com.irtimaled.bbor.common.*;
import com.irtimaled.bbor.common.events.Tick;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.dimension.DimensionType;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

public class ClientProxy extends CommonProxy {
    public static final String KeyCategory = "Bounding Box Outline Reloaded";
    public static KeyBinding ActiveHotKey = new KeyBinding("Toggle On/Off", 0x42, KeyCategory);
    public static KeyBinding OuterBoxOnlyHotKey = new KeyBinding("Toggle Display Outer Box Only", 0x4f, KeyCategory);

    private boolean active;
    private boolean outerBoxOnly;
    private ClientRenderer renderer;

    @Override
    public void init() {
        registerEventHandlers();
        ClientDimensionCache clientDimensionCache = new ClientDimensionCache();
        renderer = new ClientRenderer(clientDimensionCache);
        dimensionCache = clientDimensionCache;
    }

    @Override
    protected void registerEventHandlers() {
        super.registerEventHandlers();
        EventBus.subscribe(Render.class, e -> render(e.getPartialTicks()));
        EventBus.subscribe(KeyPressed.class, e -> keyPressed());
        EventBus.subscribe(ConnectedToRemoteServer.class, e -> connectedToServer(e.getNetworkManager()));
        EventBus.subscribe(DisconnectedFromRemoteServer.class, e -> disconnectedFromServer());
        EventBus.subscribe(InitializeClientReceived.class, e -> dimensionCache.setWorldData(e.getSeed(), e.getSpawnX(), e.getSpawnZ()));
        EventBus.subscribe(AddBoundingBoxReceived.class, e -> addBoundingBox(e.getDimensionType(), e.getKey(), e.getBoundingBoxes()));
        EventBus.subscribe(RemoveBoundingBoxReceived.class, e -> removeBoundingBox(e.getDimensionType(), e.getKey()));
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
            NBTFileParser.loadLocalDatFiles(socketAddress.getHostName(), socketAddress.getPort(), dimensionCache);
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
}