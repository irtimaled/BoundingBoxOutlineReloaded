package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashSet;
import java.util.Set;

public class KeyListener {
    private static final MinecraftClient minecraft = MinecraftClient.getInstance();
    private static final Set<Key> keys = new HashSet<>();
    private static final Set<CustomKeyBinding> keyBindings = new HashSet<>();
    public static final String Category = "Bounding Box Outline Reloaded";

    public static Key register(String description, String keyName) {
        CustomKeyBinding keyBinding = new CustomKeyBinding(description, keyName);
        keyBindings.add(keyBinding);

        Key key = keyBinding.getBBORKey();
        keys.add(key);
        return key;
    }

    public static boolean onKeyEvent(long windowHandle, int keyCode, int scanCode, int action, int modifiers) {
        return minecraft.currentScreen == null &&
                keyCode != -1 &&
                !InputUtil.isKeyPressed(windowHandle, 292) &&
                handleKeyEvent(keyCode, action > 0);
    }

    private static boolean handleKeyEvent(int keyCode, boolean isPressed) {
        for (Key key : keys) {
            if (key.handleKeyEvent(keyCode, isPressed)) {
                return true;
            }
        }
        return false;
    }

    public static KeyBinding[] keyBindings() {
        return keyBindings.toArray(new KeyBinding[0]);
    }
}
