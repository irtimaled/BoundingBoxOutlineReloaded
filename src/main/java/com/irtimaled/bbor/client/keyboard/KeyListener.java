package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.settings.KeyBinding;

import java.util.HashSet;
import java.util.Set;

public class KeyListener {
    private static Set<Key> keys = new HashSet<>();

    public static Key register(String description, int keyCode, String category) {
        Key key = new Key(description, keyCode, category);
        keys.add(key);
        return key;
    }

    public static KeyBinding[] keyBindings() {
        return keys.toArray(new KeyBinding[0]);
    }

    public static boolean setKeyBindState(int keyCode, boolean pressed) {
        for (Key key : keys) {
            if (key.getKeyCode() == keyCode) {
                if (!pressed) {
                    key.release();
                } else if (key.isPressed()) {
                    key.repeat();
                } else {
                    key.press();
                }
                return true;
            }
        }
        return false;
    }
}
