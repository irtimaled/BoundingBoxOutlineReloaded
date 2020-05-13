package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBoxProvider;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

class BoxCommandBuilder {
    static LiteralArgumentBuilder<CommandSource> build(String command) {
        return Commands.literal(command)
                .then(Commands.literal(ArgumentNames.ADD)
                        .then(Commands.argument(ArgumentNames.FROM, BlockPosArgument.blockPos())
                                .then(Commands.argument(ArgumentNames.TO, BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos from = BlockPosArgument.getBlockPos(context, ArgumentNames.FROM);
                                            BlockPos to = BlockPosArgument.getBlockPos(context, ArgumentNames.TO);
                                            Coords minCoords = getMinCoords(from, to);
                                            Coords maxCoords = getMaxCoords(from, to);
                                            CustomBoxProvider.add(minCoords, maxCoords);

                                            CommandHelper.feedback(context, "bbor.commands.box.added",
                                                    from.getX(), from.getY(), from.getZ(),
                                                    to.getX(), to.getY(), to.getZ());
                                            return 0;
                                        }))))
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomBoxProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.box.cleared.all");
                            return 0;
                        })
                        .then(Commands.argument(ArgumentNames.FROM, BlockPosArgument.blockPos())
                                .then(Commands.argument(ArgumentNames.TO, BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos from = BlockPosArgument.getBlockPos(context, ArgumentNames.FROM);
                                            BlockPos to = BlockPosArgument.getBlockPos(context, ArgumentNames.TO);
                                            Coords minCoords = getMinCoords(from, to);
                                            Coords maxCoords = getMaxCoords(from, to);
                                            boolean removed = CustomBoxProvider.remove(minCoords, maxCoords);

                                            String format = removed ? "bbor.commands.box.cleared" : "bbor.commands.box.notFound";
                                            CommandHelper.feedback(context, format,
                                                    from.getX(), from.getY(), from.getZ(),
                                                    to.getX(), to.getY(), to.getZ());
                                            return 0;
                                        }))));
    }

    private static Coords getMaxCoords(BlockPos from, BlockPos to) {
        return new Coords(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
    }

    private static Coords getMinCoords(BlockPos from, BlockPos to) {
        return new Coords(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
    }
}
