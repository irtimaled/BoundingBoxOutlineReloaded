package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBeaconProvider;
import com.irtimaled.bbor.client.providers.CustomBoxProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;

public class CustomCommand {
    private static final String COMMAND = "bbor:custom";
    private static final String BOX = "box";
    private static final String BEACON = "beacon";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(BoxCommandBuilder.build(BOX))
                .then(BeaconCommandBuilder.build(BEACON))
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomBoxProvider.clear();
                            CustomBeaconProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.custom.cleared.all");
                            return 0;
                        }));
        commandDispatcher.register(command);
    }
}

