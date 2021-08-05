package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.WorldSpawnRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxWorldSpawn extends BoundingBoxCuboid {
    private static final AbstractRenderer<BoundingBoxWorldSpawn> RENDERER = CommonInterop.registerRenderer(BoundingBoxWorldSpawn.class, () -> new WorldSpawnRenderer());

    public BoundingBoxWorldSpawn(Coords minCoords, Coords maxCoords, BoundingBoxType type) {
        super(minCoords, maxCoords, type);
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
