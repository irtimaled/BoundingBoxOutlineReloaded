package com.irtimaled.bbor.config;

import java.io.File;

public class ConfigManager {
    public static File configDir;

    public static Setting fill;
    public static Setting drawVillages;
    public static Setting drawDesertTemples;
    public static Setting drawJungleTemples;
    public static Setting drawWitchHuts;
    public static Setting drawStrongholds;
    public static Setting drawMineShafts;
    public static Setting drawNetherFortresses;
    public static Setting drawOceanMonuments;
    public static Setting alwaysVisible;
    public static Setting renderVillageAsSphere;
    public static Setting drawIronGolemSpawnArea;
    public static Setting drawVillageDoors;
    public static Setting drawSlimeChunks;
    public static Setting slimeChunkMaxY;
    public static Setting keepCacheBetweenSessions;
    public static Setting drawWorldSpawn;
    public static Setting worldSpawnMaxY;
    public static Setting drawLazySpawnChunks;
    public static Setting drawEndCities;
    public static Setting drawMansions;
    public static Setting drawShipwrecks;
    public static Setting drawOceanRuins;
    public static Setting drawBuriedTreasure;
    public static Setting drawIgloos;

    public static void loadConfig(File mcConfigDir) {
        configDir = mcConfigDir;
        Configuration config = new Configuration(new File(configDir, "BBOutlineReloaded.cfg"));
        config.load();

        fill = SetupBooleanProperty(config, "general", "fill", true, "If set to true the bounding boxes are filled. (default: true)");
        alwaysVisible = SetupBooleanProperty(config, "general", "alwaysVisible", false, "If set to true boxes will be visible even through other blocks. (default: false)");
        keepCacheBetweenSessions = SetupBooleanProperty(config, "general", "keepCacheBetweenSessions", false, "If set to true bounding box caches will be kept between sessions. (default: false)");
        drawVillages = SetupBooleanProperty(config, "features", "drawVillages", true, "If set to true village bounding boxes are drawn. (default: true)");
        renderVillageAsSphere = SetupBooleanProperty(config, "features", "renderVillageAsSphere", true, "If set to true villages will be drawn as a sphere. (default:true)");
        drawIronGolemSpawnArea = SetupBooleanProperty(config, "features", "drawIronGolemSpawnArea", true, "If set to true the iron golem spawn area of the village will be drawn. (default:true)");
        drawVillageDoors = SetupBooleanProperty(config, "features", "drawVillageDoors", false, "If set to true lines between the village centre and doors will be drawn. (default:false)");
        drawDesertTemples = SetupBooleanProperty(config, "features", "drawDesertTemples", true, "If set to true desert temple bounding boxes are drawn. (default: true)");
        drawJungleTemples = SetupBooleanProperty(config, "features", "drawJungleTemples", true, "If set to true jungle temple bounding boxes are drawn. (default: true)");
        drawWitchHuts = SetupBooleanProperty(config, "features", "drawWitchHuts", true, "If set to true witch hut bounding boxes are drawn. (default: true)");
        drawStrongholds = SetupBooleanProperty(config, "features", "drawStrongholds", false, "If set to true stronghold bounding boxes are drawn. (default: false)");
        drawMineShafts = SetupBooleanProperty(config, "features", "drawMineShafts", false, "If set to true mineshaft bounding boxes are drawn. (default: false)");
        drawNetherFortresses = SetupBooleanProperty(config, "features", "drawNetherFortresses", true, "If set to true nether fortress bounding boxes are drawn. (default: true)");
        drawOceanMonuments = SetupBooleanProperty(config, "features", "drawOceanMonuments", true, "If set to true ocean monument bounding boxes are drawn. (default: true)");
        drawSlimeChunks = SetupBooleanProperty(config, "features", "drawSlimeChunks", true, "If set to true slime chunks bounding boxes are drawn. (default: true)");
        slimeChunkMaxY = SetupIntegerProperty(config, "features", "slimeChunkMaxY", -1, "The maximum top of the slime chunk bounding box. If set to -1 it will use the value when activated, if set to 0 it will always track the player's feet. (default: -1)");
        drawWorldSpawn = SetupBooleanProperty(config, "features", "drawWorldSpawn", true, "If set to true world spawn and spawn chunks bounding boxes are drawn. (default: true)");
        worldSpawnMaxY = SetupIntegerProperty(config, "features", "worldSpawnMaxY", -1, "The maximum top of the world spawn bounding boxes. If set to -1 it will use the value when activated, if set to 0 it will always track the players feet. (default: -1)");
        drawLazySpawnChunks = SetupBooleanProperty(config, "features", "drawLazySpawnChunks", false, "If set to true the lazy spawn chunks bounding boxes will be drawn. (default: false)");
        drawEndCities = SetupBooleanProperty(config, "features", "drawEndCities", true, "If set to true end city bounding boxes will be drawn. (default: true)");
        drawMansions = SetupBooleanProperty(config, "features", "drawMansions", true, "If set to true woodland mansions will be drawn. (default: true)");
        drawShipwrecks = SetupBooleanProperty(config, "features", "drawShipwrecks", false, "If set to true shipwrecks will be drawn. (default: false)");
        drawOceanRuins = SetupBooleanProperty(config, "features", "drawOceanRuins", false, "If set to true ocean ruins will be drawn. (default: false)");
        drawBuriedTreasure = SetupBooleanProperty(config, "features", "drawBuriedTreasures", false, "If set to true buried treasure will be drawn. (default: false)");
        drawIgloos = SetupBooleanProperty(config, "features", "drawIgloos", false, "If set to true igloos will be drawn. (default: false)");
        config.save();
    }

    private static Setting SetupBooleanProperty(Configuration config, String category, String settingName, Boolean defaultValue, String comment) {
        Setting property = config.get(category, settingName, defaultValue);
        property.comment = comment;
        property.set(property.getBoolean(defaultValue));
        return property;
    }

    private static Setting SetupIntegerProperty(Configuration config, String category, String settingName, int defaultValue, String comment) {
        Setting property = config.get(category, settingName, defaultValue);
        property.comment = comment;
        property.set(property.getInt(defaultValue));
        return property;
    }
}
