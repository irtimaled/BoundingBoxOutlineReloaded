package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.client.commands.BeaconCommand;
import com.irtimaled.bbor.client.commands.BoxCommand;
import com.irtimaled.bbor.client.commands.SeedCommand;
import com.irtimaled.bbor.client.commands.SpawningSphereCommand;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.Render;
import com.irtimaled.bbor.client.events.UpdateWorldSpawnReceived;
import com.irtimaled.bbor.client.providers.SlimeChunkProvider;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.TypeHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.ISuggestionProvider;
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
        if (message.startsWith("/bbor:")) {
            NetHandlerPlayClient connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                CommandDispatcher<ISuggestionProvider> commandDispatcher = connection.func_195515_i();
                try {
                    commandDispatcher.execute(message.substring(1), Minecraft.getInstance().player.getCommandSource());
                } catch (CommandSyntaxException ignored) {
                }
            }
            return true;

        }
        return false;
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

    public static void registerClientCommands(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        SeedCommand.register(commandDispatcher);
        SpawningSphereCommand.register(commandDispatcher);
        BeaconCommand.register(commandDispatcher);
        BoxCommand.register(commandDispatcher);
    }
}
