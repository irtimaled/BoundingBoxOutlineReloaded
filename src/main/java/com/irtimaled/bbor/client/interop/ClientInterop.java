package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.client.events.ConnectedToRemoteServer;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.Render;
import com.irtimaled.bbor.client.events.SeedCommandTyped;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.TypeHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.NetworkManager;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientInterop {
    public static void connectedToRemoteServer(NetworkManager networkManager) {
        SocketAddress remoteAddress = networkManager.getRemoteAddress();
        TypeHelper.doIfType(remoteAddress, InetSocketAddress.class, inetSocketAddress -> EventBus.publish(new ConnectedToRemoteServer(inetSocketAddress)));
    }

    public static void disconnectedFromRemoteServer() {
        EventBus.publish(new DisconnectedFromRemoteServer());
    }

    public static void render(float partialTicks, EntityPlayerSP player) {
        PlayerCoords.setPlayerPosition(partialTicks, player);
        EventBus.publish(new Render(player.dimension));
    }

    public static boolean interceptChatMessage(String message) {
        if (message.startsWith("/bbor:seed")) {
            if (message.length() > 11) {
                String argument = message.substring(11);
                Long seed = parseNumericSeed(argument);
                if (seed == null) {
                    seed = (long) argument.hashCode();
                }
                EventBus.publish(new SeedCommandTyped(seed));
            }
            return true;
        }
        return false;
    }

    private static Long parseNumericSeed(String argument) {
        try {
            return Long.parseLong(argument);
        } catch (final NumberFormatException ex) {
            return null;
        }
    }
}
