package com.irtimaled.bbor.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.HashMap;
import java.util.Map;

public class BoundingBoxType {
    private static final Map<Integer, BoundingBoxType> structureTypeMap = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    public static final BoundingBoxType WorldSpawn = register("World_Spawn");
    public static final BoundingBoxType SpawnChunks = register("Spawn_Chunks");
    public static final BoundingBoxType LazySpawnChunks = register("Lazy_Chunks");
    public static final BoundingBoxType MobSpawner = register("Mob_Spawner");
    public static final BoundingBoxType SlimeChunks = register("Slime_Chunks");
    public static final BoundingBoxType AFKSphere = register("AFK Sphere");
    public static final BoundingBoxType BiomeBorder = register("Biome Border");
    public static final BoundingBoxType Custom = register("Custom");
    public static final BoundingBoxType Beacon = register("Beacon");
    public static final BoundingBoxType Conduit = register("Conduit");
    public static final BoundingBoxType SpawnableBlocks = register("Spawnable Blocks");
    public static final BoundingBoxType FlowerForest = register("Flower Forest");
    public static final BoundingBoxType BedrockCeiling = register("Bedrock Ceiling");

    public static final Map<String, BoundingBoxType> structures = new HashMap<>();

    public static BoundingBoxType register(String name) {
        return structureTypeMap.computeIfAbsent(name.hashCode(), k -> new BoundingBoxType(name));
    }

    public static void registerTypes() {
//        structureTypeMap.values().forEach(StructureProcessor::registerSupportedStructure);
    }

    public static BoundingBoxType getByNameHash(Integer nameHash) {
        return structureTypeMap.get(nameHash);
    }

    private final String name;

    private BoundingBoxType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BoundingBoxType other = (BoundingBoxType) obj;
        return this.name.equals(other.name);
    }
}
