package com.irtimaled.bbor.client.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

class CommandHelper {
    static SimpleCommandExceptionType getIncompleteCommandException(String cmd, String... commands) {
        ITextComponent textComponent = new StringTextComponent("Incomplete command");

        int length = commands.length;
        if (length > 0) {
            textComponent.appendText(" (expected ");
            for (int idx = 0; idx < length; idx++) {
                if (idx > 0) textComponent.appendText(", ");
                if (idx + 1 == length) textComponent.appendText("or ");
                String command = commands[idx];
                String commandSuggestion = String.format("/%s %s", cmd, command);
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSuggestion);
                ITextComponent suggestion = new StringTextComponent(command)
                        .applyTextStyle(TextFormatting.UNDERLINE)
                        .applyTextStyle(style -> style.setClickEvent(clickEvent));
                textComponent.appendSibling(suggestion);
            }
            textComponent.appendText(")");
        }
        return new SimpleCommandExceptionType(textComponent);
    }

    static void feedback(CommandContext<CommandSource> context, String feedback) {
        context.getSource().sendFeedback(new StringTextComponent(feedback), false);
    }
}
