package com.irtimaled.bbor.client.interop;

@FunctionalInterface
public interface BlockProcessor {
    boolean process(int x, int y, int z);
}
