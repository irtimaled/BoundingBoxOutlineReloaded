package com.irtimaled.bbor.client.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentTranslation;

class CommandHelper {
    static void feedback(CommandContext<CommandSource> context, String format, Object... values) {
        context.getSource().sendFeedback(new TextComponentTranslation(format, values), false);
    }
}
