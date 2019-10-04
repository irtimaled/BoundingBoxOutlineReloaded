package com.irtimaled.bbor.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {
    private static Set<Setting<?>> settings = new HashSet<>();
    public static File configDir;

    public static Setting<Boolean> fill;
    public static Setting<Boolean> drawVillages;
    public static Setting<Boolean> drawDesertTemples;
    public static Setting<Boolean> drawJungleTemples;
    public static Setting<Boolean> drawWitchHuts;
    public static Setting<Boolean> drawStrongholds;
    public static Setting<Boolean> drawMineShafts;
    public static Setting<Boolean> drawNetherFortresses;
    public static Setting<Boolean> drawOceanMonuments;
    public static Setting<Boolean> alwaysVisible;
    public static Setting<Boolean> drawIronGolemSpawnArea;
    public static Setting<Boolean> drawVillageDoors;
    public static Setting<Boolean> drawSlimeChunks;
    public static Setting<Integer> slimeChunkMaxY;
    public static Setting<Boolean> keepCacheBetweenSessions;
    public static Setting<Boolean> drawWorldSpawn;
    public static Setting<Integer> worldSpawnMaxY;
    public static Setting<Boolean> drawLazySpawnChunks;
    public static Setting<Boolean> drawEndCities;
    public static Setting<Boolean> drawMansions;
    public static Setting<Boolean> drawShipwrecks;
    public static Setting<Boolean> drawOceanRuins;
    public static Setting<Boolean> drawBuriedTreasure;
    public static Setting<Boolean> drawIgloos;
    public static Setting<Boolean> drawMobSpawners;
    public static Setting<Boolean> renderMobSpawnerSpawnArea;
    public static Setting<Boolean> renderMobSpawnerActivationLines;
    public static Setting<Boolean> drawPillagerOutposts;
    public static Setting<Boolean> outerBoxesOnly;
    public static Setting<Integer> villageSphereDotSize;
    public static Setting<Integer> villageSphereDensity;
    public static Setting<Boolean> drawVillageSpheres;
    public static Setting<Boolean> drawAFKSpheres;
    public static Setting<Boolean> renderAFKSpawnableBlocks;
    public static Setting<Integer> afkSpawnableBlocksRenderDistance;
    public static Setting<Boolean> drawBeacons;
    public static Setting<Boolean> drawCustomBoxes;
    public static Setting<Boolean> drawBiomeBorders;
    public static Setting<Boolean> renderOnlyCurrentBiome;
    public static Setting<Integer> biomeBordersRenderDistance;

    public static void loadConfig(File mcConfigDir) {
        configDir = new File(mcConfigDir, "config");
        configDir.mkdirs();
        Configuration config = loadConfig();

        fill = setup(config, "general", "fill", true, "If set to true the bounding boxes are filled.");
        outerBoxesOnly = setup(config, "general", "outerBoxesOnly", false, "If set to true only the outer bounding boxes are rendered.");
        alwaysVisible = setup(config, "general", "alwaysVisible", false, "If set to true boxes will be visible even through other blocks.");
        keepCacheBetweenSessions = setup(config, "general", "keepCacheBetweenSessions", false, "If set to true bounding box caches will be kept between sessions.");
        drawBeacons = setup(config, "general", "drawBeacons", true, "If set to true beacon bounding boxes will be drawn.");
        drawCustomBoxes = setup(config, "general", "drawCustomBoxes", true, "If set to true custom bounding boxes will be drawn.");
        drawBiomeBorders = setup(config, "biomeBorders", "drawBiomeBorders", true, "If set to true biome borders will be drawn.");
        renderOnlyCurrentBiome = setup(config, "biomeBorders", "renderOnlyCurrentBiome", true, "If set to true only the biome border for the current biome will be drawn.");
        biomeBordersRenderDistance = setup(config, "biomeBorders", "biomeBordersRenderDistance", 3, "The distance from the player where biome borders will be drawn.");

        drawVillageSpheres = setup(config, "villages", "drawVillageSpheres", true, "If set to true village bounding spheres are drawn.");
        drawIronGolemSpawnArea = setup(config, "villages", "drawIronGolemSpawnArea", true, "If set to true the iron golem spawn area of the village will be drawn. (default:true)");
        drawVillageDoors = setup(config, "villages", "drawVillageDoors", false, "If set to true lines between the village centre and doors will be drawn. (default:false)");
        villageSphereDotSize = setup(config, "villages", "villageSphereDotSize", 2, "The size of the dots used when rendering village as sphere.");
        villageSphereDensity = setup(config, "villages", "villageSphereDensity", 3, "The density of the dots used when rendering village as sphere.");

        drawVillages = setup(config, "structures", "drawVillages", false, "If set to true village bounding boxes will be drawn.");
        drawDesertTemples = setup(config, "structures", "drawDesertTemples", true, "If set to true desert temple bounding boxes are drawn.");
        drawJungleTemples = setup(config, "structures", "drawJungleTemples", true, "If set to true jungle temple bounding boxes are drawn.");
        drawWitchHuts = setup(config, "structures", "drawWitchHuts", true, "If set to true witch hut bounding boxes are drawn.");
        drawStrongholds = setup(config, "structures", "drawStrongholds", false, "If set to true stronghold bounding boxes are drawn.");
        drawMineShafts = setup(config, "structures", "drawMineShafts", false, "If set to true mineshaft bounding boxes are drawn.");
        drawNetherFortresses = setup(config, "structures", "drawNetherFortresses", true, "If set to true nether fortress bounding boxes are drawn.");
        drawOceanMonuments = setup(config, "structures", "drawOceanMonuments", true, "If set to true ocean monument bounding boxes are drawn.");
        drawEndCities = setup(config, "structures", "drawEndCities", true, "If set to true end city bounding boxes will be drawn.");
        drawMansions = setup(config, "structures", "drawMansions", true, "If set to true woodland mansions will be drawn.");
        drawIgloos = setup(config, "structures", "drawIgloos", true, "If set to true igloos will be drawn.");
        drawShipwrecks = setup(config, "structures", "drawShipwrecks", true, "If set to true shipwrecks will be drawn.");
        drawOceanRuins = setup(config, "structures", "drawOceanRuins", true, "If set to true ocean ruins will be drawn.");
        drawBuriedTreasure = setup(config, "structures", "drawBuriedTreasures", true, "If set to true buried treasure will be drawn.");
        drawPillagerOutposts = setup(config, "structures", "drawPillagerOutposts", true, "If set to true pillager outposts will be drawn.");

        drawSlimeChunks = setup(config, "slimeChunks", "drawSlimeChunks", true, "If set to true slime chunks bounding boxes are drawn.");
        slimeChunkMaxY = setup(config, "slimeChunks", "slimeChunkMaxY", -1, "The maximum top of the slime chunk bounding box. If set to -1 it will use the value when activated, if set to 0 it will always track the player's feet.");

        drawWorldSpawn = setup(config, "worldSpawn", "drawWorldSpawn", true, "If set to true world spawn and spawn chunks bounding boxes are drawn.");
        worldSpawnMaxY = setup(config, "worldSpawn", "worldSpawnMaxY", -1, "The maximum top of the world spawn bounding boxes. If set to -1 it will use the value when activated, if set to 0 it will always track the players feet.");
        drawLazySpawnChunks = setup(config, "worldSpawn", "drawLazySpawnChunks", false, "If set to true the lazy spawn chunks bounding boxes will be drawn.");

        drawMobSpawners = setup(config, "mobSpawners", "drawMobSpawners", true, "If set to true mob spawners will be drawn.");
        renderMobSpawnerSpawnArea = setup(config, "mobSpawners", "renderMobSpawnerSpawnArea", true, "If set to true a box to show the maximum possible spawn area (10x10x4) for a spawner will be drawn");
        renderMobSpawnerActivationLines = setup(config, "mobSpawners", "renderMobSpawnerActivationLines", true, "If set to true a red/orange/green line will be drawn to show if the spawner is active");

        drawAFKSpheres = setup(config, "afkSpot", "drawAFKSpheres", true, "If set to true afk spot spheres will be drawn.");
        renderAFKSpawnableBlocks = setup(config, "afkSpot", "renderAFKSpawnableBlocks", true, "If set to true boxes to show spawnable blocks within the AFK sphere will be drawn.");
        afkSpawnableBlocksRenderDistance = setup(config, "afkSpot", "afkSpawnableBlocksRenderDistance", 3, "The distance from the player where spawnable blocks within the AFK sphere will be drawn.");

        config.save();
    }

    private static Configuration loadConfig() {
        Configuration config = new Configuration(new File(configDir, "BBOutlineReloaded.cfg"));
        config.load();
        return config;
    }

    public static void saveConfig() {
        Configuration config = new Configuration(new File(configDir, "BBOutlineReloaded.cfg"));
        for (Setting<?> setting : settings) {
            config.put(setting);
        }
        config.save();
    }

    private static <T> Setting<T> setup(Configuration config, String category, String settingName, T defaultValue, String comment) {
        Setting<T> setting = config.get(category, settingName, defaultValue);
        setting.category = category;
        setting.name = settingName;
        setting.comment = comment + " (default: " + defaultValue.toString() + ")";
        settings.add(setting);
        return setting;
    }

    public static void Toggle(Setting<Boolean> setting) {
        setting.set(!setting.get());
    }
}
