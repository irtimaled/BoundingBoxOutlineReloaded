package com.irtimaled.bbor.client.commands;

import com.irtimaled.bbor.client.config.HexColor;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HexColorArgument implements ArgumentType<HexColor> {
    private static final List<String> EXAMPLES = Arrays.asList("#000000", "#ffffff");
    public static final DynamicCommandExceptionType INVALID_HEX_COLOR = new DynamicCommandExceptionType(v -> new LiteralMessage("Invalid hex color, expected six digit hex color starting with # but found '" + v + "'"));
    public static final SimpleCommandExceptionType EXPECTED_HEX_COLOR = new SimpleCommandExceptionType(new LiteralMessage("Expected hex color"));

    @Override
    public <S> HexColor parse(StringReader reader) throws CommandSyntaxException {
        String value = reader.getRemaining().split(" ")[0].toLowerCase();
        if (value.isEmpty()) {
            throw EXPECTED_HEX_COLOR.createWithContext(reader);
        }

        HexColor color = HexColor.from(value);
        if(color != null) {
            reader.setCursor(reader.getCursor() + 7);
            return color;
        }

        throw INVALID_HEX_COLOR.createWithContext(reader, value);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (builder.getRemaining().length() == 0) builder.suggest("#");
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
