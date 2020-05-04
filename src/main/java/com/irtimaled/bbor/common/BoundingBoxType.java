package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.Colors;
import com.irtimaled.bbor.config.ConfigManager;
import com.irtimaled.bbor.config.Setting;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoundingBoxType {
    private static final Map<Integer, BoundingBoxType> structureTypeMap = new HashMap<>();

    public static final BoundingBoxType JungleTemple = register(Colors.DARK_GREEN, "Jungle_Pyramid", ConfigManager.drawJungleTemples);
    public static final BoundingBoxType DesertTemple = register(Color.ORANGE, "Desert_Pyramid", ConfigManager.drawDesertTemples);
    public static final BoundingBoxType WitchHut = register(Color.BLUE, "Swamp_Hut", ConfigManager.drawWitchHuts);
    public static final BoundingBoxType OceanMonument = register(Color.CYAN, "Monument", ConfigManager.drawOceanMonuments);
    public static final BoundingBoxType Shipwreck = register(Color.CYAN, "Shipwreck", ConfigManager.drawShipwrecks);
    public static final BoundingBoxType OceanRuin = register(Color.CYAN, "Ocean_Ruin", ConfigManager.drawOceanRuins);
    public static final BoundingBoxType BuriedTreasure = register(Color.CYAN, "Buried_Treasure", ConfigManager.drawBuriedTreasure);
    public static final BoundingBoxType Stronghold = register(Color.YELLOW, "Stronghold", ConfigManager.drawStrongholds);
    public static final BoundingBoxType MineShaft = register(Color.LIGHT_GRAY, "Mineshaft", ConfigManager.drawMineShafts);
    public static final BoundingBoxType NetherFortress = register(Color.RED, "Fortress", ConfigManager.drawNetherFortresses);
    public static final BoundingBoxType EndCity = register(Color.MAGENTA, "EndCity", ConfigManager.drawEndCities);
    public static final BoundingBoxType Mansion = register(Colors.BROWN, "Mansion", ConfigManager.drawMansions);
    public static final BoundingBoxType Igloo = register(Color.WHITE, "Igloo", ConfigManager.drawIgloos);
    public static final BoundingBoxType PillagerOutpost = register(Color.DARK_GRAY, "Pillager_Outpost", ConfigManager.drawPillagerOutposts);
    public static final BoundingBoxType WorldSpawn = register(Color.RED, "World_Spawn", ConfigManager.drawWorldSpawn);
    public static final BoundingBoxType SpawnChunks = register(Color.RED, "Spawn_Chunks", ConfigManager.drawWorldSpawn);
    public static final BoundingBoxType LazySpawnChunks = register(Color.RED, "Lazy_Chunks", ConfigManager.drawLazySpawnChunks);
    public static final BoundingBoxType MobSpawner = register(Color.GREEN, "Mob_Spawner", ConfigManager.drawMobSpawners);
    public static final BoundingBoxType SlimeChunks = register(Colors.DARK_GREEN, "Slime_Chunks", ConfigManager.drawSlimeChunks);
    public static final BoundingBoxType Village = register(Colors.PURPLE, "Village", ConfigManager.drawVillages);
    public static final BoundingBoxType VillageSpheres = register(null, "Village Sphere", ConfigManager.drawVillageSpheres);
    public static final BoundingBoxType AFKSphere = register(Color.RED, "AFK Sphere", ConfigManager.drawAFKSpheres);
    public static final BoundingBoxType BiomeBorder = register(Color.GREEN, "Biome Border", ConfigManager.drawBiomeBorders);
    public static final BoundingBoxType Beacon = register(Color.WHITE, "Beacon", ConfigManager.drawBeacons);
    public static final BoundingBoxType Custom = register(Color.WHITE, "Custom", ConfigManager.drawCustomBoxes);

    private static BoundingBoxType register(Color color, String name, Setting<Boolean> shouldRenderSetting) {
        return structureTypeMap.computeIfAbsent(name.hashCode(), k -> new BoundingBoxType(color, name, shouldRenderSetting));
    }

    public static BoundingBoxType getByNameHash(Integer nameHash) {
        return structureTypeMap.get(nameHash);
    }

    private final Color color;
    private final String name;
    public final Setting<Boolean> shouldRenderSetting;

    private BoundingBoxType(Color color, String name, Setting<Boolean> shouldRenderSetting) {
        this.color = color;
        this.name = name;
        this.shouldRenderSetting = shouldRenderSetting;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Boolean shouldRender() {
        return shouldRenderSetting.get();
    }
}
