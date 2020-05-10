package com.irtimaled.bbor.client.config;

import com.irtimaled.bbor.common.BoundingBoxType;

import java.util.HashMap;
import java.util.Map;

public class BoundingBoxTypeHelper {
    private static final Map<String, Setting<Boolean>> structureTypeMap = new HashMap<>();

    private static void registerType(BoundingBoxType type, Setting<Boolean> shouldRenderSetting) {
        structureTypeMap.put(type.getName(), shouldRenderSetting);
    }

    static {
        registerType(BoundingBoxType.WorldSpawn, ConfigManager.drawWorldSpawn);
        registerType(BoundingBoxType.SpawnChunks, ConfigManager.drawWorldSpawn);
        registerType(BoundingBoxType.LazySpawnChunks, ConfigManager.drawLazySpawnChunks);
        registerType(BoundingBoxType.MobSpawner, ConfigManager.drawMobSpawners);
        registerType(BoundingBoxType.SlimeChunks, ConfigManager.drawSlimeChunks);
        registerType(BoundingBoxType.AFKSphere, ConfigManager.drawAFKSpheres);
        registerType(BoundingBoxType.BiomeBorder, ConfigManager.drawBiomeBorders);
        registerType(BoundingBoxType.Beacon, ConfigManager.drawBeacons);
        registerType(BoundingBoxType.Conduit, ConfigManager.drawConduits);
        registerType(BoundingBoxType.SpawnableBlocks, ConfigManager.drawSpawnableBlocks);

        registerType(BoundingBoxType.JungleTemple, ConfigManager.drawJungleTemples);
        registerType(BoundingBoxType.DesertTemple, ConfigManager.drawDesertTemples);
        registerType(BoundingBoxType.WitchHut, ConfigManager.drawWitchHuts);
        registerType(BoundingBoxType.OceanMonument, ConfigManager.drawOceanMonuments);
        registerType(BoundingBoxType.Shipwreck, ConfigManager.drawShipwrecks);
        registerType(BoundingBoxType.OceanRuin, ConfigManager.drawOceanRuins);
        registerType(BoundingBoxType.BuriedTreasure, ConfigManager.drawBuriedTreasure);
        registerType(BoundingBoxType.Stronghold, ConfigManager.drawStrongholds);
        registerType(BoundingBoxType.MineShaft, ConfigManager.drawMineShafts);
        registerType(BoundingBoxType.NetherFortress, ConfigManager.drawNetherFortresses);
        registerType(BoundingBoxType.EndCity, ConfigManager.drawEndCities);
        registerType(BoundingBoxType.Mansion, ConfigManager.drawMansions);
        registerType(BoundingBoxType.Igloo, ConfigManager.drawIgloos);
        registerType(BoundingBoxType.PillagerOutpost, ConfigManager.drawPillagerOutposts);
        registerType(BoundingBoxType.Village, ConfigManager.drawVillages);
        registerType(BoundingBoxType.VillageSpheres, ConfigManager.drawVillageSpheres);
        registerType(BoundingBoxType.NetherFossil, ConfigManager.drawNetherFossils);
        registerType(BoundingBoxType.BastionRemnant, ConfigManager.drawBastionRemnants);
        registerType(BoundingBoxType.RuinedPortal, ConfigManager.drawRuinedPortals);
    }

    public static Setting<Boolean> renderSetting(BoundingBoxType type) {
        return structureTypeMap.get(type.getName());
    }

    public static boolean shouldRender(BoundingBoxType type) {
        Setting<Boolean> setting = structureTypeMap.get(type.getName());
        return setting != null ? setting.get() : false;
    }
}
