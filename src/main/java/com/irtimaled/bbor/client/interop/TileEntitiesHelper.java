package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class TileEntitiesHelper {
    public static <T extends BlockEntity, S extends AbstractBoundingBox> Iterable<S> map(Class<T> clazz, Function<T, S> map) {
        //Collection<BlockEntity> tileEntities = MinecraftClient.getInstance().world.blockEntities;

        Set<S> results = new HashSet<>();
        /*for (BlockEntity tileEntity : tileEntities) {
            T typed = TypeHelper.as(tileEntity, clazz);
            if (typed == null) {
                continue;
            }
            S result = map.apply(typed);
            if (result != null) {
                results.add(result);
            }
        }*/
        return results;
    }
}
