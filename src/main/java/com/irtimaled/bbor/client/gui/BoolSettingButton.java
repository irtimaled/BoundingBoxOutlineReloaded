package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.config.ConfigManager;
import com.irtimaled.bbor.config.Setting;

public class BoolSettingButton extends BoolButton {
    private final Setting<Boolean> setting;

    BoolSettingButton(int width, String label, Setting<Boolean> setting) {
        super(width, label);
        this.setting = setting;
    }

    @Override
    public void onPressed() {
        ConfigManager.Toggle(this.setting);
    }

    @Override
    protected boolean getValue() {
        return this.setting.get();
    }
}
