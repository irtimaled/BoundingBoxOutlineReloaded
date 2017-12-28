package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.VillageColorCache;
import com.irtimaled.bbor.common.VillageProcessor;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.DimensionType;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientProxy extends CommonProxy {
    private boolean active;
    private boolean outerBoxOnly;
    private KeyBinding activeHotKey;
    private KeyBinding outerBoxOnlyHotKey;
    private ClientRenderer renderer;

    public void keyPressed() {
        if (activeHotKey.isPressed()) {
            active = !active;
            if (active)
                PlayerData.setActiveY();
        } else if (outerBoxOnlyHotKey.isPressed()) {
            outerBoxOnly = !outerBoxOnly;
        }
    }

    @Override
    public void init() {
        String category = "Bounding Box Outline Reloaded";
        activeHotKey = new KeyBinding("Toggle On/Off", Keyboard.KEY_B, category);
        outerBoxOnlyHotKey = new KeyBinding("Toggle Display Outer Box Only", Keyboard.KEY_O, category);
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.addAll(Minecraft.getMinecraft().gameSettings.keyBindings, activeHotKey, outerBoxOnlyHotKey);
        ClientDimensionCache clientDimensionCache = new ClientDimensionCache();
        renderer = new ClientRenderer(clientDimensionCache);
        dimensionCache = clientDimensionCache;
    }

    public void render(float partialTicks) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().player;
        PlayerData.setPlayerPosition(partialTicks, entityPlayer);

        if (this.active) {
            renderer.render(DimensionType.getById(entityPlayer.dimension), outerBoxOnly);
        }
    }

    public void playerConnectedToServer(NetworkManager networkManager) {
        SocketAddress remoteAddress = networkManager.getRemoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) remoteAddress;
            NBTFileParser.loadLocalDatFiles(socketAddress.getHostName(), socketAddress.getPort(), dimensionCache);
        }
    }

    public void playerDisconnectedFromServer() {
        active = false;
        if (ConfigManager.keepCacheBetweenSessions.getBoolean()) return;
        VillageColorCache.clear();
        dimensionCache.clear();
        villageProcessors.forEach(VillageProcessor::clear);
    }
}
