package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBeaconProvider;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

public class BeaconCommand {
    private static final String COMMAND = "bbor:beacon";
    private static final String ADD = "add";
    private static final String CLEAR = "clear";
    private static final String POS = "pos";
    private static final String LEVEL = "level";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(Commands.literal(ADD)
                        .then(Commands.argument(LEVEL, IntegerArgumentType.integer())
                                .executes(context -> {
                                    BlockPos pos = new BlockPos(context.getSource().getPos());
                                    int level = IntegerArgumentType.getInteger(context, LEVEL);
                                    addBeacon(context, pos, level);
                                    return 0;
                                })
                                .then(Commands.argument(POS, BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos pos = BlockPosArgument.getBlockPos(context, POS);
                                            int level = IntegerArgumentType.getInteger(context, LEVEL);
                                            addBeacon(context, pos, level);
                                            return 0;
                                        })))
                )
                .then(Commands.literal(CLEAR)
                        .executes(context -> {
                            CustomBeaconProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.beacon.cleared.all");
                            return 0;
                        })
                        .then(Commands.argument(POS, BlockPosArgument.blockPos())
                                .executes(context -> {
                                    BlockPos pos = BlockPosArgument.getBlockPos(context, POS);
                                    boolean removed = CustomBeaconProvider.remove(new Coords(pos));

                                    String format = removed ? "bbor.commands.beacon.cleared" : "bbor.commands.beacon.notFound";
                                    CommandHelper.feedback(context, format, pos.getX(), pos.getY(), pos.getZ());
                                    return 0;
                                })));
        commandDispatcher.register(command);
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

