package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.Colors;
import com.irtimaled.bbor.config.ConfigManager;
import com.irtimaled.bbor.config.Setting;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoundingBoxType {
    private final static Map<Integer, BoundingBoxType> structureTypeMap = new HashMap<>();

    public final static BoundingBoxType JungleTemple = register(Colors.DARK_GREEN, "Jungle_Pyramid", ConfigManager.drawJungleTemples);
    public final static BoundingBoxType DesertTemple = register(Color.ORANGE, "Desert_Pyramid", ConfigManager.drawDesertTemples);
    public final static BoundingBoxType WitchHut = register(Color.BLUE, "Swamp_Hut", ConfigManager.drawWitchHuts);
    public final static BoundingBoxType OceanMonument = register(Color.CYAN, "Monument", ConfigManager.drawOceanMonuments);
    public final static BoundingBoxType Shipwreck = register(Color.CYAN, "Shipwreck", ConfigManager.drawShipwrecks);
    public final static BoundingBoxType OceanRuin = register(Color.CYAN, "Ocean_Ruin", ConfigManager.drawOceanRuins);
    public final static BoundingBoxType BuriedTreasure = register(Color.CYAN, "Buried_Treasure", ConfigManager.drawBuriedTreasure);
    public final static BoundingBoxType Stronghold = register(Color.YELLOW, "Stronghold", ConfigManager.drawStrongholds);
    public final static BoundingBoxType MineShaft = register(Color.LIGHT_GRAY, "Mineshaft", ConfigManager.drawMineShafts);
    public final static BoundingBoxType NetherFortress = register(Color.RED, "Fortress", ConfigManager.drawNetherFortresses);
    public final static BoundingBoxType EndCity = register(Color.MAGENTA, "EndCity", ConfigManager.drawEndCities);
    public final static BoundingBoxType Mansion = register(Colors.BROWN, "Mansion", ConfigManager.drawMansions);
    public final static BoundingBoxType Igloo = register(Color.WHITE, "Igloo", ConfigManager.drawIgloos);
    public final static BoundingBoxType PillagerOutpost = register(Color.GRAY, "Pillager_Outpost", ConfigManager.drawPillagerOutposts);
    public final static BoundingBoxType WorldSpawn = register(Color.RED, "World_Spawn", ConfigManager.drawWorldSpawn);
    public final static BoundingBoxType SpawnChunks = register(Color.RED, "Spawn_Chunks", ConfigManager.drawWorldSpawn);
    public final static BoundingBoxType LazySpawnChunks = register(Color.RED, "Lazy_Chunks", ConfigManager.drawLazySpawnChunks);
    public final static BoundingBoxType MobSpawner = register(Color.GREEN, "Mob_Spawner", ConfigManager.drawMobSpawners);
    public final static BoundingBoxType SlimeChunks = register(Colors.DARK_GREEN, "Slime_Chunks", ConfigManager.drawSlimeChunks);

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
