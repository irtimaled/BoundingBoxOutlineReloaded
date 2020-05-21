package com.irtimaled.bbor.client.gui;

public abstract class ControlListEntry implements IControl {
    int index;
    private int x;
    private int y;
    private boolean visible = true;

    public void close() {
    }

    public int getControlHeight() {
        return 24;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public int getControlTop() {
        return y;
    }

    public void done() {
    }

    public abstract void filter(String lowerValue);
}
