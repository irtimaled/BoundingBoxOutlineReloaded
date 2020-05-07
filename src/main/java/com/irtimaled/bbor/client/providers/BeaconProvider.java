package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.TileEntitiesHelper;
import com.irtimaled.bbor.client.models.BoundingBoxBeacon;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.tileentity.TileEntityBeacon;

public class BeaconProvider implements IBoundingBoxProvider<BoundingBoxBeacon> {
    @Override
    public boolean canProvide(int dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.Beacon);
    }

    @Override
    public Iterable<BoundingBoxBeacon> get(int dimensionId) {
        return TileEntitiesHelper.map(TileEntityBeacon.class, beacon -> {
            int levels = beacon.getLevels();
            Coords coords = new Coords(beacon.getPos());
            return BoundingBoxBeacon.from(coords, levels);
        });
    }
}
