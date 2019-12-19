package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.providers.BeaconProvider;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class BeaconCommand {
    private static final String COMMAND = "bbor:beacon";
    private static final String ADD = "add";
    private static final String CLEAR = "clear";
    private static final String POS = "pos";

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralArgumentBuilder command = CommandManager.literal(COMMAND)
                .then(CommandManager.literal(ADD)
                        .then(CommandManager.argument(POS, BlockPosArgumentType.blockPos())
                                .executes(context -> {
                                    BlockPos pos = BlockPosArgumentType.getBlockPos(context, POS);
                                    AddValidBeacon(context, pos);
                                    return 0;
                                }))
                        .executes(context -> {
                            BlockPos pos = new BlockPos(context.getSource().getPosition());
                            AddValidBeacon(context, pos);
                            return 0;
                        }))
                .then(CommandManager.literal(CLEAR)
                        .executes(context -> {
                            BeaconProvider.clear();

                            CommandHelper.feedback(context, "All beacons cleared");
                            return 0;
                        })
                        .then(CommandManager.argument(POS, BlockPosArgumentType.blockPos())
                                .executes(context -> {
                                    BlockPos pos = BlockPosArgumentType.getBlockPos(context, POS);
                                    boolean removed = BeaconProvider.remove(new Coords(pos));

                                    String prefix = removed ? "Beacon cleared" : "No beacon found";
                                    String feedback = getPosBasedFeedback(prefix, pos);
                                    CommandHelper.feedback(context, feedback);
                                    return 0;
                                })))
                .executes(context -> {
                    throw INCOMPLETE_COMMAND.create();
                });
        commandDispatcher.register(command);
    }

    private static void AddValidBeacon(CommandContext<ServerCommandSource> context, BlockPos pos) throws CommandSyntaxException {
        BlockEntity tileEntity = MinecraftClient.getInstance().world.getBlockEntity(pos);
        BeaconBlockEntity beacon = TypeHelper.as(tileEntity, BeaconBlockEntity.class);
        if(beacon == null) {
            if (!pos.isWithinDistance(context.getSource().getPosition(), ClientInterop.getRenderDistanceChunks()*16)) {
                throw POS_UNLOADED.create();
            }

            throw POS_NOT_BEACON.create();
        }
        BeaconProvider.add(new Coords(pos), beacon.getLevel());

        String feedback = getPosBasedFeedback("Beacon added", pos);
        CommandHelper.feedback(context, feedback);
    }

    private static String getPosBasedFeedback(String prefix, BlockPos pos) {
        return String.format("%s at [%d, %d, %d]", prefix, pos.getX(), pos.getY(), pos.getZ());
    }

    private static final SimpleCommandExceptionType INCOMPLETE_COMMAND =
            CommandHelper.getIncompleteCommandException(COMMAND, ADD, CLEAR);
    private static final SimpleCommandExceptionType POS_NOT_BEACON =
            new SimpleCommandExceptionType(new LiteralText("That position is not a beacon block"));
    private static final SimpleCommandExceptionType POS_UNLOADED =
            new SimpleCommandExceptionType(new TranslatableText("argument.pos.unloaded"));

}

