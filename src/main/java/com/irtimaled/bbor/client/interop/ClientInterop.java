package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.commands.*;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.SaveLoaded;
import com.irtimaled.bbor.client.events.UpdateWorldSpawnReceived;
import com.irtimaled.bbor.client.providers.SlimeChunkProvider;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.DimensionId;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

public class ClientInterop {
    public static void disconnectedFromRemoteServer() {
        SaveGameStructureLoader.clear();
        EventBus.publish(new DisconnectedFromRemoteServer());
    }

    public static void render(float partialTicks, ClientPlayerEntity player) {
        Player.setPosition(partialTicks, player);
        ClientRenderer.render(DimensionId.from(player.dimension));
    }

    public static void renderDeferred() {
        ClientRenderer.renderDeferred();
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
        CustomCommand.register(commandDispatcher);
        ConfigCommand.register(commandDispatcher);
        StructuresCommand.register(commandDispatcher);
    }

    public static void receivedChunk(int chunkX, int chunkZ) {
        SaveGameStructureLoader.loadStructures(chunkX, chunkZ);
    }

    public static void saveLoaded(String fileName, long seed) {
        displayScreen(null);
        Minecraft.getInstance().mouseHelper.grabMouse();

        clearStructures();

        SlimeChunkProvider.setSeed(seed);
        SaveGameStructureLoader.loadSaveGame(fileName);
    }

    public static void clearStructures() {
        EventBus.publish(new SaveLoaded());
        SaveGameStructureLoader.clear();
    }

    public static void displayScreen(Screen screen) {
        Minecraft.getInstance().displayGuiScreen(screen);
    }

    public static long getGameTime() {
        return Minecraft.getInstance().world.getGameTime();
    }
}
