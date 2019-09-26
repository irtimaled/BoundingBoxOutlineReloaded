package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBoxProvider;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiConsumer;

public class BoxCommand {
    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal("bbor:box")
                .then(Commands.literal("add")
                        .then(Commands.argument("from", BlockPosArgument.blockPos())
                                .then(Commands.argument("to", BlockPosArgument.blockPos())
                                        .executes((context) -> {
                                            CoordsFromContext(context, CustomBoxProvider::add);
                                            return 0;
                                        }))))
                .then(Commands.literal("clear")
                        .executes((context) -> {
                            CustomBoxProvider.clear();
                            return 0;
                        })
                        .then(Commands.argument("from", BlockPosArgument.blockPos())
                                .then(Commands.argument("to", BlockPosArgument.blockPos())
                                        .executes((context) -> {
                                            CoordsFromContext(context, CustomBoxProvider::remove);
                                            return 0;
                                        }))));
        commandDispatcher.register(command);
    }

    private static void CoordsFromContext(CommandContext context, BiConsumer<Coords, Coords> consumer) throws CommandSyntaxException {
        BlockPos from = BlockPosArgument.getBlockPos(context, "from");
        BlockPos to = BlockPosArgument.getBlockPos(context, "to");
        Coords minCoords = new Coords(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        Coords maxCoords = new Coords(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        consumer.accept(minCoords, maxCoords);
    }
}
