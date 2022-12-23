package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.SpawnableBlocksRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

public class BoundingBoxSpawnableBlocks extends AbstractBoundingBox {
    private static final AbstractRenderer<BoundingBoxSpawnableBlocks> RENDERER = CommonInterop.registerRenderer(BoundingBoxSpawnableBlocks.class, () -> new SpawnableBlocksRenderer());

    private final int baseX;
    private final int baseZ;
    private final IntSet blocks = IntSets.synchronize(new IntOpenHashSet(), this);

    public BoundingBoxSpawnableBlocks(int baseX, int baseZ) {
        super(BoundingBoxType.SpawnableBlocks);
        this.baseX = baseX;
        this.baseZ = baseZ;
    }

    public IntSet getBlockYs() {
        return blocks;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseZ() {
        return baseZ;
    }

    @Override
    public Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ) {
        return true;
    }

    @Override
    protected double getDistanceX(double x) {
        return 0;
    }

    @Override
    protected double getDistanceY(double y) {
        return 0;
    }

    @Override
    protected double getDistanceZ(double z) {
        return 0;
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
