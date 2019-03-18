package com.irtimaled.bbor.client.keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class Key extends KeyBinding {
    private InputMappings.Input input;
    private KeyHandler onKeyPress;
    private KeyHandler onLongKeyPress;
    private int longPressDuration;

    Key(String description, int keyCode, String category) {
        super(description, keyCode, category);
    }

    public Key onKeyPressHandler(KeyHandler onKeyPress) {
        this.onKeyPress = onKeyPress;
        return this;
    }

    public Key onLongKeyPressHandler(int duration, KeyHandler onLongKeyPress) {
        this.longPressDuration = duration;
        this.onLongKeyPress = onLongKeyPress;
        return this;
    }

    InputMappings.Input getInput() {
        if (input == null)
            return getDefault();
        return input;
    }

    @Override
    public void bind(InputMappings.Input input) {
        this.input = input;
        super.bind(input);
    }

    private int pressDuration = 0;

    @Override
    public boolean isPressed() {
        return pressDuration == 1;
    }

    void release() {
        if (onKeyPress != null && (onLongKeyPress == null || pressDuration < longPressDuration)) {
            onKeyPress.handle();
        }

        pressDuration = 0;
    }

    void repeat() {
        if (onLongKeyPress == null) return;

        if (pressDuration <= longPressDuration) {
            pressDuration++;
        }

        if (pressDuration == longPressDuration) {
            onLongKeyPress.handle();
        }
    }

    void press() {
        pressDuration++;
    }
}
