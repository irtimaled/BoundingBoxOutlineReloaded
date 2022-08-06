package com.irtimaled.bbor.client.config;

public class HexColor {
    private final String value;
    private final boolean hasAlpha;

    private HexColor(String value, boolean hasAlpha) {
        this.value = value;
        this.hasAlpha = hasAlpha;
    }

    public static HexColor from(String value) {
        String lowerValue = value.toLowerCase();
        if (lowerValue.length() == 7 &&
                lowerValue.matches("#[0-9a-f]{6}")) return new HexColor(lowerValue, false);
        if (lowerValue.length() == 9 &&
                lowerValue.matches("#[0-9a-f]{8}")) return new HexColor(lowerValue, true);

        return null;
    }

    public static HexColor random() {
        return from(String.format("#%06x", (int) (Math.random() * 0x1000000)));
    }

    public String getValue() {
        return value;
    }

    public boolean hasAlpha() {
        return hasAlpha;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HexColor hexColor = (HexColor) obj;
        return value.equals(hexColor.value);
    }
}
