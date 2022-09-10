package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record DimensionId(Object value) {

    private static final Map<Object, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(@NotNull Object dimensionType) {
        return fromValue(NMSHelper.resourceKeyGetValue(dimensionType));
    }

    public static DimensionId fromValue(Object value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(NMSHelper.worldGetOverloadWorldKey());

    @Override
    public String toString() {
        return value.toString();
    }
}
