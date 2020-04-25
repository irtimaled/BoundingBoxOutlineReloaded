package com.irtimaled.bbor.client.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

class CommandHelper {
    static void feedback(CommandContext<CommandSource> context, String format, Object... values) {
        context.getSource().sendFeedback(new TextComponentTranslation(format, values), false);
    }

    static SimpleCommandExceptionType getException(String message, String... values) {
        ITextComponent textComponent = new TextComponentTranslation(message);

        int length = values.length;
        if (length > 0) {
            textComponent.appendText(" (");
            for (int idx = 0; idx < length; idx++) {
                if (idx > 0) textComponent.appendText(", ");
                ITextComponent suggestion = new TextComponentTranslation(values[idx]);
                textComponent.appendSibling(suggestion);
            }
            textComponent.appendText(")");
        }
        return new SimpleCommandExceptionType(textComponent);
    }
}
