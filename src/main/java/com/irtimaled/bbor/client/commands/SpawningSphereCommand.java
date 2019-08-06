package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.providers.SpawningSphereProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SpawningSphereCommand {
    private static final String COMMAND = "bbor:spawningSphere";
    private static final String CALCULATE_SPAWNABLE = "calculateSpawnable";

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        LiteralArgumentBuilder command = CommandManager.literal(COMMAND)
                .then(CommandManager.literal(ArgumentNames.SET)
                        .then(CommandManager.argument(ArgumentNames.POS, Arguments.point())
                                .executes(SpawningSphereCommand::setSphere))
                        .executes(SpawningSphereCommand::setSphere))
                .then(CommandManager.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            boolean cleared = SpawningSphereProvider.clearSphere();

                            String format = cleared ? "bbor.commands.spawningSphere.cleared" : "bbor.commands.spawningSphere.notSet";
                            CommandHelper.feedback(context, format);
                            return 0;
                        }))
                .then(CommandManager.literal(CALCULATE_SPAWNABLE)
                        .executes(context -> {
                            if (!SpawningSphereProvider.hasSpawningSphereInDimension(Player.getDimensionId())) {
                                CommandHelper.feedback(context, "bbor.commands.spawningSphere.notSet");
                                return 0;
                            }

                            Counts counts = new Counts();
                            World world = MinecraftClient.getInstance().world;
                            SpawningSphereProvider.calculateSpawnableSpacesCount(pos -> {
                                counts.spawnable++;
                                if (world.getLightLevel(LightType.SKY, pos) > 7)
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

    public static int setSphere(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        SpawningSphereProvider.setSphere(Arguments.getPoint(context, ArgumentNames.POS).snapXZ(0.5d));

        CommandHelper.feedback(context, "bbor.commands.spawningSphere.set");
        return 0;
    }

    private static class Counts {
        private int spawnable = 0;
        private int nightSpawnable = 0;

    }
}
