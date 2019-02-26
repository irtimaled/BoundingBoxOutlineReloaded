package com.irtimaled.bbor.client.events;

public class Render {
    private final float partialTicks;

    public Render(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
