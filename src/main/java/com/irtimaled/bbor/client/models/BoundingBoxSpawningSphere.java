package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.SpawningSphereRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.interop.CommonInterop;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class BoundingBoxSpawningSphere extends BoundingBoxSphere {
    private static final AbstractRenderer<BoundingBoxSpawningSphere> RENDERER = CommonInterop.registerRenderer(BoundingBoxSpawningSphere.class, () -> new SpawningSphereRenderer());

    public static final int SAFE_RADIUS = 24;
    public static final int SPAWN_RADIUS = 128;

    private final Set<BlockPos> blocksAllTime = new ObjectLinkedOpenHashSet<>();
    private final Set<BlockPos> blocksNightOnly = new ObjectLinkedOpenHashSet<>();
    private Integer spawnableCount;

    public BoundingBoxSpawningSphere(Point point) {
        super(point, SPAWN_RADIUS, BoundingBoxType.AFKSphere);
    }

    public Set<BlockPos> getBlocksAllTime() {
        return blocksAllTime;
    }

    public Set<BlockPos> getBlocksNightOnly() {
        return blocksNightOnly;
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
