package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

class CustomKeyBinding extends KeyBinding {
    private final Key key;

    CustomKeyBinding(String description, int keyCode) {
        super(description, keyCode, KeyListener.Category);
        this.key = new Key(keyCode);
    }

    @Override
    public void setKeyCode(InputUtil.KeyCode input) {
        super.setKeyCode(input);
        int keyCode = input.getKeyCode();
        key.updateKeyCode(keyCode);
    }

    public Key getKey() {
        return key;
    }
}