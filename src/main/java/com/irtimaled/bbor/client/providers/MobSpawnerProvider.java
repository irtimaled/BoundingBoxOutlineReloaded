package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.interop.TileEntitiesHelper;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.tileentity.TileEntityMobSpawner;

public class MobSpawnerProvider implements IBoundingBoxProvider<BoundingBoxMobSpawner> {
    public Iterable<BoundingBoxMobSpawner> get(int dimensionId) {
        return TileEntitiesHelper.map(TileEntityMobSpawner.class, spawner -> {
            Coords coords = new Coords(spawner.getPos());
            return BoundingBoxMobSpawner.from(coords);
        });
    }
}
