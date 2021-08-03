package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.SpawningSphereRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class BoundingBoxSpawningSphere extends BoundingBoxSphere {
    private static final AbstractRenderer<BoundingBoxSpawningSphere> RENDERER = ClientRenderer.registerRenderer(BoundingBoxSpawningSphere.class, new SpawningSphereRenderer());

    public static final int SAFE_RADIUS = 24;
    public static final int SPAWN_RADIUS = 128;

    private final Set<BlockPos> blocks = new HashSet<>();
    private Integer spawnableCount;

    public BoundingBoxSpawningSphere(Point point) {
        super(point, SPAWN_RADIUS, BoundingBoxType.AFKSphere);
    }

    public Set<BlockPos> getBlocks() {
        return blocks;
    }

    public boolean isWithinSphere(Point point) {
        return this.getPoint().getDistance(point) <= getRadius() + 0.5D;
    }

    public void setSpawnableCount(int count) {
        this.spawnableCount = count;
    }

    public Integer getSpawnableSpacesCount() {
        return this.spawnableCount;
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
