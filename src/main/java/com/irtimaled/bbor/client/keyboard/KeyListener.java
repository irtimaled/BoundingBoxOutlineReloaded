package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KeyListener {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static long mainWindowHandle;
    private static final Set<Key> keys = new HashSet<>();
    private static final Set<CustomKeyBinding> keyBindings = new HashSet<>();
    public static final String Category = "Bounding Box Outline Reloaded";

    public static void init() {
        mainWindowHandle = minecraft.getMainWindow().getHandle();
        GLFW.glfwSetKeyCallback(mainWindowHandle, KeyListener::onKeyEvent);
    }

    public static Key register(String description, String keyName) {
        InputMappings.Input input = InputMappings.getInputByName(keyName);
        CustomKeyBinding keyBinding = new CustomKeyBinding(description, input.getKeyCode());
        keyBindings.add(keyBinding);

        Key key = keyBinding.getCustomKey();
        keys.add(key);
        return key;
    }

    private static void onKeyEvent(long windowHandle, int keyCode, int scanCode, int action, int modifiers) {
        boolean isPressed = action > 0;
        if (windowHandle == mainWindowHandle &&
                minecraft.currentScreen == null &&
                keyCode != -1 &&
                !InputMappings.isKeyDown(mainWindowHandle, 292) &&
                handleKeyEvent(keyCode, isPressed))
            return;
        minecraft.keyboardListener.onKeyEvent(windowHandle, keyCode, scanCode, action, modifiers);
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
