package com.irtimaled.bbor.client.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

class CommandHelper {
    static void feedback(CommandContext<ServerCommandSource> context, String format, Object... values) {
        context.getSource().sendFeedback(Text.translatable(format, values), false);
    }

    static boolean lastNodeIsLiteral(CommandContext<ServerCommandSource> context, String literal) {
        CommandNode lastNode = getLastNode(context);
        if (lastNode instanceof LiteralCommandNode) {
            LiteralCommandNode literalCommandNode = (LiteralCommandNode) lastNode;
            return literalCommandNode.getLiteral().equals(literal);
        }
        return false;
    }

    private static CommandNode getLastNode(CommandContext<ServerCommandSource> context) {
        ParsedCommandNode[] nodes = context.getNodes().toArray(new ParsedCommandNode[0]);
        if (nodes.length == 0) return null;
        return nodes[nodes.length - 1].getNode();
    }
}
