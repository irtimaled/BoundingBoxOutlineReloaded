package com.irtimaled.bbor;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigManager {
    public final File configDir;

    public Property showDebugInfo;
    public Property fill;
    public Property drawVillages;
    public Property drawDesertTemples;
    public Property drawJungleTemples;
    public Property drawWitchHuts;
    public Property drawStrongholds;
    public Property drawMineShafts;
    public Property drawNetherFortresses;
    public Property drawOceanMonuments;
    public Property alwaysVisible;
    public Property renderVillageAsSphere;
    public Property drawIronGolemSpawnArea;
    public Property drawSlimeChunks;
    public Property slimeChunkMaxY;
    public Property keepCacheBetweenSessions;
    public Property drawWorldSpawn;
    public Property worldSpawnMaxY;

    private Configuration config;

    public ConfigManager(File configDir) {
        this.configDir = configDir;
        config = new Configuration(new File(configDir, "BBOutlineReloaded.cfg"));
        config.load();

        showDebugInfo = SetupBooleanProperty(config, "general", "showDebugInfo", false, "If set to true debug information will be displayed. (default: false)");
        fill = SetupBooleanProperty(config, "general", "fill", false, "If set to true the bounding boxes are filled. (default: false)");
        alwaysVisible = SetupBooleanProperty(config, "general", "alwaysVisible", false, "If set to true boxes will be visible even through other blocks. (default: false)");
        keepCacheBetweenSessions = SetupBooleanProperty(config, "general", "keepCacheBetweenSessions", false, "If set to true bounding box caches will be kept between sessions. (default: false)");
        drawVillages = SetupBooleanProperty(config, "features", "drawVillages", true, "If set to true village bounding boxes are drawn. (default: true)");
        renderVillageAsSphere = SetupBooleanProperty(config, "features", "renderVillageAsSphere", true, "If set to true villages will be drawn as a sphere. (default:true)");
        drawIronGolemSpawnArea = SetupBooleanProperty(config, "features", "drawIronGolemSpawnArea", true, "If set to true the iron golem spawn area of the village will be drawn. (default:true)");
        drawDesertTemples = SetupBooleanProperty(config, "features", "drawDesertTemples", true, "If set to true desert temple bounding boxes are drawn. (default: true)");
        drawJungleTemples = SetupBooleanProperty(config, "features", "drawJungleTemples", true, "If set to true jungle temple bounding boxes are drawn. (default: true)");
        drawWitchHuts = SetupBooleanProperty(config, "features", "drawWitchHuts", true, "If set to true witch hut bounding boxes are drawn. (default: true)");
        drawStrongholds = SetupBooleanProperty(config, "features", "drawStrongholds", false, "If set to true stronghold bounding boxes are drawn. (default: false)");
        drawMineShafts = SetupBooleanProperty(config, "features", "drawMineShafts", false, "If set to true mineshaft bounding boxes are drawn. (default: false)");
        drawNetherFortresses = SetupBooleanProperty(config, "features", "drawNetherFortresses", true, "If set to true nether fortress bounding boxes are drawn. (default: true)");
        drawOceanMonuments = SetupBooleanProperty(config, "features", "drawOceanMonuments", true, "If set to true ocean monument bounding boxes are drawn. (default: true)");
        drawSlimeChunks = SetupBooleanProperty(config, "features", "drawSlimeChunks", true, "If set to true slime chunks bounding boxes are drawn. (default: true)");
        slimeChunkMaxY = SetupIntegerProperty(config, "features", "slimeChunkMaxY", 0, "The maximum top of the slime chunk bounding box. If set to -1 it will use the value when activated, if set to 0 it will always track the player's feet. (default: 0)");
        drawWorldSpawn = SetupBooleanProperty(config, "features", "drawWorldSpawn", true, "If set to true world spawn and spawn chunks bounding boxes are drawn. (default: true)");
        worldSpawnMaxY = SetupIntegerProperty(config, "features", "worldSpawnMaxY", 0, "The maximum top of the world spawn bounding boxes. If set to -1 it will use the value when activated, if set to 0 it will always track the players feet. (default: 0)");
        config.save();
    }

    private Property SetupBooleanProperty(Configuration config, String category, String configName, Boolean defaultValue, String comment) {
        Property property = config.get(category, configName, defaultValue);
        property.comment = comment;
        property.set(property.getBoolean(defaultValue));
        return property;
    }

    private Property SetupIntegerProperty(Configuration config, String category, String configName, int defaultValue, String comment) {
        Property property = config.get(category, configName, defaultValue);
        property.comment = comment;
        property.set(property.getInt(defaultValue));
        return property;
    }
}