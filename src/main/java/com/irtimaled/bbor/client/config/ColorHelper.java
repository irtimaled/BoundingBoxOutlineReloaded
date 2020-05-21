package com.irtimaled.bbor.client.config;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorHelper {
    private static final Map<HexColor, Color> colorMap = new HashMap<>();

    private static Color getColor(HexColor value) {
        return colorMap.computeIfAbsent(value, ColorHelper::decodeColor);
    }

    private static Color decodeColor(HexColor hexColor) {
        try {
            int color = Integer.decode(hexColor.getValue());
            return new Color(color, hexColor.hasAlpha());
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Color getColor(Setting<HexColor> value) {
        if (value == null) return Color.WHITE;

        Color color = getColor(value.get());
        return color != null ? color : getColor(value.defaultValue);
    }
}
