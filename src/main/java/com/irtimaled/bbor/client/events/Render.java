package com.irtimaled.bbor.client.events;

public class Render {
    private final int dimensionId;

    public Render(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public int getDimensionId() {
        return dimensionId;
    }
}
