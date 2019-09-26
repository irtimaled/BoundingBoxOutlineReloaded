package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.SpawningSphereProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;

public class SpawningSphereCommand {
    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal("bbor:spawningSphere")
                .then(Commands.literal("set")
                        .executes((context) -> {
                            SpawningSphereProvider.setSphere();
                            return 0;
                        }))
                .then(Commands.literal("clear")
                        .executes((context) -> {
                            SpawningSphereProvider.clear();
                            return 0;
                        }))
                .then(Commands.literal("calculateSpawnable")
                        .executes((context) -> {
                            SpawningSphereProvider.recalculateSpawnableSpacesCount();
                            return 0;
                        }));
        commandDispatcher.register(command);
    }
}
