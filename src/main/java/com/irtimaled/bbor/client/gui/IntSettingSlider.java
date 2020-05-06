package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.config.Setting;
import net.minecraft.client.resources.I18n;

import java.util.HashMap;
import java.util.Map;

class IntSettingSlider extends AbstractSlider implements IRenderableControl {
    private final String format;
    private final Map<Integer, String> displayValues = new HashMap<>();

    final Setting<Integer> setting;
    final int minValue;
    final int range;

    IntSettingSlider(int id, int x, int y, int width, int minValue, int maxValue, String format, Setting<Integer> setting) {
        super(id, x, y, width);
        this.setting = setting;
        this.minValue = minValue;
        this.format = format;
        this.range = maxValue - minValue;
        this.setProgress(getSliderValue());
        this.updateText();
    }

    IntSettingSlider addDisplayValue(int value, String displayValue) {
        displayValues.put(value, displayValue);
        if (setting.get() == value) {
            this.updateText();
        }
        return this;
    }

    private String getDisplayValue() {
        Integer value = setting.get();
        return I18n.format(format, displayValues.getOrDefault(value, value.toString()));
    }

    protected Integer getSettingValue() {
        return minValue + (int) (range * progress);
    }

    protected double getSliderValue() {
        return (setting.get() - minValue) / (double) range;
    }

    @Override
    protected void updateText() {
        this.displayString = this.getDisplayValue();
    }

    @Override
    protected void onProgressChanged() {
        this.setting.set(this.getSettingValue());
    }
}
