package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.config.Setting;
import net.minecraft.client.resources.I18n;

class MaxYSettingSlider extends IntSettingSlider {
    private final int actualMinValue;

    MaxYSettingSlider(int width, int minValue, Setting<Integer> setting) {
        super(width, minValue - 2, 127, I18n.format("bbor.options.maxY", "%s"), setting);
        this.actualMinValue = minValue;
        this.setProgress(getSliderValue());
        this.addDisplayValue(-1, I18n.format("bbor.options.maxY.activated"));
        this.addDisplayValue(0, I18n.format("bbor.options.maxY.player"));
        this.addDisplayValue(63, I18n.format("bbor.options.maxY.seaLevel"));
    }

    @Override
    protected Integer getSettingValue() {
        Integer value = super.getSettingValue();
        if (value >= actualMinValue)
            return value;
        return (value + 1) - actualMinValue;
    }

    @Override
    protected double getSliderValue() {
        int value = setting.get();
        if (value < actualMinValue)
            value = (value - 1) + actualMinValue;

        return (value - minValue) / (double) range;
    }
}
