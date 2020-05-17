package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.config.HexColor;
import com.irtimaled.bbor.client.config.Setting;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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
    private static final String RESET = "reset";

    public static void register(CommandDispatcher<ISuggestionProvider> commandDispatcher) {
        LiteralArgumentBuilder command = Commands.literal(COMMAND)
                .then(buildCommands(GET, ConfigCommand::getCommandForSetting))
                .then(buildCommands(ArgumentNames.SET, ConfigCommand::setCommandForSetting))
                .then(buildCommands(RESET, ConfigCommand::resetCommandForSetting)
                        .executes(context1 -> {
                            ConfigManager.getSettings().forEach(Setting::reset);
                            ConfigManager.saveConfig();
                            return 0;
                        }))
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

    private static LiteralArgumentBuilder<CommandSource> resetCommandForSetting(Setting<?> setting) {
        return Commands.literal(setting.getName())
                .executes(context -> {
                    setting.reset();
                    ConfigManager.saveConfig();
                    return 0;
                });
    }

    private interface CommandBuilder extends Function<Setting<?>, LiteralArgumentBuilder<CommandSource>> {
    }

    private static LiteralArgumentBuilder<CommandSource> buildCommands(String commandName, CommandBuilder commandBuilder) {
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
                buildSetSettingCommand(command, (Setting<Boolean>) setting, Arguments.bool(), Boolean.class);
                break;
            case 'I':
                buildSetSettingCommand(command, (Setting<Integer>) setting, Arguments.integer(), Integer.class);
                break;
            case 'S':
                buildSetSettingCommand(command, (Setting<String>) setting, Arguments.string(), String.class);
                break;
            case 'H':
                buildSetSettingCommand(command, (Setting<HexColor>) setting, Arguments.hexColor(), HexColor.class);
                break;
        }
        return command;
    }

    private static <T> void buildSetSettingCommand(LiteralArgumentBuilder<CommandSource> command,
                                                   Setting<T> setting, ArgumentType<T> argument, Class<T> clazz) {
        Command<CommandSource> setSettingCommand = context -> {
            setting.set(context.getArgument(VALUE, clazz));
            if (CommandHelper.lastNodeIsLiteral(context, SAVE)) {
                ConfigManager.saveConfig();
            }
            return 0;
        };
        command.then(Commands.argument(VALUE, argument)
                .executes(setSettingCommand)
                .then(Commands.literal(SAVE)
                        .executes(setSettingCommand)));
    }
}
