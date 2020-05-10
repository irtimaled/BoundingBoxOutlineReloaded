package com.irtimaled.bbor.client.interop;

import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface BlockProcessor {
    void process(BlockPos blockPos);
}
