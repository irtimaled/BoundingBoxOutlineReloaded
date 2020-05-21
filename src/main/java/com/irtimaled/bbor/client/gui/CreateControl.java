package com.irtimaled.bbor.client.gui;

@FunctionalInterface
interface CreateControl {
    AbstractControl create(Integer width);
}
