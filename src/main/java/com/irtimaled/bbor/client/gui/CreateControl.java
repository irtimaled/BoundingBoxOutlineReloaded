package com.irtimaled.bbor.client.gui;

@FunctionalInterface
interface CreateControl {
    IControl create(Integer x, Integer y, Integer width);
}
