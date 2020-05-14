package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.providers.CustomSphereProvider;
import com.irtimaled.bbor.common.models.Point;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

class SphereCommandBuilder {
    public static final String RADIUS = "radius";

    static LiteralArgumentBuilder<CommandSource> build(String command) {
        return Commands.literal(command)
                .then(Commands.literal(ArgumentNames.ADD)
                        .then(Commands.argument(ArgumentNames.POS, Arguments.point())
                                .then(Commands.argument(RADIUS, Arguments.integer())
                                        .executes(SphereCommandBuilder::addSphere)))
                        .then(Commands.argument(RADIUS, Arguments.integer())
                                .executes(SphereCommandBuilder::addSphere)))
                .then(Commands.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomSphereProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.sphere.cleared.all");
                            return 0;
                        })
                        .then(Commands.argument(ArgumentNames.FROM, Arguments.coords())
                                .then(Commands.argument(ArgumentNames.TO, Arguments.coords())
                                        .executes(context -> {
                                            Point pos = Arguments.getPoint(context, ArgumentNames.POS).snapXZ(0.5d);
                                            boolean removed = CustomSphereProvider.remove(pos);

                                            String format = removed ? "bbor.commands.sphere.cleared" : "bbor.commands.sphere.notFound";
                                            CommandHelper.feedback(context, format,
                                                    pos.getX(), pos.getY(), pos.getZ());
                                            return 0;
                                        }))));
    }

    private static int addSphere(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Point pos = Arguments.getPoint(context, ArgumentNames.POS).snapXZ(0.5d);
        int radius = Arguments.getInteger(context, RADIUS);
        CustomSphereProvider.add(pos, radius);

        CommandHelper.feedback(context, "bbor.commands.sphere.added",
                pos.getX(), pos.getY(), pos.getZ(), radius);
        return 0;
    }
}
