package com.irtimaled.bbor.client.events;

import net.minecraft.network.NetworkManager;

public class ConnectedToRemoteServer {
    private final NetworkManager networkManager;

    public ConnectedToRemoteServer(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }
}
