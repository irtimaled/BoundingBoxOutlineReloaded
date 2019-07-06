package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;

class CustomKeyBinding extends KeyBinding {
    private final Key key;

    CustomKeyBinding(String description, int keyCode) {
        super(description, keyCode, KeyListener.Category);
        this.key = new Key(keyCode);
    }

    @Override
    public void setKeyModifierAndCode(KeyModifier keyModifier, InputMappings.Input input) {
        super.setKeyModifierAndCode(keyModifier, input);
        int keyCode = input.getKeyCode();
        key.updateKeyCode(keyCode);
    }

    public Key getCustomKey() {
        return key;
    }
}
