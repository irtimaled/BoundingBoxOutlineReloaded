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
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

public class ClientInterop {
    public static void disconnectedFromRemoteServer() {
        EventBus.publish(new DisconnectedFromRemoteServer());
    }

    public static void render(float partialTicks, ClientPlayerEntity player) {
        Player.setPosition(partialTicks, player);
        ClientRenderer.render(player.dimension.getId());
    }

    public static boolean interceptChatMessage(String message) {
        if (message.startsWith("/bbor:")) {
            ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                CommandDispatcher<ISuggestionProvider> commandDispatcher = connection.getCommandDispatcher();
                CommandSource commandSource = Minecraft.getInstance().player.getCommandSource();
                try {
                    commandDispatcher.execute(message.substring(1), commandSource);
                } catch (CommandSyntaxException exception) {
                    commandSource.sendErrorMessage(TextComponentUtils.toTextComponent(exception.getRawMessage()));
                    if (exception.getInput() != null && exception.getCursor() >= 0) {
                        ITextComponent suggestion = new StringTextComponent("")
                                .applyTextStyle(TextFormatting.GRAY)
                                .applyTextStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message)));
                        int textLength = Math.min(exception.getInput().length(), exception.getCursor());
                        if (textLength > 10) {
                            suggestion.appendText("...");
                        }

                        suggestion.appendText(exception.getInput().substring(Math.max(0, textLength - 10), textLength));
                        if (textLength < exception.getInput().length()) {
                            suggestion.appendSibling(new StringTextComponent(exception.getInput().substring(textLength))
                                    .applyTextStyles(TextFormatting.RED, TextFormatting.UNDERLINE));
                        }

                        suggestion.appendSibling(new TranslationTextComponent("command.context.here")
                                .applyTextStyles(TextFormatting.RED, TextFormatting.ITALIC));
                        commandSource.sendErrorMessage(suggestion);
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
        return Minecraft.getInstance().gameSettings.renderDistanceChunks;
    }

    public static void handleSeedMessage(ITextComponent chatComponent) {
        TypeHelper.doIfType(chatComponent, TranslationTextComponent.class, message -> {
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
