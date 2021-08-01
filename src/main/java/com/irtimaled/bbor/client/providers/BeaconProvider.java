package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.TileEntitiesHelper;
import com.irtimaled.bbor.client.models.BoundingBoxBeacon;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.mixin.access.IBeaconBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;

public class BeaconProvider implements IBoundingBoxProvider<BoundingBoxBeacon> {
    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.Beacon);
    }

    @Override
    public Iterable<BoundingBoxBeacon> get(DimensionId dimensionId) {
        return TileEntitiesHelper.map(BeaconBlockEntity.class, beacon -> {
            int levels = ((IBeaconBlockEntity) beacon).getLevel();
            Coords coords = new Coords(beacon.getPos());
            return BoundingBoxBeacon.from(coords, levels);
        });
    }
}
