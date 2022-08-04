package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

class CustomKeyBinding extends KeyBinding {
    private final Key key;
    private final InputUtil.Key forgeKey;

    CustomKeyBinding(String description, String translationKey) {
        super(description, InputUtil.fromTranslationKey(translationKey).getCode(), KeyListener.Category);
        this.forgeKey = InputUtil.fromTranslationKey(translationKey);
        this.key = new Key(this.forgeKey.getCode());
    }

    @Override
    public void setBoundKey(InputUtil.Key input) {
        super.setBoundKey(input);
        int keyCode = input.getCode();
        key.updateKeyCode(keyCode);
    }

    public InputUtil.Key getKey() {
        return this.forgeKey;
    }

    public Key getBBORKey() {
        return key;
    }
}
