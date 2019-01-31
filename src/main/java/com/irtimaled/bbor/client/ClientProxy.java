package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.VillageColorCache;
import com.irtimaled.bbor.common.VillageProcessor;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientProxy extends CommonProxy {
    private boolean active;
    private boolean outerBoxOnly;
    private ClientRenderer renderer;
    private int remoteUserCount = 0;

    public void toggleActive() {
        active = !active;
        if (active)
            PlayerData.setActiveY();
    }

    public void toggleOuterBoxOnly() {
        outerBoxOnly = !outerBoxOnly;
    }

    @Override
    public void init() {
        ClientDimensionCache clientDimensionCache = new ClientDimensionCache();
        renderer = new ClientRenderer(clientDimensionCache);
        dimensionCache = clientDimensionCache;
    }

    public void render(float partialTicks) {
        EntityPlayer entityPlayer = Minecraft.getInstance().player;
        PlayerData.setPlayerPosition(partialTicks, entityPlayer);

        if (this.active) {
            renderer.render(entityPlayer.dimension, outerBoxOnly);
        }
    }

    public void setRemoteUserCount(int remoteUserCount) {
        this.remoteUserCount = remoteUserCount;
    }

    protected boolean hasRemoteUsers()    {
        return remoteUserCount > 0;
    }

    @Override
    public void tick() {
        if (this.active || this.hasRemoteUsers()) {
            super.tick();
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
        villageProcessors.forEach(VillageProcessor::close);
        villageProcessors.clear();

        if (ConfigManager.keepCacheBetweenSessions.getBoolean()) return;
        VillageColorCache.clear();
        dimensionCache.clear();
    }
}
