package com.irtimaled.bbor.client.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

class CommandHelper {
    static SimpleCommandExceptionType getIncompleteCommandException(String cmd, String... commands) {
        Text textComponent = new LiteralText("Incomplete command");

        int length = commands.length;
        if (length > 0) {
            textComponent.append(" (expected ");
            for (int idx = 0; idx < length; idx++) {
                if (idx > 0) textComponent.append(", ");
                if (idx + 1 == length) textComponent.append("or ");
                String command = commands[idx];
                String commandSuggestion = String.format("/%s %s", cmd, command);
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSuggestion);
                Text suggestion = new LiteralText(command)
                        .formatted(Formatting.UNDERLINE)
                        .styled(style -> style.setClickEvent(clickEvent));
                textComponent.append(suggestion);
            }
            textComponent.append(")");
        }
        return new SimpleCommandExceptionType(textComponent);
    }

    static void feedback(CommandContext<ServerCommandSource> context, String feedback) {
        context.getSource().sendFeedback(new LiteralText(feedback), false);
    }
}
