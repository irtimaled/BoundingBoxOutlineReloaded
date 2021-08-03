package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.SpawnableBlocksRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class BoundingBoxSpawnableBlocks extends AbstractBoundingBox {
    private static final AbstractRenderer<BoundingBoxSpawnableBlocks> RENDERER = ClientRenderer.registerRenderer(BoundingBoxSpawnableBlocks.class, new SpawnableBlocksRenderer());

    private final Set<BlockPos> blocks = new HashSet<>();

    public BoundingBoxSpawnableBlocks() {
        super(BoundingBoxType.SpawnableBlocks);
    }

    public Set<BlockPos> getBlocks() {
        return blocks;
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
