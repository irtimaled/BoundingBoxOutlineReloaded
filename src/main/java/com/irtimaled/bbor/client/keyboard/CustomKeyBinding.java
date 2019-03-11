package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

class CustomKeyBinding extends KeyBinding {
    private final Key key;

    CustomKeyBinding(String description, int keyCode) {
        super(description, keyCode, KeyListener.Category);
        this.key = new Key(keyCode);
    }

    @Override
    public void bind(InputMappings.Input input) {
        super.bind(input);
        int keyCode = input.getKeyCode();
        key.updateKeyCode(keyCode);
    }

    public Key getCustomKey() {
        return key;
    }
}