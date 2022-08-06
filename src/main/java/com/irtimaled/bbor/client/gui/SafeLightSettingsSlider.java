package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.config.Setting;
import net.minecraft.client.resource.language.I18n;

public class SafeLightSettingsSlider extends IntSettingSlider {
    SafeLightSettingsSlider(int width, Setting<Integer> setting) {
        super(width, 0, 15, I18n.translate("bbor.options.safe_light", "%s"), setting);
        this.setInitialPosition();
    }
}
