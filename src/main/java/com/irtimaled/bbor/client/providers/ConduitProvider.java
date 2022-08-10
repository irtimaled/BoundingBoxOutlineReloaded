package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import com.irtimaled.bbor.client.models.BoundingBoxConduit;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.mixin.client.access.IConduitBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public class ConduitProvider implements IBoundingBoxProvider<BoundingBoxConduit> {

    private static final Long2ObjectMap<BoundingBoxConduit> boxes = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>(), ConduitProvider.class);
    private static ObjectList<BoundingBoxConduit> boxesCopy = new ObjectArrayList<>(boxes.values());

    static {
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityAddEvent.class, event -> {
            updateOrCreateConduit(event.blockEntity());
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityUpdateEvent.class, event -> {
            updateOrCreateConduit(event.blockEntity());
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockEntityRemoveEvent.class, event -> {
            boxes.remove(ChunkPos.toLong(ChunkSectionPos.getSectionCoord(event.x()), ChunkSectionPos.getSectionCoord(event.z())));
            updateCopy();
        });
    }

    public static void updateOrCreateConduit(BlockEntity blockEntity) {
        if (blockEntity instanceof ConduitBlockEntity conduit) {
            int levels = ((IConduitBlockEntity) conduit).getActivatingBlocks().size() / 7;
            final long key = ChunkPos.toLong(blockEntity.getPos());
            if (boxes.containsKey(key) && boxes.get(key).getLevel() == levels) return;
            Coords coords = new Coords(conduit.getPos());
            boxes.put(key, new BoundingBoxConduit(coords, levels));
            updateCopy();
        }
    }

    public static void updateCopy() {
        synchronized (ConduitProvider.class) {
            boxesCopy = new ObjectArrayList<>(boxes.values());
        }
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.Conduit);
    }

    @Override
    public Iterable<BoundingBoxConduit> get(DimensionId dimensionId) {
        return boxesCopy;
    }
}
