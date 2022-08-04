package com.irtimaled.bbor.common;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.world.level.levelgen.structure.structures.EndCityStructure;
import net.minecraft.world.level.levelgen.structure.structures.IglooStructure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutStructure;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;

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
    public static final BoundingBoxType FlowerForest = register("Flower Forest");
    public static final BoundingBoxType BedrockCeiling = register("Bedrock Ceiling");

    public static final BoundingBoxType JungleTemple = register("jungle_temple");
    public static final BoundingBoxType DesertTemple = register("desert_pyramid");
    public static final BoundingBoxType WitchHut = register("swamp_hut");
    public static final BoundingBoxType OceanMonument = register("monument");
    public static final BoundingBoxType Shipwreck = register("shipwreck");
    public static final BoundingBoxType OceanRuin = register("ocean_ruin");
    public static final BoundingBoxType BuriedTreasure = register("buried_treasure");
    public static final BoundingBoxType Stronghold = register("stronghold");
    public static final BoundingBoxType MineShaft = register("mineshaft");
    public static final BoundingBoxType NetherFortress = register("fortress");
    public static final BoundingBoxType EndCity = register("endcity");
    public static final BoundingBoxType Mansion = register("mansion");
    public static final BoundingBoxType Igloo = register("igloo");
    public static final BoundingBoxType PillagerOutpost = register("pillager_outpost");
    public static final BoundingBoxType Village = register("village");
    public static final BoundingBoxType NetherFossil = register("nether_fossil");
    public static final BoundingBoxType BastionRemnant = register("bastion_remnant");
    public static final BoundingBoxType RuinedPortal = register("ruined_portal");

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

    // TODO change it
    public static BoundingBoxType getByStructure(Structure structure) {
        if (structure instanceof BuriedTreasureStructure) {
            return BuriedTreasure;
        } else if (structure instanceof JungleTempleStructure) {
            return JungleTemple;
        } else if (structure instanceof DesertPyramidStructure) {
            return DesertTemple;
        } else if (structure instanceof EndCityStructure) {
            return EndCity;
        } else if (structure instanceof NetherFortressStructure) {
            return NetherFortress;
        } else if (structure instanceof IglooStructure) {
            return Igloo;
        } else if (structure instanceof MineshaftStructure) {
            return MineShaft;
        } else if (structure instanceof NetherFossilStructure) {
            return NetherFossil;
        } else if (structure instanceof OceanMonumentStructure) {
            return OceanMonument;
        } else if (structure instanceof OceanRuinStructure) {
            return OceanRuin;
        } else if (structure instanceof RuinedPortalStructure) {
            return RuinedPortal;
        } else if (structure instanceof ShipwreckStructure) {
            return Shipwreck;
        } else if (structure instanceof StrongholdStructure) {
            return  Stronghold;
        } else if (structure instanceof SwampHutStructure) {
            return WitchHut;
        } else if (structure instanceof WoodlandMansionStructure) {
            return Mansion;
        } else if (structure instanceof JigsawStructure) {
            // TODO
        }
        return WorldSpawn; // not it
    }
}
