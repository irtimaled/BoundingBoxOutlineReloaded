package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.ReflectionHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DimensionId {
    private static final Function<DimensionType, Optional<RegistryKey<DimensionType>>> getRegistryKey =
            ReflectionHelper.getPrivateFieldGetter(DimensionType.class, Optional.class);
    private static final Map<Identifier, DimensionType> typeMap = new HashMap<>();
    private static final Map<Identifier, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(DimensionType dimensionType) {
        Identifier value = getRegistryKey.apply(dimensionType).get().getValue();
        typeMap.put(value, dimensionType);
        return from(value);
    }

    public static DimensionId from(Identifier value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(DimensionType.getDefaultDimensionType());

    private final Identifier value;

    public DimensionId(Identifier value) {
        this.value = value;
    }

    public Identifier getValue() {
        return value;
    }

    public DimensionType getDimensionType() {
        return typeMap.get(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
