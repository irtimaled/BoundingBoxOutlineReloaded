package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBoxProvider;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

public class BoxCommand {
    private static final String COMMAND = "bbor:box";
    private static final String ADD = "add";
    private static final String CLEAR = "clear";
    private static final String FROM = "from";
    private static final String TO = "to";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(Commands.literal(ADD)
                        .then(Commands.argument(FROM, BlockPosArgument.blockPos())
                                .then(Commands.argument(TO, BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos from = BlockPosArgument.getBlockPos(context, FROM);
                                            BlockPos to = BlockPosArgument.getBlockPos(context, TO);
                                            Coords minCoords = getMinCoords(from, to);
                                            Coords maxCoords = getMaxCoords(from, to);
                                            CustomBoxProvider.add(minCoords, maxCoords);

                                            CommandHelper.feedback(context, "bbor.commands.box.added",
                                                    from.getX(), from.getY(), from.getZ(),
                                                    to.getX(), to.getY(), to.getZ());
                                            return 0;
                                        }))))
                .then(Commands.literal(CLEAR)
                        .executes(context -> {
                            CustomBoxProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.box.cleared.all");
                            return 0;
                        })
                        .then(Commands.argument(FROM, BlockPosArgument.blockPos())
                                .then(Commands.argument(TO, BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos from = BlockPosArgument.getBlockPos(context, FROM);
                                            BlockPos to = BlockPosArgument.getBlockPos(context, TO);
                                            Coords minCoords = getMinCoords(from, to);
                                            Coords maxCoords = getMaxCoords(from, to);
                                            boolean removed = CustomBoxProvider.remove(minCoords, maxCoords);

                                            String format = removed ? "bbor.commands.box.cleared" : "bbor.commands.box.notFound";
                                            CommandHelper.feedback(context, format,
                                                    from.getX(), from.getY(), from.getZ(),
                                                    to.getX(), to.getY(), to.getZ());
                                            return 0;
                                        }))));
        commandDispatcher.register(command);
    }

    private static Coords getMaxCoords(BlockPos from, BlockPos to) {
        return new Coords(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
    }

    private static Coords getMinCoords(BlockPos from, BlockPos to) {
        return new Coords(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
    }
}
