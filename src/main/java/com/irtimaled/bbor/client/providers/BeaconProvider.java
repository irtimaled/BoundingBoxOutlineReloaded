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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BeaconProvider implements IBoundingBoxProvider<BoundingBoxBeacon> {

    private static final Long2ObjectMap<BoundingBoxBeacon> boxes = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>(), BeaconProvider.class);
    private static ObjectList<BoundingBoxBeacon> boxesCopy = new ObjectArrayList<>(boxes.values());

    static {
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityAddEvent.class, event -> {
            updateOrCreateBeacon(event.blockEntity());
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityUpdateEvent.class, event -> {
            updateOrCreateBeacon(event.blockEntity());
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityRemoveEvent.class, event -> {
            boxes.remove(new ChunkPos(new BlockPos(event.x(), event.y(), event.z())).toLong());
            updateCopy();
        });
    }

    public static void updateOrCreateBeacon(BlockEntity blockEntity) {
        if (blockEntity instanceof BeaconBlockEntity beacon) {
            int levels = ((IBeaconBlockEntity) beacon).getLevel1();
            final long key = ChunkPos.toLong(blockEntity.getPos());
            if (boxes.containsKey(key) && boxes.get(key).getLevel() == levels) return;
            Coords coords = new Coords(beacon.getPos());
            boxes.put(key, BoundingBoxBeacon.from(coords, levels));
            updateCopy();
        }
    }

    private static void updateCopy() {
        synchronized (BeaconProvider.class) {
            boxesCopy = new ObjectArrayList<>(boxes.values());
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
