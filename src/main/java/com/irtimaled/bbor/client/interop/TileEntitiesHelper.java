package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.mixin.client.access.IClientChunkManager;
import com.irtimaled.bbor.mixin.client.access.IClientChunkManagerClientChunkMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;

public class TileEntitiesHelper {

    private static Collection<BlockEntity> tileEntities = null;

    private static Collection<BlockEntity> getTileEntities() {
        if (tileEntities == null) {
            @SuppressWarnings("ConstantConditions") final AtomicReferenceArray<WorldChunk> chunks = ((IClientChunkManagerClientChunkMap) (Object) ((IClientChunkManager) MinecraftClient.getInstance().world.getChunkManager()).getChunks()).getChunks();
            Collection<BlockEntity> tileEntities = new ArrayList<>();
            for (int i = 0; i < chunks.length(); i ++) {
                final WorldChunk worldChunk = chunks.get(i);
                if(worldChunk == null) continue;
                tileEntities.addAll(worldChunk.getBlockEntities().values());
            }
            TileEntitiesHelper.tileEntities = tileEntities;
            return tileEntities;
        }
        return tileEntities;
    }

    public static void clearCache() {
        tileEntities = null;
    }

    public static <T extends BlockEntity, S extends AbstractBoundingBox> Iterable<S> map(Class<T> clazz, Function<T, S> map) {
        final Collection<BlockEntity> tileEntities = getTileEntities();

        Set<S> results = new HashSet<>();
        for (BlockEntity tileEntity : tileEntities) {
            T typed = TypeHelper.as(tileEntity, clazz);
            if (typed == null) {
                continue;
            }
            S result = map.apply(typed);
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }
}
