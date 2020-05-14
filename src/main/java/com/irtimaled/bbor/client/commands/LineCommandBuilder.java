package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomLineProvider;
import com.irtimaled.bbor.common.models.Point;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

class LineCommandBuilder {
    private static final String WIDTH = "width";

    static LiteralArgumentBuilder<CommandSource> build(String command) {
        return Commands.literal(command)
                .then(Commands.literal(ArgumentNames.ADD)
                        .then(Commands.argument(ArgumentNames.FROM, Arguments.point())
                                .then(Commands.argument(ArgumentNames.TO, Arguments.point())
                                        .executes(LineCommandBuilder::addLine)
                                        .then(Commands.argument(WIDTH, Arguments.doubleArg())
                                                .executes(LineCommandBuilder::addLine)))))
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomLineProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.line.cleared.all");
                            return 0;
                        })
                        .then(Commands.argument(ArgumentNames.FROM, Arguments.coords())
                                .then(Commands.argument(ArgumentNames.TO, Arguments.coords())
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

    private static int addLine(CommandContext<CommandSource> context) throws CommandSyntaxException {
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
