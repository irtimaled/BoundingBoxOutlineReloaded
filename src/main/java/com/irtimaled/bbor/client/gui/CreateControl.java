package com.irtimaled.bbor.client.gui;

@FunctionalInterface
interface CreateControl {
    IControl create(Integer id, Integer x, Integer y, Integer width);
}
