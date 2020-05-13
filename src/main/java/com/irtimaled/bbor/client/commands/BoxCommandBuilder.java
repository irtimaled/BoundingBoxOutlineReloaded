package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomBoxProvider;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

class BoxCommandBuilder {
    static LiteralArgumentBuilder<CommandSource> build(String command) {
        return Commands.literal(command)
                .then(Commands.literal(ArgumentNames.ADD)
                        .then(Commands.argument(ArgumentNames.FROM, Arguments.coords())
                                .executes(BoxCommandBuilder::addBox)
                                .then(Commands.argument(ArgumentNames.TO, Arguments.coords())
                                        .executes(BoxCommandBuilder::addBox))))
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomBoxProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.box.cleared.all");
                            return 0;
                        })
                        .then(Commands.argument(ArgumentNames.FROM, Arguments.coords())
                                .then(Commands.argument(ArgumentNames.TO, Arguments.coords())
                                        .executes(context -> {
                                            Coords from = Arguments.getCoords(context, ArgumentNames.FROM);
                                            Coords to = Arguments.getCoords(context, ArgumentNames.TO);
                                            Coords minCoords = getMinCoords(from, to);
                                            Coords maxCoords = getMaxCoords(from, to);
                                            boolean removed = CustomBoxProvider.remove(minCoords, maxCoords);

                                            String format = removed ? "bbor.commands.box.cleared" : "bbor.commands.box.notFound";
                                            CommandHelper.feedback(context, format,
                                                    from.getX(), from.getY(), from.getZ(),
                                                    to.getX(), to.getY(), to.getZ());
                                            return 0;
                                        }))));
    }

    private static int addBox(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Coords from = Arguments.getCoords(context, ArgumentNames.FROM);
        Coords to = Arguments.getCoords(context, ArgumentNames.TO);
        Coords minCoords = getMinCoords(from, to);
        Coords maxCoords = getMaxCoords(from, to);
        CustomBoxProvider.add(minCoords, maxCoords);

        CommandHelper.feedback(context, "bbor.commands.box.added",
                from.getX(), from.getY(), from.getZ(),
                to.getX(), to.getY(), to.getZ());
        return 0;
    }

    private static Coords getMaxCoords(Coords from, Coords to) {
        return new Coords(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
    }

    private static Coords getMinCoords(Coords from, Coords to) {
        return new Coords(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
    }
}
