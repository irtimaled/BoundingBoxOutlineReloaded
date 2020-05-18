package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.Point;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.Vec3Argument;

import java.util.function.Supplier;

public class Arguments {
    public static BlockPosArgument coords() {
        return BlockPosArgument.blockPos();
    }

    public static Vec3Argument point() {
        return Vec3Argument.vec3();
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

    public static Coords getCoords(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return new Coords(getArgumentValueOrDefault(context, name, Vec3Argument::getVec3, () -> context.getSource().getPos()));
    }

    public static Point getPoint(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return new Point(getArgumentValueOrDefault(context, name, Vec3Argument::getVec3, () -> context.getSource().getPos()));
    }

    public static int getInteger(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return getArgumentValueOrDefault(context, name, IntegerArgumentType::getInteger, () -> 0);
    }

    public static double getDouble(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return getArgumentValueOrDefault(context, name, DoubleArgumentType::getDouble, () -> 0.0D);
    }

    public static String getString(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return getArgumentValueOrDefault(context, name, StringArgumentType::getString, () -> "");
    }

    public static boolean getBool(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return getArgumentValueOrDefault(context, name, BoolArgumentType::getBool, () -> false);
    }

    private static <T> T getArgumentValueOrDefault(CommandContext<CommandSource> context,
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
        T get(CommandContext<CommandSource> context, String name) throws CommandSyntaxException;
    }
}
