package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.config.Setting;

public class BoolSettingButton extends Button {
    private final Setting<Boolean> setting;

    BoolSettingButton(int id, int x, int y, int width, String label, Setting<Boolean> setting) {
        super(id, x, y, width, label);
        this.setting = setting;
    }

    @Override
    protected int getHoverState(boolean p_getHoverState_1_) {
        return setting.get() ? 2 : 1;
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        setting.set(!setting.get());
    }
}
