package com.irtimaled.bbor.client.events;

import java.net.InetSocketAddress;

public class ConnectedToRemoteServer {
    private final InetSocketAddress internetAddress;

    public ConnectedToRemoteServer(InetSocketAddress internetAddress) {
        this.internetAddress = internetAddress;
    }

    public InetSocketAddress getInternetAddress() {
        return internetAddress;
    }
}
