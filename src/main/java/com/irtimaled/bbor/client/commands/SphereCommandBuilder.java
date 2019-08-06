package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.client.providers.CustomSphereProvider;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

class SphereCommandBuilder {
    public static final String RADIUS = "radius";

    static LiteralArgumentBuilder<ServerCommandSource> build(String command) {
        return CommandManager.literal(command)
                .then(CommandManager.literal(ArgumentNames.ADD)
                        .then(CommandManager.argument(ArgumentNames.POS, Arguments.point())
                                .then(CommandManager.argument(RADIUS, Arguments.integer())
                                        .executes(SphereCommandBuilder::addSphere)))
                        .then(CommandManager.argument(RADIUS, Arguments.integer())
                                .executes(SphereCommandBuilder::addSphere)))
                .then(CommandManager.literal(ArgumentNames.CLEAR)
                        .executes(context -> {
                            CustomSphereProvider.clear();

                            CommandHelper.feedback(context, "bbor.commands.sphere.cleared.all");
                            return 0;
                        })
                        .then(CommandManager.argument(ArgumentNames.FROM, Arguments.coords())
                                .then(CommandManager.argument(ArgumentNames.TO, Arguments.coords())
                                        .executes(context -> {
                                            Point pos = Arguments.getPoint(context, ArgumentNames.POS).snapXZ(0.5d);
                                            boolean removed = CustomSphereProvider.remove(pos);

                                            String format = removed ? "bbor.commands.sphere.cleared" : "bbor.commands.sphere.notFound";
                                            CommandHelper.feedback(context, format,
                                                    pos.getX(), pos.getY(), pos.getZ());
                                            return 0;
                                        }))));
    }

    private static int addSphere(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Point pos = Arguments.getPoint(context, ArgumentNames.POS).snapXZ(0.5d);
        int radius = Arguments.getInteger(context, RADIUS);
        CustomSphereProvider.add(pos, radius);

        CommandHelper.feedback(context, "bbor.commands.sphere.added",
                pos.getX(), pos.getY(), pos.getZ(), radius);
        return 0;
    }
}
