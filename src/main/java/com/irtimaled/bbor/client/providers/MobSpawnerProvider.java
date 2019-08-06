package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.TileEntitiesHelper;
import com.irtimaled.bbor.client.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.block.entity.MobSpawnerBlockEntity;

public class MobSpawnerProvider implements IBoundingBoxProvider<BoundingBoxMobSpawner> {
    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.MobSpawner);
    }

    @Override
    public Iterable<BoundingBoxMobSpawner> get(DimensionId dimensionId) {
        return TileEntitiesHelper.map(MobSpawnerBlockEntity.class, spawner -> {
            Coords coords = new Coords(spawner.getPos());
            return BoundingBoxMobSpawner.from(coords);
        });
    }
}
