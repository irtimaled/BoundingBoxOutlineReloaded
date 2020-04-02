package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.interop.TileEntitiesHelper;
import com.irtimaled.bbor.common.models.BoundingBoxBeacon;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.tileentity.TileEntityBeacon;

public class BeaconProvider implements IBoundingBoxProvider<BoundingBoxBeacon> {
    public Iterable<BoundingBoxBeacon> get(int dimensionId) {
        return TileEntitiesHelper.map(TileEntityBeacon.class, beacon -> {
            int levels = beacon.getLevels();
            if (levels == 0) return null;

            Coords coords = new Coords(beacon.getPos());
            return BoundingBoxBeacon.from(coords, levels);
        });
    }
}
