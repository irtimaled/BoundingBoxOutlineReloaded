package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.BeaconProvider;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;

public class BeaconCommand {
    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal("bbor:beacon")
                .then(Commands.literal("add")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes((context) -> {
                                    BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
                                    TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
                                    TypeHelper.doIfType(tileEntity, TileEntityBeacon.class, beacon -> {
                                        BeaconProvider.add(new Coords(pos), beacon.getLevels());
                                    });
                                    return 0;
                                })))
                .then(Commands.literal("clear")
                        .executes((context) -> {
                            BeaconProvider.clear();
                            return 0;
                        })
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes((context) -> {
                                    BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
                                    BeaconProvider.remove(new Coords(pos));
                                    return 0;
                                })));
        commandDispatcher.register(command);
    }
}
