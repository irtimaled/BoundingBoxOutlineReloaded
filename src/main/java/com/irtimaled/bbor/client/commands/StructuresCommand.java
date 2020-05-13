package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.gui.LoadSavesScreen;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;

public class StructuresCommand {
    private static final String COMMAND = "bbor:structures";
    private static final String LOAD = "load";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(Commands.literal(LOAD)
                        .executes(context -> {
                            LoadSavesScreen.show();
                            return 0;
                        }))
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            ClientInterop.clearStructures();
                            return 0;
                        }));

        commandDispatcher.register(command);
    }
}
