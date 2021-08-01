package com.irtimaled.bbor.client.gui;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.irtimaled.bbor.client.config.Setting;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

class IntSettingSlider extends AbstractSlider {
    private final String format;
    private final Map<Integer, String> displayValues = new HashMap<>();

    final Setting<Integer> setting;
    final int minValue;

    IntSettingSlider(int width, int minValue, int maxValue, String format, Setting<Integer> setting) {
        super(width, maxValue - minValue);
        this.setting = setting;
        this.minValue = minValue;
        this.format = format;
        setInitialPosition();
        this.updateText();
    }

    protected void setInitialPosition() {
        this.setPosition(this.setting.get() - this.minValue);
    }

    IntSettingSlider addDisplayValue(int value, String displayValue) {
        displayValues.put(value, displayValue);
        if (setting.get() == value) {
            this.updateText();
        }
        return this;
    }

    IntSettingSlider addDisplayValueRange(int start, int end) {
        return addDisplayValueRange(start, end, String::valueOf);
    }

    IntSettingSlider addDisplayValueRange(int start, int end, Function<Integer, String> formatter) {
        Preconditions.checkArgument(start <= end);
        Preconditions.checkNotNull(formatter);
        IntStream.range(start, end).forEach(value -> addDisplayValue(value, formatter.apply(value)));
        return this;
    }

    protected Integer getSettingValue() {
        return minValue + getPosition();
    }

    private void updateText() {
        Integer value = setting.get();
        this.setMessage(new LiteralText(I18n.translate(format, displayValues.getOrDefault(value, value.toString()))));
    }

    @Override
    protected void onProgressChanged() {
        this.setting.set(this.getSettingValue());
        updateText();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder narrationMessageBuilder) {
        this.appendDefaultNarrations(narrationMessageBuilder);
    }
}
