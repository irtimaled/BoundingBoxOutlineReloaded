package com.irtimaled.bbor.client.events;

public class SeedCommandTyped {
    private final Long seed;

    public SeedCommandTyped(Long seed) {
        this.seed = seed;
    }

    public Long getSeed() {
        return seed;
    }
}
