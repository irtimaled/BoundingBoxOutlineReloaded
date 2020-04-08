package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.IGuiEventListener;

interface IControl extends IGuiEventListener {
    void render(int mouseX, int mouseY);

    void setX(int x);

    void setY(int y);

    int getControlWidth();

    int getControlHeight();

    boolean getVisible();

    void setVisible(boolean visible);

    void filter(String lowerValue);

    default void close() {
    }
}
