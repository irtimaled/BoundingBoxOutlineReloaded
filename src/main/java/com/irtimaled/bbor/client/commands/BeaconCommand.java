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
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class BeaconCommand {
    private static final String COMMAND = "bbor:beacon";
    private static final String ADD = "add";
    private static final String CLEAR = "clear";
    private static final String POS = "pos";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(Commands.literal(ADD)
                        .then(Commands.argument(POS, BlockPosArgument.blockPos())
                                .executes(context -> {
                                    BlockPos pos = BlockPosArgument.getBlockPos(context, POS);
                                    AddValidBeacon(context, pos);
                                    return 0;
                                }))
                        .executes(context -> {
                            BlockPos pos = new BlockPos(context.getSource().getPos());
                            AddValidBeacon(context, pos);
                            return 0;
                        }))
                .then(Commands.literal(CLEAR)
                        .executes(context -> {
                            BeaconProvider.clear();

                            CommandHelper.feedback(context, "All beacons cleared");
                            return 0;
                        })
                        .then(Commands.argument(POS, BlockPosArgument.blockPos())
                                .executes(context -> {
                                    BlockPos pos = BlockPosArgument.getBlockPos(context, POS);
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

    private static void AddValidBeacon(CommandContext<CommandSource> context, BlockPos pos) throws CommandSyntaxException {
        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
        TileEntityBeacon beacon = TypeHelper.as(tileEntity, TileEntityBeacon.class);
        if(beacon == null) {
            BlockPos playerPosition = new BlockPos(context.getSource().getPos());
            if (pos.getDistance(playerPosition) > ClientInterop.getRenderDistanceChunks()*16) {
                throw POS_UNLOADED.create();
            }

            throw POS_NOT_BEACON.create();
        }
        BeaconProvider.add(new Coords(pos), beacon.getLevels());

        String feedback = getPosBasedFeedback("Beacon added", pos);
        CommandHelper.feedback(context, feedback);
    }

    private static String getPosBasedFeedback(String prefix, BlockPos pos) {
        return String.format("%s at [%d, %d, %d]", prefix, pos.getX(), pos.getY(), pos.getZ());
    }

    private static final SimpleCommandExceptionType INCOMPLETE_COMMAND =
            CommandHelper.getIncompleteCommandException(COMMAND, ADD, CLEAR);
    private static final SimpleCommandExceptionType POS_NOT_BEACON =
            new SimpleCommandExceptionType(new TextComponentString("That position is not a beacon block"));
    private static final SimpleCommandExceptionType POS_UNLOADED =
            new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.unloaded"));

}

