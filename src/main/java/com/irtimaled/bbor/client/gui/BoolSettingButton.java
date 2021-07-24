package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.config.Setting;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

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

    @Override
    public void appendNarrations(NarrationMessageBuilder narrationMessageBuilder) {
        // TODO
        this.appendDefaultNarrations(narrationMessageBuilder);
    }
}
