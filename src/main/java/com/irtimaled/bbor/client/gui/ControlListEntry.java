package com.irtimaled.bbor.client.gui;

public abstract class ControlListEntry implements IControl {
    int index;
    ControlList list;
    private int x;
    private int y;
    private boolean visible = true;

    public void close() { }

    @Override
    public int getControlHeight() { return 24; }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean getVisible() {
        return visible;
    }
}
