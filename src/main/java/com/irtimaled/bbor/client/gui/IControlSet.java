package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.Element;

import java.util.List;

public interface IControlSet extends IFocusableControl, Element {
    List<? extends IControl> controls();

    IControl getFocused();

    void setFocused(IControl control);

    boolean isDragging();

    void setDragging(boolean dragging);

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IControl control : this.controls()) {
            if (control.isVisible() && control.mouseClicked(mouseX, mouseY, button)) {
                IControl focused = getFocused();
                if (focused != null && focused != control) {
                    focused.clearFocus();
                }
                this.setFocused(control);
                if (button == 0) this.setDragging(true);
                return true;
            }
        }
        return false;
    }

    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        IControl focused = this.getFocused();
        return focused != null && focused.mouseReleased(mouseX, mouseY, button);
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        IControl focused = this.getFocused();
        return focused != null && this.isDragging() && button == 0 && focused.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        IControl focused = this.getFocused();
        return focused != null && focused.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    default boolean keyPressed(int key, int scanCode, int modifiers) {
        IControl focused = this.getFocused();
        return focused != null && focused.keyPressed(key, scanCode, modifiers);
    }

    default boolean keyReleased(int key, int scanCode, int modifiers) {
        IControl focused = this.getFocused();
        return focused != null && focused.keyReleased(key, scanCode, modifiers);
    }

    default boolean charTyped(char character, int modifiers) {
        IControl focused = this.getFocused();
        return focused != null && focused.charTyped(character, modifiers);
    }

    // TODO: Fix this
//    default boolean changeFocus(boolean moveForward) {
//        IControl focused = this.getFocused();
//        if (focused != null && focused.changeFocus(moveForward)) {
//            return true;
//        }
//
//        List<? extends IControl> controls = this.controls();
//        int controlIndex = controls.indexOf(focused);
//        int newIndex;
//        if (focused != null && controlIndex >= 0) {
//            newIndex = controlIndex + (moveForward ? 1 : 0);
//        } else if (moveForward) {
//            newIndex = 0;
//        } else {
//            newIndex = controls.size();
//        }
//
//        if (ListHelper.findNextMatch(controls, newIndex, moveForward,
//                c -> c.changeFocus(moveForward), this::setFocused)) return true;
//        this.setFocused(null);
//        return false;
//    }

    default void clearFocus() {
        IControl focused = getFocused();
        if (focused != null) {
            setFocused(null);
            focused.clearFocus();
        }
    }
}
