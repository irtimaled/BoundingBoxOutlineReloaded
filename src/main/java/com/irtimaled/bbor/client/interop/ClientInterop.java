package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.commands.BeaconCommand;
import com.irtimaled.bbor.client.commands.BoxCommand;
import com.irtimaled.bbor.client.commands.SeedCommand;
import com.irtimaled.bbor.client.commands.SpawningSphereCommand;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.UpdateWorldSpawnReceived;
import com.irtimaled.bbor.client.providers.SlimeChunkProvider;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.TypeHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class ClientInterop {
    public static void disconnectedFromRemoteServer() {
        EventBus.publish(new DisconnectedFromRemoteServer());
    }

    public static void render(float partialTicks, ClientPlayerEntity player) {
        Player.setPosition(partialTicks, player);
        ClientRenderer.render(player.dimension.getRawId());
    }

    public static boolean interceptChatMessage(String message) {
        if (message.startsWith("/bbor:")) {
            ClientPlayNetworkHandler connection = MinecraftClient.getInstance().getNetworkHandler();
            if (connection != null) {
                CommandDispatcher<CommandSource> commandDispatcher = connection.getCommandDispatcher();
                ServerCommandSource commandSource = MinecraftClient.getInstance().player.getCommandSource();
                try {
                    commandDispatcher.execute(message.substring(1), commandSource);
                } catch (CommandSyntaxException exception) {
                    commandSource.sendError(Texts.toText(exception.getRawMessage()));
                    if (exception.getInput() != null && exception.getCursor() >= 0) {
                        Text suggestion = new LiteralText("")
                                .formatted(Formatting.GRAY)
                                .styled(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message)));
                        int textLength = Math.min(exception.getInput().length(), exception.getCursor());
                        if (textLength > 10) {
                            suggestion.append("...");
                        }

                        suggestion.append(exception.getInput().substring(Math.max(0, textLength - 10), textLength));
                        if (textLength < exception.getInput().length()) {
                            suggestion.append(new LiteralText(exception.getInput().substring(textLength))
                                    .formatted(Formatting.RED, Formatting.UNDERLINE));
                        }

                        suggestion.append(new TranslatableText("command.context.here")
                                .formatted(Formatting.RED, Formatting.ITALIC));
                        commandSource.sendError(suggestion);
                    }
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
        return MinecraftClient.getInstance().options.viewDistance;
    }

    public static void handleSeedMessage(Text chatComponent) {
        TypeHelper.doIfType(chatComponent, TranslatableText.class, message -> {
            if (!message.getKey().equals("commands.seed.success")) return;

            try {
                long seed = Long.parseLong(message.getArgs()[0].toString());
                SlimeChunkProvider.setSeed(seed);
            } catch (Exception ignored) {
            }
        });
    }

    public static void registerClientCommands(CommandDispatcher<CommandSource> commandDispatcher) {
        SeedCommand.register(commandDispatcher);
        SpawningSphereCommand.register(commandDispatcher);
        BeaconCommand.register(commandDispatcher);
        BoxCommand.register(commandDispatcher);
    }
}
