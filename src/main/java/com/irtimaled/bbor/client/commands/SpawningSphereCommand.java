package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.SpawningSphereProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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

                                    CommandHelper.feedback(context, "Spawning sphere set");
                                    return 0;
                                }))
                        .executes(context -> {
                            Vec3d pos = context.getSource().getPos();
                            SpawningSphereProvider.setSphere(pos.x, pos.y, pos.z);

                            CommandHelper.feedback(context, "Spawning sphere set");
                            return 0;
                        }))
                .then(Commands.literal(CLEAR)
                        .executes(context -> {
                            boolean cleared = SpawningSphereProvider.clear();

                            String feedback = cleared ? "Spawning sphere cleared" : "No spawning sphere set";
                            CommandHelper.feedback(context, feedback);
                            return 0;
                        }))
                .then(Commands.literal(CALCULATE_SPAWNABLE)
                        .executes(context -> {
                            int count = SpawningSphereProvider.recalculateSpawnableSpacesCount();

                            String feedback = count == -1 ? "No spawning sphere set" : String.format("Calculated %,d spawnable spaces", count);
                            CommandHelper.feedback(context, feedback);
                            return 0;
                        }))
                .executes(context -> {
                    throw INCOMPLETE_COMMAND.create();
                });
        commandDispatcher.register(command);
    }

    private static final SimpleCommandExceptionType INCOMPLETE_COMMAND =
            CommandHelper.getIncompleteCommandException(COMMAND, SET, CLEAR, CALCULATE_SPAWNABLE);
}
