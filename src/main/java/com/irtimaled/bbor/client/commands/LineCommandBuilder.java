package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.client.providers.CustomLineProvider;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

class LineCommandBuilder {
    private static final String WIDTH = "width";

    static LiteralArgumentBuilder<ServerCommandSource> build(String command) {
        return CommandManager.literal(command)
                .then(CommandManager.literal(ArgumentNames.ADD)
                        .then(CommandManager.argument(ArgumentNames.FROM, Arguments.point())
                                .then(CommandManager.argument(ArgumentNames.TO, Arguments.point())
                                        .executes(LineCommandBuilder::addLine)
                                        .then(CommandManager.argument(WIDTH, Arguments.doubleArg())
                                                .executes(LineCommandBuilder::addLine)))))
                .then(CommandManager.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomLineProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.line.cleared.all");
                            return 0;
                        })
                        .then(CommandManager.argument(ArgumentNames.FROM, Arguments.coords())
                                .then(CommandManager.argument(ArgumentNames.TO, Arguments.coords())
                                        .executes(context -> {
                                            Point from = Arguments.getPoint(context, ArgumentNames.FROM).snapXZ(0.5d);
                                            Point to = Arguments.getPoint(context, ArgumentNames.TO).snapXZ(0.5d);
                                            boolean removed = CustomLineProvider.remove(from, to);

                                            String format = removed ? "bbor.commands.line.cleared" : "bbor.commands.line.notFound";
                                            CommandHelper.feedback(context, format,
                                                    from.getX(), from.getY(), from.getZ(),
                                                    to.getX(), to.getY(), to.getZ());
                                            return 0;
                                        }))));
    }

    private static int addLine(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Point from = Arguments.getPoint(context, ArgumentNames.FROM).snapXZ(0.5d);
        Point to = Arguments.getPoint(context, ArgumentNames.TO).snapXZ(0.5d);
        Double width = Arguments.getDouble(context, WIDTH);
        CustomLineProvider.add(from, to, width);

        CommandHelper.feedback(context, "bbor.commands.line.added",
                from.getX(), from.getY(), from.getZ(),
                to.getX(), to.getY(), to.getZ());
        return 0;
    }
}
