package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import com.irtimaled.bbor.client.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public class MobSpawnerProvider implements IBoundingBoxProvider<BoundingBoxMobSpawner> {

    private static final Long2ObjectMap<BoundingBoxMobSpawner> boxes = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>(), ConduitProvider.class);
    private static ObjectList<BoundingBoxMobSpawner> boxesCopy = new ObjectArrayList<>(boxes.values());

    static {
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityAddEvent.class, event -> {
            updateOrCreateMobSpawner(event.blockEntity());
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityUpdateEvent.class, event -> {
            updateOrCreateMobSpawner(event.blockEntity());
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityRemoveEvent.class, event -> {
            boxes.remove(ChunkPos.toLong(ChunkSectionPos.getSectionCoord(event.x()), ChunkSectionPos.getSectionCoord(event.z())));
            updateCopy();
        });
    }

    public static void updateOrCreateMobSpawner(BlockEntity blockEntity) {
        if (blockEntity instanceof MobSpawnerBlockEntity mobSpawner) {
            final long key = ChunkPos.toLong(blockEntity.getPos());
            Coords coords = new Coords(mobSpawner.getPos());
            boxes.put(key, BoundingBoxMobSpawner.from(coords));
            updateCopy();
        }
    }

    private static void updateCopy() {
        boxesCopy = new ObjectArrayList<>(boxes.values());
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.MobSpawner);
    }

    @Override
    public Iterable<BoundingBoxMobSpawner> get(DimensionId dimensionId) {
        return boxesCopy;
    }
}
