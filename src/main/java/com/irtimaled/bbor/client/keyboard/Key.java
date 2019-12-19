package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.util.InputUtil;

import java.util.HashSet;
import java.util.Set;

public class Key {
    private int keyCode;
    private KeyHandler onKeyPress;
    private Set<Key> subKeys = new HashSet<>();
    private boolean triggeredSincePress;

    Key(int keyCode) {
        this.keyCode = keyCode;
    }

    public Key onKeyPressHandler(KeyHandler onKeyPress) {
        this.onKeyPress = onKeyPress;
        return this;
    }

    void updateKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    private int pressDuration = 0;

    private void runHandler(KeyHandler onKeyPress) {
        triggeredSincePress = true;
        onKeyPress.handle();
    }

    private void press() {
        for (Key subKey : subKeys) {
            subKey.triggeredSincePress = false;
        }
        triggeredSincePress = false;
        pressDuration++;
    }

    private void release() {
        try {
            for (Key subKey : subKeys) {
                if (subKey.pressDuration > 0) {
                    subKey.release();
                    return;
                }
                if (subKey.triggeredSincePress) {
                    return;
                }
            }
            if (onKeyPress != null && pressDuration > 0) {
                runHandler(onKeyPress);
            }
        } finally {
            pressDuration = 0;
        }
    }

    boolean handleKeyEvent(int keyCode, boolean isPressed) {
        if (this.keyCode == keyCode) {
            if (isPressed) {
                press();
            } else {
                release();
            }
            return true;
        } else if (this.pressDuration > 0) {
            for (Key subKey : subKeys) {
                if (subKey.handleKeyEvent(keyCode, isPressed)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Key register(String keyName) {
        InputUtil.KeyCode input = InputUtil.fromName(keyName);
        Key key = new Key(input.getKeyCode());
        subKeys.add(key);
        return key;
    }
}
