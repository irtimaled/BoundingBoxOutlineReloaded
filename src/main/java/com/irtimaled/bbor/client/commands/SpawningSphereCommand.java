package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.providers.SpawningSphereProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;

public class SpawningSphereCommand {
    private static final String COMMAND = "bbor:spawningSphere";
    private static final String CALCULATE_SPAWNABLE = "calculateSpawnable";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(Commands.literal(ArgumentNames.SET)
                        .then(Commands.argument(ArgumentNames.POS, Vec3Argument.vec3())
                                .executes(context -> {
                                    Vec3d pos = Vec3Argument.getVec3(context, ArgumentNames.POS);
                                    SpawningSphereProvider.setSphere(pos.x, pos.y, pos.z);

                                    CommandHelper.feedback(context, "bbor.commands.spawningSphere.set");
                                    return 0;
                                }))
                        .executes(context -> {
                            Vec3d pos = context.getSource().getPos();
                            SpawningSphereProvider.setSphere(pos.x, pos.y, pos.z);

                            CommandHelper.feedback(context, "bbor.commands.spawningSphere.set");
                            return 0;
                        }))
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            boolean cleared = SpawningSphereProvider.clear();

                            String format = cleared ? "bbor.commands.spawningSphere.cleared" : "bbor.commands.spawningSphere.notSet";
                            CommandHelper.feedback(context, format);
                            return 0;
                        }))
                .then(Commands.literal(CALCULATE_SPAWNABLE)
                        .executes(context -> {
                            if(!SpawningSphereProvider.hasSpawningSphereInDimension(Player.getDimensionId())) {
                                CommandHelper.feedback(context, "bbor.commands.spawningSphere.notSet");
                                return 0;
                            }

                            Counts counts = new Counts();
                            World world = Minecraft.getInstance().world;
                            SpawningSphereProvider.calculateSpawnableSpacesCount(pos -> {
                                counts.spawnable++;
                                if(world.getLightFor(EnumLightType.SKY, pos) > 7)
                                    counts.nightSpawnable++;
                            });
                            SpawningSphereProvider.setSpawnableSpacesCount(counts.spawnable);

                            CommandHelper.feedback(context, "bbor.commands.spawningSphere.calculated",
                                    String.format("%,d", counts.spawnable),
                                    String.format("%,d", counts.nightSpawnable));
                            return 0;
                        }));
        commandDispatcher.register(command);
    }

    private static class Counts {
        private int spawnable = 0;
        private int nightSpawnable = 0;

    }
}
