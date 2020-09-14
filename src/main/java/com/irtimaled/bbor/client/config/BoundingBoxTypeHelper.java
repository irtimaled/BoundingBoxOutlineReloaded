package com.irtimaled.bbor.client.config;

import com.irtimaled.bbor.common.BoundingBoxType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoundingBoxTypeHelper {
    private static final Map<String, BoundingBoxTypeSettings> structureTypeMap = new HashMap<>();

    private static void registerType(BoundingBoxType type, Setting<Boolean> shouldRender, Setting<HexColor> color) {
        structureTypeMap.put(type.getName(), new BoundingBoxTypeSettings(shouldRender, color));
    }

    static {
        registerType(BoundingBoxType.WorldSpawn, ConfigManager.drawWorldSpawn, ConfigManager.colorWorldSpawn);
        registerType(BoundingBoxType.SpawnChunks, ConfigManager.drawWorldSpawn, ConfigManager.colorWorldSpawn);
        registerType(BoundingBoxType.LazySpawnChunks, ConfigManager.drawLazySpawnChunks, ConfigManager.colorLazySpawnChunks);
        registerType(BoundingBoxType.MobSpawner, ConfigManager.drawMobSpawners, ConfigManager.colorMobSpawners);
        registerType(BoundingBoxType.SlimeChunks, ConfigManager.drawSlimeChunks, ConfigManager.colorSlimeChunks);
        registerType(BoundingBoxType.AFKSphere, ConfigManager.drawAFKSpheres, ConfigManager.colorAFKSpheres);
        registerType(BoundingBoxType.BiomeBorder, ConfigManager.drawBiomeBorders, ConfigManager.colorBiomeBorders);
        registerType(BoundingBoxType.Beacon, ConfigManager.drawBeacons, ConfigManager.colorBeacons);
        registerType(BoundingBoxType.Custom, ConfigManager.drawConduits, ConfigManager.colorCustom);
        registerType(BoundingBoxType.Conduit, ConfigManager.drawConduits, ConfigManager.colorConduits);
        registerType(BoundingBoxType.SpawnableBlocks, ConfigManager.drawSpawnableBlocks, ConfigManager.colorSpawnableBlocks);
        registerType(BoundingBoxType.FlowerForest, ConfigManager.drawFlowerForests, null);
        registerType(BoundingBoxType.BedrockCeiling, ConfigManager.drawBedrockCeilingBlocks, ConfigManager.colorBedrockCeilingBlocks);

        registerType(BoundingBoxType.JungleTemple, ConfigManager.drawJungleTemples, ConfigManager.colorJungleTemples);
        registerType(BoundingBoxType.DesertTemple, ConfigManager.drawDesertTemples, ConfigManager.colorDesertTemples);
        registerType(BoundingBoxType.WitchHut, ConfigManager.drawWitchHuts, ConfigManager.colorWitchHuts);
        registerType(BoundingBoxType.OceanMonument, ConfigManager.drawOceanMonuments, ConfigManager.colorOceanMonuments);
        registerType(BoundingBoxType.Shipwreck, ConfigManager.drawShipwrecks, ConfigManager.colorShipwrecks);
        registerType(BoundingBoxType.OceanRuin, ConfigManager.drawOceanRuins, ConfigManager.colorOceanRuins);
        registerType(BoundingBoxType.BuriedTreasure, ConfigManager.drawBuriedTreasure, ConfigManager.colorBuriedTreasure);
        registerType(BoundingBoxType.Stronghold, ConfigManager.drawStrongholds, ConfigManager.colorStrongholds);
        registerType(BoundingBoxType.MineShaft, ConfigManager.drawMineShafts, ConfigManager.colorMineShafts);
        registerType(BoundingBoxType.NetherFortress, ConfigManager.drawNetherFortresses, ConfigManager.colorNetherFortresses);
        registerType(BoundingBoxType.EndCity, ConfigManager.drawEndCities, ConfigManager.colorEndCities);
        registerType(BoundingBoxType.Mansion, ConfigManager.drawMansions, ConfigManager.colorMansions);
        registerType(BoundingBoxType.Igloo, ConfigManager.drawIgloos, ConfigManager.colorIgloos);
        registerType(BoundingBoxType.PillagerOutpost, ConfigManager.drawPillagerOutposts, ConfigManager.colorPillagerOutposts);
        registerType(BoundingBoxType.Village, ConfigManager.drawVillages, ConfigManager.colorVillages);
        registerType(BoundingBoxType.NetherFossil, ConfigManager.drawNetherFossils, ConfigManager.colorNetherFossils);
        registerType(BoundingBoxType.BastionRemnant, ConfigManager.drawBastionRemnants, ConfigManager.colorBastionRemnants);
        registerType(BoundingBoxType.RuinedPortal, ConfigManager.drawRuinedPortals, ConfigManager.colorRuinedPortals);
    }

    public static Setting<Boolean> renderSetting(BoundingBoxType type) {
        return structureTypeMap.get(type.getName()).shouldRender;
    }

    public static boolean shouldRender(BoundingBoxType type) {
        BoundingBoxTypeSettings settings = structureTypeMap.get(type.getName());
        return settings != null ? settings.shouldRender.get() : false;
    }

    public static Color getColor(BoundingBoxType type) {
        BoundingBoxTypeSettings settings = structureTypeMap.get(type.getName());
        return settings != null ? ColorHelper.getColor(settings.color) : Color.WHITE;
    }

    private static class BoundingBoxTypeSettings {
        private final Setting<Boolean> shouldRender;
        private final Setting<HexColor> color;

        public BoundingBoxTypeSettings(Setting<Boolean> shouldRender, Setting<HexColor> color) {
            this.shouldRender = shouldRender;
            this.color = color;
        }
    }
}
