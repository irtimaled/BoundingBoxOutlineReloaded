package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.ReflectionHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DimensionId {
    private static final Function<DimensionType, Optional<RegistryKey<DimensionType>>> getRegistryKey =
            ReflectionHelper.getPrivateFieldGetter(DimensionType.class, Optional.class);
    private static final Map<ResourceLocation, RegistryKey<World>> typeMap = new HashMap<>();
    private static final Map<ResourceLocation, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(RegistryKey<World> registryKey) {
        ResourceLocation value = registryKey.getLocation();
        typeMap.put(value, registryKey);
        return from(value);
    }

    public static DimensionId from(ResourceLocation value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(World.OVERWORLD);
    public static DimensionId NETHER = DimensionId.from(World.THE_NETHER);

    private final ResourceLocation value;

    public DimensionId(ResourceLocation value) {
        this.value = value;
    }

    public ResourceLocation getValue() {
        return value;
    }

    public RegistryKey<World> getDimensionType() {
        return typeMap.get(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
