package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.Render;
import com.irtimaled.bbor.client.events.UpdateWorldSpawnReceived;
import com.irtimaled.bbor.client.providers.SlimeChunkProvider;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.TypeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

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
                SlimeChunkProvider.setSeed(seed);
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

    public static int getRenderDistanceChunks() {
        return Minecraft.getInstance().gameSettings.renderDistanceChunks;
    }

    public static void handleSeedMessage(ITextComponent chatComponent) {
        TypeHelper.doIfType(chatComponent, TextComponentTranslation.class, message -> {
            if (!message.getKey().equals("commands.seed.success")) return;

            try {
                long seed = Long.parseLong(message.getFormatArgs()[0].toString());
                SlimeChunkProvider.setSeed(seed);
            } catch (Exception ignored) {
            }
        });
    }
}
