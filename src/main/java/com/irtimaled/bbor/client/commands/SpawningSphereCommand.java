package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.SpawningSphereProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.math.Vec3d;

public class SpawningSphereCommand {
    private static final String COMMAND = "bbor:spawningSphere";
    private static final String SET = "set";
    private static final String POS = "pos";
    private static final String CLEAR = "clear";
    private static final String CALCULATE_SPAWNABLE = "calculateSpawnable";

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralArgumentBuilder command = CommandManager.literal(COMMAND)
                .then(CommandManager.literal(SET)
                        .then(CommandManager.argument(POS, Vec3ArgumentType.vec3())
                                .executes(context -> {
                                    Vec3d pos = Vec3ArgumentType.getVec3(context, POS);
                                    SpawningSphereProvider.setSphere(pos.x, pos.y, pos.z);

                                    CommandHelper.feedback(context, "Spawning sphere set");
                                    return 0;
                                }))
                        .executes(context -> {
                            Vec3d pos = context.getSource().getPosition();
                            SpawningSphereProvider.setSphere(pos.x, pos.y, pos.z);

                            CommandHelper.feedback(context, "Spawning sphere set");
                            return 0;
                        }))
                .then(CommandManager.literal(CLEAR)
                        .executes(context -> {
                            boolean cleared = SpawningSphereProvider.clear();

                            String feedback = cleared ? "Spawning sphere cleared" : "No spawning sphere set";
                            CommandHelper.feedback(context, feedback);
                            return 0;
                        }))
                .then(CommandManager.literal(CALCULATE_SPAWNABLE)
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
