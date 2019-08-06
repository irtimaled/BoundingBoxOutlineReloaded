package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.config.Setting;
import net.minecraft.client.resource.language.I18n;

class MaxYSettingSlider extends IntSettingSlider {
    private final int actualMinValue;

    MaxYSettingSlider(int width, int minValue, Setting<Integer> setting) {
        super(width, minValue - 2, 127, I18n.translate("bbor.options.maxY", "%s"), setting);
        this.actualMinValue = minValue;
        this.setInitialPosition();
        this.addDisplayValue(-1, I18n.translate("bbor.options.maxY.activated"));
        this.addDisplayValue(0, I18n.translate("bbor.options.maxY.player"));
        this.addDisplayValue(63, I18n.translate("bbor.options.maxY.seaLevel"));
    }

    @Override
    protected Integer getSettingValue() {
        Integer value = super.getSettingValue();
        if (value >= actualMinValue)
            return value;
        return (value + 1) - actualMinValue;
    }

    @Override
    protected void setInitialPosition() {
        if (actualMinValue != minValue + 2) return;

        int value = setting.get();
        if (value < actualMinValue)
            value = (value - 1) + actualMinValue;

        setPosition(value - minValue);
    }
}
