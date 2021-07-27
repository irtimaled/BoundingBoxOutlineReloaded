package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.config.HexColor;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.Coords;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Supplier;

public class Arguments {
    public static BlockPosArgumentType coords() {
        return BlockPosArgumentType.blockPos();
    }

    public static Vec3ArgumentType point() {
        return Vec3ArgumentType.vec3();
    }

    public static IntegerArgumentType integer() {
        return IntegerArgumentType.integer();
    }

    public static IntegerArgumentType integer(int min, int max) {
        return IntegerArgumentType.integer(min, max);
    }

    public static DoubleArgumentType doubleArg() {
        return DoubleArgumentType.doubleArg();
    }

    public static StringArgumentType string() {
        return StringArgumentType.string();
    }

    public static BoolArgumentType bool() {
        return BoolArgumentType.bool();
    }

    public static ArgumentType<HexColor> hexColor() {
        return new HexColorArgument();
    }

    public static Coords getCoords(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return new Coords(getArgumentValueOrDefault(context, name, Vec3ArgumentType::getVec3, () -> context.getSource().getPosition()));
    }

    public static Point getPoint(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return new Point(getArgumentValueOrDefault(context, name, Vec3ArgumentType::getVec3, () -> context.getSource().getPosition()));
    }

    public static int getInteger(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return getArgumentValueOrDefault(context, name, IntegerArgumentType::getInteger, () -> 0);
    }

    public static double getDouble(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return getArgumentValueOrDefault(context, name, DoubleArgumentType::getDouble, () -> 0.0D);
    }

    public static String getString(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return getArgumentValueOrDefault(context, name, StringArgumentType::getString, () -> "");
    }

    public static boolean getBool(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return getArgumentValueOrDefault(context, name, BoolArgumentType::getBool, () -> false);
    }

    private static <T> T getArgumentValueOrDefault(CommandContext<ServerCommandSource> context,
                                                   String name,
                                                   ArgumentFetcher<T> getValue,
                                                   Supplier<T> defaultValue) throws CommandSyntaxException {
        try {
            return getValue.get(context, name);
        } catch (IllegalArgumentException exception) {
            return defaultValue.get();
        }
    }

    @FunctionalInterface
    private interface ArgumentFetcher<T> {
        T get(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException;
    }
}
