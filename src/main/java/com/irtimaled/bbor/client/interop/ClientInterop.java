package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.Render;
import com.irtimaled.bbor.client.events.SeedCommandTyped;
import com.irtimaled.bbor.common.EventBus;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientInterop {
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
