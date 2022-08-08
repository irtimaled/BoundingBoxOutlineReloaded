package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import com.irtimaled.bbor.client.models.BoundingBoxBeacon;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.mixin.access.IBeaconBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BeaconProvider implements IBoundingBoxProvider<BoundingBoxBeacon> {

    private final Long2ObjectMap<BoundingBoxBeacon> boxes = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>(), this);
    private ObjectList<BoundingBoxBeacon> boxesCopy = new ObjectArrayList<>(this.boxes.values());

    {
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityAddEvent.class, event -> {
            if (event.blockEntity() instanceof BeaconBlockEntity beacon) {
                int levels = ((IBeaconBlockEntity) beacon).getLevel1();
                Coords coords = new Coords(beacon.getPos());
                boxes.put(new ChunkPos(beacon.getPos()).toLong(), BoundingBoxBeacon.from(coords, levels));
                updateCopy();
            }
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityUpdateEvent.class, event -> {
            if (event.blockEntity() instanceof BeaconBlockEntity beacon) {
                int levels = ((IBeaconBlockEntity) beacon).getLevel1();
                Coords coords = new Coords(beacon.getPos());
                boxes.put(new ChunkPos(beacon.getPos()).toLong(), BoundingBoxBeacon.from(coords, levels));
                updateCopy();
            }
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityRemoveEvent.class, event -> {
            boxes.remove(new ChunkPos(new BlockPos(event.x(), event.y(), event.z())).toLong());
            updateCopy();
        });
    }

    private void updateCopy() {
        synchronized (this) {
            this.boxesCopy = new ObjectArrayList<>(this.boxes.values());
        }
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.Beacon);
    }

    @Override
    public Iterable<BoundingBoxBeacon> get(DimensionId dimensionId) {
        return this.boxesCopy;
    }
}
