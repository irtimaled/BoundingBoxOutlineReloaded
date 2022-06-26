package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.commands.ConfigCommand;
import com.irtimaled.bbor.client.commands.CustomCommand;
import com.irtimaled.bbor.client.commands.SeedCommand;
import com.irtimaled.bbor.client.commands.SpawningSphereCommand;
import com.irtimaled.bbor.client.commands.StructuresCommand;
import com.irtimaled.bbor.client.events.DisconnectedFromRemoteServer;
import com.irtimaled.bbor.client.events.SaveLoaded;
import com.irtimaled.bbor.client.events.UpdateWorldSpawnReceived;
import com.irtimaled.bbor.client.providers.SlimeChunkProvider;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.DimensionId;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class ClientInterop {
    public static void disconnectedFromRemoteServer() {
        SaveGameStructureLoader.clear();
        BiomeBorderHelper.onDisconnect();
        EventBus.publish(new DisconnectedFromRemoteServer());
    }

    public static void render(MatrixStack matrixStack, ClientPlayerEntity player) {
        ClientRenderer.render(matrixStack, DimensionId.from(player.getEntityWorld().getRegistryKey()));
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

                        MutableText suggestion = Text.literal("")
                                .formatted(Formatting.GRAY)
                                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message)));
                        int textLength = Math.min(exception.getInput().length(), exception.getCursor());
                        if (textLength > 10) {
                            suggestion.append("...");
                        }

                        suggestion.append(exception.getInput().substring(Math.max(0, textLength - 10), textLength));
                        if (textLength < exception.getInput().length()) {
                            suggestion.append(Text.literal(exception.getInput().substring(textLength))
                                    .formatted(Formatting.RED, Formatting.UNDERLINE));
                        }

                        suggestion.append(Text.translatable("command.context.here")
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
        return MinecraftClient.getInstance().options.getViewDistance().getValue();
    }

    public static void handleSeedMessage(Text chatComponent) {
        TypeHelper.doIfType(chatComponent, TranslatableTextContent.class, message -> {
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
        CustomCommand.register(commandDispatcher);
        ConfigCommand.register(commandDispatcher);
        StructuresCommand.register(commandDispatcher);
    }

    public static void receivedChunk(int chunkX, int chunkZ) {
        SaveGameStructureLoader.loadStructures(chunkX, chunkZ);
        BiomeBorderHelper.onChunkLoaded(chunkX, chunkZ);
    }

    public static void unloadChunk(int chunkX, int chunkZ) {
        BiomeBorderHelper.onChunkUnload(chunkX, chunkZ);
    }

    public static void saveLoaded(String fileName, long seed) {
        displayScreen(null);
        MinecraftClient.getInstance().mouse.lockCursor();

        clearStructures();

        SlimeChunkProvider.setSeed(seed);
        SaveGameStructureLoader.loadSaveGame(fileName);
    }

    public static void clearStructures() {
        EventBus.publish(new SaveLoaded());
        SaveGameStructureLoader.clear();
    }

    public static void displayScreen(Screen screen) {
        MinecraftClient.getInstance().setScreen(screen);
    }

    public static long getGameTime() {
        return MinecraftClient.getInstance().world.getTime();
    }
}
