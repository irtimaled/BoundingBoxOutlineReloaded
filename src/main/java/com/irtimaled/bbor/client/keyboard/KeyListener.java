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
    private static Set<Key> keys = new HashSet<>();

    public static void init() {
        mainWindowHandle = minecraft.mainWindow.getHandle();
        GLFW.glfwSetKeyCallback(mainWindowHandle, KeyListener::onKeyEvent);
    }

    public static Key register(String description, int keyCode, String category) {
        Key key = new Key(description, keyCode, category);
        keys.add(key);
        return key;
    }

    private static void onKeyEvent(long windowHandle, int keyCode, int scanCode, int action, int modifiers) {
        if (windowHandle == mainWindowHandle && minecraft.currentScreen == null && keyCode != -1 && !InputMappings.func_216506_a(mainWindowHandle, 292)) {
            InputMappings.Input input = InputMappings.getInputByCode(keyCode, scanCode);
            for (Key key : keys) {
                if (key.getInput() == input) {
                    switch (action) {
                        case GLFW.GLFW_PRESS:
                            key.press();
                            break;
                        case GLFW.GLFW_REPEAT:
                            key.repeat();
                            break;
                        case GLFW.GLFW_RELEASE:
                            key.release();
                            return;
                    }
                    if (minecraft.currentScreen != null)
                        key.release();
                    return;
                }
            }
        }
        minecraft.keyboardListener.onKeyEvent(windowHandle, keyCode, scanCode, action, modifiers);
    }

    public static KeyBinding[] keyBindings() {
        return keys.stream().toArray(KeyBinding[]::new);
    }
}
