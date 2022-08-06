package com.irtimaled.bbor.client.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ConfigManager {
    private static final Set<Setting<?>> settings = new HashSet<>();
    private static File configDir;

    public static Setting<Boolean> fill;
    //    public static Setting<Boolean> drawVillages;
//    public static Setting<Boolean> drawDesertTemples;
//    public static Setting<Boolean> drawJungleTemples;
//    public static Setting<Boolean> drawWitchHuts;
//    public static Setting<Boolean> drawStrongholds;
//    public static Setting<Boolean> drawMineShafts;
//    public static Setting<Boolean> drawNetherFortresses;
//    public static Setting<Boolean> drawOceanMonuments;
    public static Setting<Boolean> alwaysVisible;
    public static Setting<Boolean> drawSlimeChunks;
    public static Setting<Integer> slimeChunkMaxY;
    public static Setting<Boolean> keepCacheBetweenSessions;
    public static Setting<Boolean> drawWorldSpawn;
    public static Setting<Integer> worldSpawnMaxY;
    public static Setting<Boolean> drawLazySpawnChunks;
    //    public static Setting<Boolean> drawEndCities;
//    public static Setting<Boolean> drawMansions;
//    public static Setting<Boolean> drawShipwrecks;
//    public static Setting<Boolean> drawOceanRuins;
//    public static Setting<Boolean> drawBuriedTreasure;
//    public static Setting<Boolean> drawIgloos;
    public static Setting<Boolean> drawMobSpawners;
    public static Setting<Boolean> renderMobSpawnerSpawnArea;
    public static Setting<Boolean> renderMobSpawnerActivationLines;
    //    public static Setting<Boolean> drawPillagerOutposts;
    public static Setting<Boolean> outerBoxesOnly;
    public static Setting<Boolean> drawAFKSpheres;
    public static Setting<Boolean> renderAFKSpawnableBlocks;
    public static Setting<Integer> afkSpawnableBlocksRenderDistance;
    public static Setting<Boolean> drawBeacons;
    public static Setting<Boolean> drawBiomeBorders;
    public static Setting<Boolean> renderOnlyCurrentBiome;
    public static Setting<Integer> biomeBordersRenderDistance;
    public static Setting<Integer> biomeBordersMaxY;
    //    public static Setting<Boolean> drawNetherFossils;
//    public static Setting<Boolean> drawBastionRemnants;
//    public static Setting<Boolean> drawRuinedPortals;
    public static Setting<Boolean> drawConduits;
    public static Setting<Boolean> renderConduitMobHarmArea;
    public static Setting<Boolean> drawSpawnableBlocks;
    public static Setting<Integer> spawnableBlocksRenderWidth;
    public static Setting<Integer> spawnableBlocksRenderHeight;
    public static Setting<Boolean> invertBoxColorPlayerInside;
    public static Setting<Boolean> renderSphereAsDots;
    public static Setting<Boolean> drawFlowerForests;
    public static Setting<Integer> flowerForestsRenderDistance;
    public static Setting<Boolean> drawBedrockCeilingBlocks;

    public static Setting<HexColor> colorWorldSpawn;
    public static Setting<HexColor> colorLazySpawnChunks;
    public static Setting<HexColor> colorMobSpawners;
    public static Setting<HexColor> colorMobSpawnersLineFarAway;
    public static Setting<HexColor> colorMobSpawnersLineNearby;
    public static Setting<HexColor> colorMobSpawnersLineActive;
    public static Setting<HexColor> colorSlimeChunks;
    public static Setting<HexColor> colorAFKSpheres;
    public static Setting<HexColor> colorAFKSpheresSafeArea;
    public static Setting<HexColor> colorBiomeBorders;
    public static Setting<HexColor> colorBeacons;
    public static Setting<HexColor> colorCustom;
    public static Setting<HexColor> colorConduits;
    public static Setting<HexColor> colorConduitMobHarmArea;
    public static Setting<HexColor> colorSpawnableBlocks;
//    public static Setting<HexColor> colorJungleTemples;
//    public static Setting<HexColor> colorDesertTemples;
//    public static Setting<HexColor> colorWitchHuts;
//    public static Setting<HexColor> colorOceanMonuments;
//    public static Setting<HexColor> colorShipwrecks;
//    public static Setting<HexColor> colorOceanRuins;
//    public static Setting<HexColor> colorBuriedTreasure;
//    public static Setting<HexColor> colorStrongholds;
//    public static Setting<HexColor> colorMineShafts;
//    public static Setting<HexColor> colorNetherFortresses;
//    public static Setting<HexColor> colorEndCities;
//    public static Setting<HexColor> colorMansions;
//    public static Setting<HexColor> colorIgloos;
//    public static Setting<HexColor> colorPillagerOutposts;
//    public static Setting<HexColor> colorVillages;
//    public static Setting<HexColor> colorNetherFossils;
//    public static Setting<HexColor> colorBastionRemnants;
//    public static Setting<HexColor> colorRuinedPortals;
    public static Setting<HexColor> colorFlowerForestDandelion;
    public static Setting<HexColor> colorFlowerForestPoppy;
    public static Setting<HexColor> colorFlowerForestAllium;
    public static Setting<HexColor> colorFlowerForestAzureBluet;
    public static Setting<HexColor> colorFlowerForestRedTulip;
    public static Setting<HexColor> colorFlowerForestOrangeTulip;
    public static Setting<HexColor> colorFlowerForestWhiteTulip;
    public static Setting<HexColor> colorFlowerForestPinkTulip;
    public static Setting<HexColor> colorFlowerForestOxeyeDaisy;
    public static Setting<HexColor> colorFlowerForestCornflower;
    public static Setting<HexColor> colorFlowerForestLilyOfTheValley;
    public static Setting<HexColor> colorBedrockCeilingBlocks;

    public static Setting<HexColor> buttonOnOverlay;

    public static Setting<Integer> fastRender;

    public static Map<String, Setting<Boolean>> structureRenderSettings = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    public static Map<String, Setting<HexColor>> structureColorSettings = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private static final Map<String, HexColor> defaultStructureColors = new Object2ObjectOpenHashMap<>();

    static {
         defaultStructureColors.put("minecraft:fortress", Objects.requireNonNull(HexColor.from("#ff0000")));
         defaultStructureColors.put("minecraft:village_desert", Objects.requireNonNull(HexColor.from("#800080")));
         defaultStructureColors.put("minecraft:village_snowy", Objects.requireNonNull(HexColor.from("#800080")));
         defaultStructureColors.put("minecraft:village_plains", Objects.requireNonNull(HexColor.from("#800080")));
         defaultStructureColors.put("minecraft:village_savanna", Objects.requireNonNull(HexColor.from("#800080")));
         defaultStructureColors.put("minecraft:village_taiga", Objects.requireNonNull(HexColor.from("#800080")));
         defaultStructureColors.put("minecraft:desert_pyramid", Objects.requireNonNull(HexColor.from("#ffc800")));
         defaultStructureColors.put("minecraft:swamp_hut", Objects.requireNonNull(HexColor.from("#0000ff")));
         defaultStructureColors.put("minecraft:monument", Objects.requireNonNull(HexColor.from("#00ffff")));
         defaultStructureColors.put("minecraft:shipwreck", Objects.requireNonNull(HexColor.from("#00ffff")));
         defaultStructureColors.put("minecraft:shipwreck_beached", Objects.requireNonNull(HexColor.from("#00ffff")));
         defaultStructureColors.put("minecraft:ocean_ruin_cold", Objects.requireNonNull(HexColor.from("#00ffff")));
         defaultStructureColors.put("minecraft:ocean_ruin_warm", Objects.requireNonNull(HexColor.from("#00ffff")));
         defaultStructureColors.put("minecraft:buried_treasure", Objects.requireNonNull(HexColor.from("#00ffff")));
         defaultStructureColors.put("minecraft:stronghold", Objects.requireNonNull(HexColor.from("#ffff00")));
         defaultStructureColors.put("minecraft:mineshaft", Objects.requireNonNull(HexColor.from("#c0c0c0")));
         defaultStructureColors.put("minecraft:end_city", Objects.requireNonNull(HexColor.from("#ff00ff")));
         defaultStructureColors.put("minecraft:mansion", Objects.requireNonNull(HexColor.from("#8b4513")));
         defaultStructureColors.put("minecraft:igloo", Objects.requireNonNull(HexColor.from("#ffffff")));
         defaultStructureColors.put("minecraft:pillager_outpost", Objects.requireNonNull(HexColor.from("#404040")));
         defaultStructureColors.put("minecraft:nether_fossil", Objects.requireNonNull(HexColor.from("#ffffff")));
         defaultStructureColors.put("minecraft:bastion_remnant", Objects.requireNonNull(HexColor.from("#c0c0c0")));
         defaultStructureColors.put("minecraft:ruined_portal", Objects.requireNonNull(HexColor.from("#c800ff")));
         defaultStructureColors.put("minecraft:ruined_portal_desert", Objects.requireNonNull(HexColor.from("#c800ff")));
         defaultStructureColors.put("minecraft:ruined_portal_mountain", Objects.requireNonNull(HexColor.from("#c800ff")));
         defaultStructureColors.put("minecraft:ruined_portal_jungle", Objects.requireNonNull(HexColor.from("#c800ff")));
         defaultStructureColors.put("minecraft:ruined_portal_ocean", Objects.requireNonNull(HexColor.from("#c800ff")));
         defaultStructureColors.put("minecraft:ruined_portal_swamp", Objects.requireNonNull(HexColor.from("#c800ff")));
         defaultStructureColors.put("minecraft:ruined_portal_nether", Objects.requireNonNull(HexColor.from("#c800ff")));
    }

    private static Configuration config;

    public static void loadConfig() {
        configDir = new File(".", "config");
        configDir.mkdirs();
        config = loadConfiguration();

        fill = setup(config, "general", "fill", true, "If set to true the bounding boxes are filled.");
        outerBoxesOnly = setup(config, "general", "outerBoxesOnly", false, "If set to true only the outer bounding boxes are rendered.");
        alwaysVisible = setup(config, "general", "alwaysVisible", false, "If set to true boxes will be visible even through other blocks.");
        keepCacheBetweenSessions = setup(config, "general", "keepCacheBetweenSessions", false, "If set to true bounding box caches will be kept between sessions.");
        invertBoxColorPlayerInside = setup(config, "general", "invertBoxColorPlayerInside", false, "If set to true the color of any bounding box the player is inside will be inverted.");
        renderSphereAsDots = setup(config, "general", "renderSphereAsDots", false, "If set to true spheres will be rendered as dots.");
        buttonOnOverlay = setup(config, "general", "buttonEnabledOverlay", HexColor.from("#3000ff00"), "The color and alpha of the button overlay when a button is on.");
        fastRender = setup(config, "general", "fastRender", 2, "Fast render settings. Higher value for faster rendering. ");

        drawBeacons = setup(config, "beacons", "drawBeacons", true, "If set to true beacon bounding boxes will be drawn.");

        drawConduits = setup(config, "conduits", "drawConduits", true, "If set to true conduit bounding spheres will be drawn.");
        renderConduitMobHarmArea = setup(config, "conduits", "renderConduitMobHarmArea", true, "If set to true a box to show the area where hostile mobs are harmed will be drawn");

        drawBiomeBorders = setup(config, "biomeBorders", "drawBiomeBorders", true, "If set to true biome borders will be drawn.");
        renderOnlyCurrentBiome = setup(config, "biomeBorders", "renderOnlyCurrentBiome", true, "If set to true only the biome border for the current biome will be drawn.");
        biomeBordersRenderDistance = setup(config, "biomeBorders", "biomeBordersRenderDistance", 3, "The distance from the player where biome borders will be drawn.");
        biomeBordersMaxY = setup(config, "biomeBorders", "biomeBordersMaxY", -1, "The maximum top of the biome borders. If set to -1 it will use the value when activated, if set to 0 it will always track the players feet.");

        drawFlowerForests = setup(config, "flowerForests", "drawFlowerForests", true, "If set to true flower forest flower overlays will be drawn.");
        flowerForestsRenderDistance = setup(config, "flowerForests", "flowerForestsRenderDistance", 3, "The distance from the player where flower forests will be drawn.");

        drawBedrockCeilingBlocks = setup(config, "bedrockCeiling", "drawBedrockCeilingBlocks", true, "If set to true position with only one layer of bedrock will be drawn.");

//        drawVillages = setup(config, "structures", "drawVillages", false, "If set to true village bounding boxes will be drawn.");
//        drawDesertTemples = setup(config, "structures", "drawDesertTemples", true, "If set to true desert temple bounding boxes are drawn.");
//        drawJungleTemples = setup(config, "structures", "drawJungleTemples", true, "If set to true jungle temple bounding boxes are drawn.");
//        drawWitchHuts = setup(config, "structures", "drawWitchHuts", true, "If set to true witch hut bounding boxes are drawn.");
//        drawStrongholds = setup(config, "structures", "drawStrongholds", false, "If set to true stronghold bounding boxes are drawn.");
//        drawMineShafts = setup(config, "structures", "drawMineShafts", false, "If set to true mineshaft bounding boxes are drawn.");
//        drawNetherFortresses = setup(config, "structures", "drawNetherFortresses", true, "If set to true nether fortress bounding boxes are drawn.");
//        drawOceanMonuments = setup(config, "structures", "drawOceanMonuments", true, "If set to true ocean monument bounding boxes are drawn.");
//        drawEndCities = setup(config, "structures", "drawEndCities", true, "If set to true end city bounding boxes will be drawn.");
//        drawMansions = setup(config, "structures", "drawMansions", true, "If set to true woodland mansions will be drawn.");
//        drawIgloos = setup(config, "structures", "drawIgloos", true, "If set to true igloos will be drawn.");
//        drawShipwrecks = setup(config, "structures", "drawShipwrecks", true, "If set to true shipwrecks will be drawn.");
//        drawOceanRuins = setup(config, "structures", "drawOceanRuins", true, "If set to true ocean ruins will be drawn.");
//        drawBuriedTreasure = setup(config, "structures", "drawBuriedTreasures", true, "If set to true buried treasure will be drawn.");
//        drawPillagerOutposts = setup(config, "structures", "drawPillagerOutposts", true, "If set to true pillager outposts will be drawn.");
//        drawNetherFossils = setup(config, "structures", "drawNetherFossils", true, "If set to true nether fossils will be drawn.");
//        drawBastionRemnants = setup(config, "structures", "drawBastionRemnants", true, "If set to true bastion remnants will be drawn.");
//        drawRuinedPortals = setup(config, "structures", "drawRuinedPortals", true, "If set to true ruined portals will be drawn.");

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

        drawSpawnableBlocks = setup(config, "spawnableBlocks", "drawSpawnableBlocks", false, "If set to true boxes to show spawnable blocks will be drawn.");
        spawnableBlocksRenderWidth = setup(config, "spawnableBlocks", "spawnableBlocksRenderWidth", 2, "The distance from the player where spawnable blocks will be drawn in X and Z axis.");
        spawnableBlocksRenderHeight = setup(config, "spawnableBlocks", "spawnableBlocksRenderHeight", 1, "The distance from the player where spawnable blocks will be drawn in Y axis.");

        colorWorldSpawn = setup(config, "colors", "colorWorldSpawn", HexColor.from("#ff0000"), "Color of world spawn and spawn chunks bounding boxes.");
        colorLazySpawnChunks = setup(config, "colors", "colorLazySpawnChunks", HexColor.from("#ff0000"), "Color of lazy spawn chunks bounding boxes.");
        colorMobSpawners = setup(config, "colors", "colorMobSpawners", HexColor.from("#00ff00"), "Color of mob spawners.");
        colorMobSpawnersLineFarAway = setup(config, "colors", "colorMobSpawnersLineFarAway", HexColor.from("#ff0000"), "Color of mob spawner activation line if spawner far away.");
        colorMobSpawnersLineNearby = setup(config, "colors", "colorMobSpawnersLineNearby", HexColor.from("#ff7f00"), "Color of mob spawners activation line if spawner nearby.");
        colorMobSpawnersLineActive = setup(config, "colors", "colorMobSpawnersLineActive", HexColor.from("#00ff00"), "Color of mob spawners activation line if spawner active.");
        colorSlimeChunks = setup(config, "colors", "colorSlimeChunks", HexColor.from("#006000"), "Color of slime chunks bounding boxes.");
        colorAFKSpheres = setup(config, "colors", "colorAFKSpheres", HexColor.from("#ff0000"), "Color of afk spot spheres.");
        colorAFKSpheresSafeArea = setup(config, "colors", "colorAFKSpheresSafeArea", HexColor.from("#00ff00"), "Color of afk spot safe area spheres.");
        colorBiomeBorders = setup(config, "colors", "colorBiomeBorders", HexColor.from("#00ff00"), "Color of biome borders.");
        colorBeacons = setup(config, "colors", "colorBeacons", HexColor.from("#ffffff"), "Color of beacon bounding boxes.");
        colorCustom = setup(config, "colors", "colorCustom", HexColor.from("#ffffff"), "Color of all types of custom boxes.");
        colorConduits = setup(config, "colors", "colorConduits", HexColor.from("#00ffff"), "Color of conduit bounding spheres.");
        colorConduitMobHarmArea = setup(config, "colors", "colorConduitMobHarmArea", HexColor.from("#ff7f00"), "Color of conduit mob harm bounding boxes.");
        colorSpawnableBlocks = setup(config, "colors", "colorSpawnableBlocks", HexColor.from("#ff0000"), "Color of spawnable blocks.");
//        colorJungleTemples = setup(config, "colors", "colorJungleTemples", HexColor.from("#006000"), "Color of jungle temple bounding boxes.");
//        colorDesertTemples = setup(config, "colors", "colorDesertTemples", HexColor.from("#ffc800"), "Color of desert temple bounding boxes.");
//        colorWitchHuts = setup(config, "colors", "colorWitchHuts", HexColor.from("#0000ff"), "Color of witch hut bounding boxes.");
//        colorOceanMonuments = setup(config, "colors", "colorOceanMonuments", HexColor.from("#00ffff"), "Color of ocean monument bounding boxes.");
//        colorShipwrecks = setup(config, "colors", "colorShipwrecks", HexColor.from("#00ffff"), "Color of ship wrecks.");
//        colorOceanRuins = setup(config, "colors", "colorOceanRuins", HexColor.from("#00ffff"), "Color of ocean ruins.");
//        colorBuriedTreasure = setup(config, "colors", "colorBuriedTreasure", HexColor.from("#00ffff"), "Color of buried treasure.");
//        colorStrongholds = setup(config, "colors", "colorStrongholds", HexColor.from("#ffff00"), "Color of stronghold bounding boxes.");
//        colorMineShafts = setup(config, "colors", "colorMineShafts", HexColor.from("#c0c0c0"), "Color of mineshaft bounding boxes.");
//        colorNetherFortresses = setup(config, "colors", "colorNetherFortresses", HexColor.from("#ff0000"), "Color of nether fortress bounding boxes.");
//        colorEndCities = setup(config, "colors", "colorEndCities", HexColor.from("#ff00ff"), "Color of end cities.");
//        colorMansions = setup(config, "colors", "colorMansions", HexColor.from("#8b4513"), "Color of woodland mansions.");
//        colorIgloos = setup(config, "colors", "colorIgloos", HexColor.from("#ffffff"), "Color of igloos.");
//        colorPillagerOutposts = setup(config, "colors", "colorPillagerOutposts", HexColor.from("#404040"), "Color of pillager outposts.");
//        colorVillages = setup(config, "colors", "colorVillages", HexColor.from("#800080"), "Color of village bounding boxes.");
//        colorNetherFossils = setup(config, "colors", "colorNetherFossils", HexColor.from("#ffffff"), "Color of nether fossils.");
//        colorBastionRemnants = setup(config, "colors", "colorBastionRemnants", HexColor.from("#c0c0c0"), "Color of bastion remnants.");
//        colorRuinedPortals = setup(config, "colors", "colorRuinedPortals", HexColor.from("#c800ff"), "Color of ruined portals.");
        colorFlowerForestDandelion = setup(config, "colors", "colorFlowerForestDandelion", HexColor.from("#ffff00"), "Color of Flower Forest Dandelion");
        colorFlowerForestPoppy = setup(config, "colors", "colorFlowerForestPoppy", HexColor.from("#ff0000"), "Color of Flower Forest Poppy");
        colorFlowerForestAllium = setup(config, "colors", "colorFlowerForestAllium", HexColor.from("#ff00ff"), "Color of Flower Forest Allium");
        colorFlowerForestAzureBluet = setup(config, "colors", "colorFlowerForestAzureBluet", HexColor.from("#d3d3d3"), "Color of Flower Forest Azure Bluet");
        colorFlowerForestRedTulip = setup(config, "colors", "colorFlowerForestRedTulip", HexColor.from("#ff0000"), "Color of Flower Forest Red Tulip");
        colorFlowerForestOrangeTulip = setup(config, "colors", "colorFlowerForestOrangeTulip", HexColor.from("#ff681f"), "Color of Flower Forest Orange Tulip");
        colorFlowerForestWhiteTulip = setup(config, "colors", "colorFlowerForestWhiteTulip", HexColor.from("#d3d3d3"), "Color of Flower Forest White Tulip");
        colorFlowerForestPinkTulip = setup(config, "colors", "colorFlowerForestPinkTulip", HexColor.from("#ff69b4"), "Color of Flower Forest Pink Tulip");
        colorFlowerForestOxeyeDaisy = setup(config, "colors", "colorFlowerForestOxeyeDaisy", HexColor.from("#d3d3d3"), "Color of Flower Forest Oxeye Daisy");
        colorFlowerForestCornflower = setup(config, "colors", "colorFlowerForestCornflower", HexColor.from("#0000ff"), "Color of Flower Forest Cornflower");
        colorFlowerForestLilyOfTheValley = setup(config, "colors", "colorFlowerForestLilyOfTheValley", HexColor.from("#ffffff"), "Color of Flower Forest Lily Of The Valley");
        colorBedrockCeilingBlocks = setup(config, "colors", "colorBedrockCeilingBlocks", HexColor.from("#00ff00"), "Color of Bedrock Ceiling Blocks");
        config.save();
    }

    private static Configuration loadConfiguration() {
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

    public static Setting<Boolean> structureShouldRender(String key) {
        final Setting<Boolean> setting = setup(config, "structures", "drawStructure_" + key.replace(':', '_'), true, "If set to true structure %s bounding boxes will be drawn.".formatted(key));
        structureRenderSettings.put(key, setting);
        saveConfig();
        return setting;
    }

    public static Setting<HexColor> structureColor(String key) {
        final Setting<HexColor> setting = setup(config, "colors", "colorStructure_" + key.replace(':', '_'), defaultStructureColors.getOrDefault(key, HexColor.random()), "Color if structure %s bounding boxes.".formatted(key));
        structureColorSettings.put(key, setting);
        saveConfig();
        return setting;
    }

    private static <T> Setting<T> setup(Configuration config, String category, String settingName, T defaultValue, String comment) {
        Setting<T> setting = config.get(category, settingName, defaultValue);
        setting.category = category;
        setting.name = settingName;
        setting.defaultValue = defaultValue;
        if (setting.get() == null)
            setting.reset();
        setting.comment = comment + " (default: " + defaultValue.toString() + ")";
        settings.add(setting);
        return setting;
    }

    public static void Toggle(Setting<Boolean> setting) {
        setting.set(!setting.get());
    }

    public static Set<Setting<?>> getSettings() {
        return settings;
    }
}
