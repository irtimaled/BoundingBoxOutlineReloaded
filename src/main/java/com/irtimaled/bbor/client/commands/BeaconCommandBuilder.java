package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBeaconProvider;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

class BeaconCommandBuilder {
    private static final String LEVEL = "level";

    static LiteralArgumentBuilder<CommandSource> build(String command) {
        return Commands.literal(command)
                .then(Commands.literal(ArgumentNames.ADD)
                        .then(Commands.argument(LEVEL, IntegerArgumentType.integer())
                                .executes(context -> {
                                    BlockPos pos = new BlockPos(context.getSource().getPos());
                                    int level = IntegerArgumentType.getInteger(context, LEVEL);
                                    addBeacon(context, pos, level);
                                    return 0;
                                })
                                .then(Commands.argument(ArgumentNames.POS, BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos pos = BlockPosArgument.getBlockPos(context, ArgumentNames.POS);
                                            int level = IntegerArgumentType.getInteger(context, LEVEL);
                                            addBeacon(context, pos, level);
                                            return 0;
                                        })))
                )
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomBeaconProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.beacon.cleared.all");
                            return 0;
                        })
                        .then(Commands.argument(ArgumentNames.POS, BlockPosArgument.blockPos())
                                .executes(context -> {
                                    BlockPos pos = BlockPosArgument.getBlockPos(context, ArgumentNames.POS);
                                    boolean removed = CustomBeaconProvider.remove(new Coords(pos));

                                    String format = removed ? "bbor.commands.beacon.cleared" : "bbor.commands.beacon.notFound";
                                    CommandHelper.feedback(context, format, pos.getX(), pos.getY(), pos.getZ());
                                    return 0;
                                })));
    }

    private static void addBeacon(CommandContext<CommandSource> context, BlockPos pos, int level) throws CommandSyntaxException {
        if (level < 1 || level > 4) {
            throw CommandHelper.getException("bbor.commandArgument.invalid",
                    "bbor.commands.beacon.expectedLevelInRange")
                    .createWithContext(new StringReader(context.getInput()));
        }

        CustomBeaconProvider.add(new Coords(pos), level);
        CommandHelper.feedback(context, "bbor.commands.beacon.added", pos.getX(), pos.getY(), pos.getZ(), level);
    }
}
