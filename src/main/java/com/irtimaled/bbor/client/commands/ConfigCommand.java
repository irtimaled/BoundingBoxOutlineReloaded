package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.config.Setting;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;

import java.util.function.Function;

public class ConfigCommand {
    private static final String COMMAND = "bbor:config";
    private static final String GET = "get";
    private static final String SAVE = "save";
    private static final String SHOW_GUI = "showGui";
    private static final String VALUE = "value";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(buildCommands(GET, ConfigCommand::getCommandForSetting))
                .then(buildCommands(ArgumentNames.SET, ConfigCommand::setCommandForSetting))
                .then(Commands.literal(SAVE)
                        .executes(context -> {
                            ConfigManager.saveConfig();
                            return 0;
                        }))
                .then(Commands.literal(SHOW_GUI)
                        .executes(context -> {
                            SettingsScreen.show();
                            return 0;
                        }));

        commandDispatcher.register(command);
    }

    private interface CommandBuilder extends Function<Setting<?>, LiteralArgumentBuilder<CommandSource>> {
    }

    private static LiteralArgumentBuilder<CommandSource> buildCommands(String commandName,
                                                                       CommandBuilder commandBuilder) {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal(commandName);
        for (Setting<?> setting : ConfigManager.getSettings()) {
            command.then(commandBuilder.apply(setting));
        }
        return command;
    }

    private static LiteralArgumentBuilder<CommandSource> getCommandForSetting(Setting<?> setting) {
        return Commands.literal(setting.getName())
                .executes(context -> {
                    CommandHelper.feedback(context, "%s: %s", setting.getName(), setting.get());
                    return 0;
                });
    }

    private static LiteralArgumentBuilder<CommandSource> setCommandForSetting(Setting<?> setting) {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal(setting.getName());
        switch (setting.getType()) {
            case 'B':
                return command.then(Commands.argument(VALUE, Arguments.bool())
                        .executes(context -> {
                            boolean value = Arguments.getBool(context, VALUE);
                            ((Setting<Boolean>) setting).set(value);
                            return 0;
                        }));
            case 'I':
                return command.then(Commands.argument(VALUE, Arguments.integer())
                        .executes(context -> {
                            int value = Arguments.getInteger(context, VALUE);
                            ((Setting<Integer>) setting).set(value);
                            return 0;
                        }));
        }
        return command;
    }
}
