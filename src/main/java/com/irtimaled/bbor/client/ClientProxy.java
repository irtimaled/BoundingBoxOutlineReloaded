package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.events.ConnectedToRemoteServer;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.KeyPressed;
import com.irtimaled.bbor.client.events.RenderEvent;
import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.VillageColorCache;
import com.irtimaled.bbor.common.VillageProcessor;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.dimension.DimensionType;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
    }

    private void render(float partialTicks) {
        EntityPlayer entityPlayer = Minecraft.getInstance().player;
        PlayerData.setPlayerPosition(partialTicks, entityPlayer);

        if (this.active) {
            tick();
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
        villageProcessors.forEach(VillageProcessor::close);
        villageProcessors.clear();

        if (ConfigManager.keepCacheBetweenSessions.getBoolean()) return;
        VillageColorCache.clear();
        dimensionCache.clear();
    }
}