package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.MobSpawnerRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxMobSpawner extends BoundingBoxCuboid {
    private static final AbstractRenderer<BoundingBoxMobSpawner> RENDERER = ClientRenderer.registerRenderer(BoundingBoxMobSpawner.class, new MobSpawnerRenderer());

    private final Coords coords;

    private BoundingBoxMobSpawner(Coords coords, Coords minCoords, Coords maxCoords) {
        super(minCoords, maxCoords, BoundingBoxType.MobSpawner);
        this.coords = coords;
    }

    public static BoundingBoxMobSpawner from(Coords coords) {
        Coords minCoords = new Coords(coords.getX() - 5,
                coords.getY() - 1,
                coords.getZ() - 5);
        Coords maxCoords = new Coords(coords.getX() + 5,
                coords.getY() + 2,
                coords.getZ() + 5);
        return new BoundingBoxMobSpawner(coords, minCoords, maxCoords);
    }

    public Coords getCoords() {
        return coords;
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
