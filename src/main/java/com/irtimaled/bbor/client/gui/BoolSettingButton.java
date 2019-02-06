package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.config.Setting;

public class BoolSettingButton extends AbstractButton {
    private final Setting<Boolean> setting;

    BoolSettingButton(int x, int y, int width, String label, Setting<Boolean> setting) {
        super(x, y, width, label);
        this.setting = setting;
    }

    @Override
    protected int getState() {
        return active ? setting.get() ? 2 : 1 : 0;
    }

    @Override
    public void onPressed() {
        setting.set(!setting.get());
    }
}
