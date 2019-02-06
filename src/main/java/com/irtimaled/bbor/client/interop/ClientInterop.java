package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.Render;
import com.irtimaled.bbor.client.events.SeedCommandTyped;
import com.irtimaled.bbor.client.events.UpdateWorldSpawnReceived;
import com.irtimaled.bbor.common.EventBus;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ClientInterop {
    public static void disconnectedFromRemoteServer() {
        EventBus.publish(new DisconnectedFromRemoteServer());
    }

    public static void render(float partialTicks, ClientPlayerEntity player) {
        PlayerCoords.setPlayerPosition(partialTicks, player);
        EventBus.publish(new Render(player.dimension.getId()));
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

    public static void updateWorldSpawnReceived(BlockPos blockPos) {
        EventBus.publish(new UpdateWorldSpawnReceived(blockPos.getX(), blockPos.getZ()));
    }
}
