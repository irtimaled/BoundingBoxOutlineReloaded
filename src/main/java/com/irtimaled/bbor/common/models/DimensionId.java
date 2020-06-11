package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.ReflectionHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionId {
    private static final Function<DimensionType, Optional<RegistryKey<DimensionType>>> getRegistryKey =
            ReflectionHelper.getPrivateFieldGetter(DimensionType.class, Optional.class, RegistryKey.class, DimensionType.class);
    private static final Map<Identifier, DimensionType> typeMap = new HashMap<>();
    private static final Map<Identifier, DimensionId> dimensionIdMap = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();

    public static DimensionId from(DimensionType dimensionType) {
        Optional<RegistryKey<DimensionType>> dimesion = getRegistryKey.apply(dimensionType);
        Identifier value;
        if (dimesion.isPresent())
        {
            value = dimesion.get().getValue();
        } 
        else
        {
            // I don't know why all return false
            // when it call from Player.setPosition
            // when client connect to online server
            // LOGGER.error("isOverworld:{}", dimensionType.isOverworld());
            // LOGGER.error("isNether:{}", dimensionType.isNether());
            // LOGGER.error("isEnd:{}", dimensionType.isEnd());
            // LOGGER.error("fuck!!!!!");
            value = null;
        }

        //Identifier value = getRegistryKey.apply(dimensionType).get().getValue();

        typeMap.put(value, dimensionType);
        return from(value);
    }

    public static DimensionId from(Identifier value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(DimensionType.getOverworldDimensionType());

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
