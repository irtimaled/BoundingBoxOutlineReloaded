package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.SpawningSphereProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.Vec3d;

public class SpawningSphereCommand {
    private static final String COMMAND = "bbor:spawningSphere";
    private static final String SET = "set";
    private static final String POS = "pos";
    private static final String CLEAR = "clear";
    private static final String CALCULATE_SPAWNABLE = "calculateSpawnable";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(Commands.literal(SET)
                        .then(Commands.argument(POS, Vec3Argument.vec3())
                                .executes(context -> {
                                    Vec3d pos = Vec3Argument.getVec3(context, POS);
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
                .then(Commands.literal(CLEAR)
                        .executes(context -> {
                            boolean cleared = SpawningSphereProvider.clear();

                            String format = cleared ? "bbor.commands.spawningSphere.cleared" : "bbor.commands.spawningSphere.notSet";
                            CommandHelper.feedback(context, format);
                            return 0;
                        }))
                .then(Commands.literal(CALCULATE_SPAWNABLE)
                        .executes(context -> {
                            int count = SpawningSphereProvider.recalculateSpawnableSpacesCount();

                            String format = count == -1 ? "bbor.commands.spawningSphere.notSet" : "bbor.commands.spawningSphere.calculated";
                            CommandHelper.feedback(context, format, String.format("%,d", count));
                            return 0;
                        }));
        commandDispatcher.register(command);
    }
}
