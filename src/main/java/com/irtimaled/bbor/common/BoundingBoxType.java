package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.Colors;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoundingBoxType {
    private static final Map<Integer, BoundingBoxType> structureTypeMap = new HashMap<>();

    public static final BoundingBoxType WorldSpawn = register(Color.RED, "World_Spawn");
    public static final BoundingBoxType SpawnChunks = register(Color.RED, "Spawn_Chunks");
    public static final BoundingBoxType LazySpawnChunks = register(Color.RED, "Lazy_Chunks");
    public static final BoundingBoxType MobSpawner = register(Color.GREEN, "Mob_Spawner");
    public static final BoundingBoxType SlimeChunks = register(Colors.DARK_GREEN, "Slime_Chunks");
    public static final BoundingBoxType AFKSphere = register(Color.RED, "AFK Sphere");
    public static final BoundingBoxType BiomeBorder = register(Color.GREEN, "Biome Border");
    public static final BoundingBoxType Custom = register(Color.WHITE, "Custom");
    public static final BoundingBoxType Beacon = register(Color.WHITE, "Beacon");
    public static final BoundingBoxType Conduit = register(Color.CYAN, "Conduit");
    public static final BoundingBoxType SpawnableBlocks = register(Color.RED, "Spawnable Blocks");

    public static final BoundingBoxType JungleTemple = register(Colors.DARK_GREEN, "Jungle_Pyramid");
    public static final BoundingBoxType DesertTemple = register(Color.ORANGE, "Desert_Pyramid");
    public static final BoundingBoxType WitchHut = register(Color.BLUE, "Swamp_Hut");
    public static final BoundingBoxType OceanMonument = register(Color.CYAN, "Monument");
    public static final BoundingBoxType Shipwreck = register(Color.CYAN, "Shipwreck");
    public static final BoundingBoxType OceanRuin = register(Color.CYAN, "Ocean_Ruin");
    public static final BoundingBoxType BuriedTreasure = register(Color.CYAN, "Buried_Treasure");
    public static final BoundingBoxType Stronghold = register(Color.YELLOW, "Stronghold");
    public static final BoundingBoxType MineShaft = register(Color.LIGHT_GRAY, "Mineshaft");
    public static final BoundingBoxType NetherFortress = register(Color.RED, "Fortress");
    public static final BoundingBoxType EndCity = register(Color.MAGENTA, "EndCity");
    public static final BoundingBoxType Mansion = register(Colors.BROWN, "Mansion");
    public static final BoundingBoxType Igloo = register(Color.WHITE, "Igloo");
    public static final BoundingBoxType PillagerOutpost = register(Color.DARK_GRAY, "Pillager_Outpost");
    public static final BoundingBoxType Village = register(Colors.PURPLE, "Village");
    public static final BoundingBoxType VillageSpheres = register(null, "Village Sphere");
    public static final BoundingBoxType NetherFossil = register(Color.WHITE, "Nether_Fossil");
    public static final BoundingBoxType BastionRemnant = register(Color.LIGHT_GRAY, "Bastion_Remnant");
    public static final BoundingBoxType RuinedPortal = register(Colors.COOL_PURPLE, "Ruined_Portal");

    private static BoundingBoxType register(Color color, String name) {
        BoundingBoxType type = structureTypeMap.computeIfAbsent(name.hashCode(), k -> new BoundingBoxType(color, name));
        StructureProcessor.registerSupportedStructure(type);
        return type;
    }

    public static BoundingBoxType getByNameHash(Integer nameHash) {
        return structureTypeMap.get(nameHash);
    }

    private final Color color;
    private final String name;

    private BoundingBoxType(Color color, String name) {
        this.color = color;
        this.name = name;
    }

    public Color getColor() {
        return color;
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
