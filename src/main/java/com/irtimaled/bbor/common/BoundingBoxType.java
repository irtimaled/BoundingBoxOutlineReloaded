package com.irtimaled.bbor.common;

import java.util.HashMap;
import java.util.Map;

public class BoundingBoxType {
    private static final Map<Integer, BoundingBoxType> structureTypeMap = new HashMap<>();

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

    public static final BoundingBoxType JungleTemple = register("Jungle_Pyramid");
    public static final BoundingBoxType DesertTemple = register("Desert_Pyramid");
    public static final BoundingBoxType WitchHut = register("Swamp_Hut");
    public static final BoundingBoxType OceanMonument = register("Monument");
    public static final BoundingBoxType Shipwreck = register("Shipwreck");
    public static final BoundingBoxType OceanRuin = register("Ocean_Ruin");
    public static final BoundingBoxType BuriedTreasure = register("Buried_Treasure");
    public static final BoundingBoxType Stronghold = register("Stronghold");
    public static final BoundingBoxType MineShaft = register("Mineshaft");
    public static final BoundingBoxType NetherFortress = register("Fortress");
    public static final BoundingBoxType EndCity = register("EndCity");
    public static final BoundingBoxType Mansion = register("Mansion");
    public static final BoundingBoxType Igloo = register("Igloo");
    public static final BoundingBoxType PillagerOutpost = register("Pillager_Outpost");
    public static final BoundingBoxType Village = register("Village");
    public static final BoundingBoxType NetherFossil = register("Nether_Fossil");
    public static final BoundingBoxType BastionRemnant = register("Bastion_Remnant");
    public static final BoundingBoxType RuinedPortal = register("Ruined_Portal");

    private static BoundingBoxType register(String name) {
        return structureTypeMap.computeIfAbsent(name.hashCode(), k -> new BoundingBoxType(name));
    }

    public static void registerTypes() {
        structureTypeMap.values().forEach(StructureProcessor::registerSupportedStructure);
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
