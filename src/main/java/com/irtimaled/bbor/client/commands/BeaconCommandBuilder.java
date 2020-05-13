package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBeaconProvider;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

class BeaconCommandBuilder {
    private static final String LEVEL = "level";

    static LiteralArgumentBuilder<CommandSource> build(String command) {
        return Commands.literal(command)
                .then(Commands.literal(ArgumentNames.ADD)
                        .then(Commands.argument(LEVEL, Arguments.integer(1,4))
                                .executes(BeaconCommandBuilder::addBeacon)
                                .then(Commands.argument(ArgumentNames.POS, Arguments.coords())
                                        .executes(BeaconCommandBuilder::addBeacon)))
                )
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomBeaconProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.beacon.cleared.all");
                            return 0;
                        })
                        .then(Commands.argument(ArgumentNames.POS, Arguments.coords())
                                .executes(context -> {
                                    Coords coords = Arguments.getCoords(context, ArgumentNames.POS);
                                    boolean removed = CustomBeaconProvider.remove(coords);

                                    String format = removed ? "bbor.commands.beacon.cleared" : "bbor.commands.beacon.notFound";
                                    CommandHelper.feedback(context, format, coords.getX(), coords.getY(), coords.getZ());
                                    return 0;
                                })));
    }

    private static int addBeacon(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Coords coords = Arguments.getCoords(context, ArgumentNames.POS);
        int level = Arguments.getInteger(context, LEVEL);

        CustomBeaconProvider.add(coords, level);
        CommandHelper.feedback(context, "bbor.commands.beacon.added", coords.getX(), coords.getY(), coords.getZ(), level);
        return 0;
    }
}
