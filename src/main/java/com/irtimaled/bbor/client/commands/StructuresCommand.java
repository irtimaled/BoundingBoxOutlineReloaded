package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.gui.LoadSavesScreen;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;

public class StructuresCommand {
    private static final String COMMAND = "bbor:structures";
    private static final String LOAD = "load";

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralArgumentBuilder command = CommandManager.literal(COMMAND)
                .then(CommandManager.literal(LOAD)
                        .executes(context -> {
                            LoadSavesScreen.show();
                            return 0;
                        }))
                .then(CommandManager.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            ClientInterop.clearStructures();
                            return 0;
                        }));

        commandDispatcher.register(command);
    }
}
