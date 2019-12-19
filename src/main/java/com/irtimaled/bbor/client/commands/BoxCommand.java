package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBoxProvider;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.math.BlockPos;

public class BoxCommand {
    private static final String COMMAND = "bbor:box";
    private static final String ADD = "add";
    private static final String CLEAR = "clear";
    private static final String FROM = "from";
    private static final String TO = "to";

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralArgumentBuilder command = CommandManager.literal(COMMAND)
                .then(CommandManager.literal(ADD)
                        .then(CommandManager.argument(FROM, BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument(TO, BlockPosArgumentType.blockPos())
                                        .executes(context -> {
                                            BlockPos from = BlockPosArgumentType.getBlockPos(context, FROM);
                                            BlockPos to = BlockPosArgumentType.getBlockPos(context, TO);
                                            Coords minCoords = getMinCoords(from, to);
                                            Coords maxCoords = getMaxCoords(from, to);
                                            CustomBoxProvider.add(minCoords, maxCoords);

                                            String feedback = getPosBasedFeedback("Box added", from, to);
                                            CommandHelper.feedback(context, feedback);
                                            return 0;
                                        }))))
                .then(CommandManager.literal(CLEAR)
                        .executes(context -> {
                            CustomBoxProvider.clear();

                            CommandHelper.feedback(context, "All boxes cleared");
                            return 0;
                        })
                        .then(CommandManager.argument(FROM, BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument(TO, BlockPosArgumentType.blockPos())
                                        .executes(context -> {
                                            BlockPos from = BlockPosArgumentType.getBlockPos(context, FROM);
                                            BlockPos to = BlockPosArgumentType.getBlockPos(context, TO);
                                            Coords minCoords = getMinCoords(from, to);
                                            Coords maxCoords = getMaxCoords(from, to);
                                            boolean removed = CustomBoxProvider.remove(minCoords, maxCoords);

                                            String prefix = removed ? "Box cleared" : "No box found";
                                            String feedback = getPosBasedFeedback(prefix, from, to);
                                            CommandHelper.feedback(context, feedback);
                                            return 0;
                                        }))))
                .executes(context -> {
                    throw INCOMPLETE_COMMAND.create();
                });
        commandDispatcher.register(command);
    }

    private static Coords getMaxCoords(BlockPos from, BlockPos to) {
        return new Coords(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
    }

    private static Coords getMinCoords(BlockPos from, BlockPos to) {
        return new Coords(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
    }

    private static String getPosBasedFeedback(String prefix, BlockPos from, BlockPos to) {
        return String.format("%s with start [%d, %d, %d] and end [%d, %d, %d]", prefix, from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }

    private static final SimpleCommandExceptionType INCOMPLETE_COMMAND =
            CommandHelper.getIncompleteCommandException(COMMAND, ADD, CLEAR);
}
