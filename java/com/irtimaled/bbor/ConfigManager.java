package com.irtimaled.bbor;

import com.irtimaled.bbor.config.Configuration;
import com.irtimaled.bbor.config.Setting;

import java.io.File;

public class ConfigManager {
    public final File configDir;

    public Setting fill;
    public Setting drawVillages;
    public Setting drawDesertTemples;
    public Setting drawJungleTemples;
    public Setting drawWitchHuts;
    public Setting drawStrongholds;
    public Setting drawMineShafts;
    public Setting drawNetherFortresses;
    public Setting drawOceanMonuments;
    public Setting alwaysVisible;
    public Setting renderVillageAsSphere;
    public Setting drawIronGolemSpawnArea;
    public Setting drawVillageDoors;
    public Setting drawSlimeChunks;
    public Setting slimeChunkMaxY;
    public Setting keepCacheBetweenSessions;
    public Setting drawWorldSpawn;
    public Setting worldSpawnMaxY;
    public Setting drawLazySpawnChunks;

    private Configuration config;

    public ConfigManager(File configDir) {
        this.configDir = configDir;
        config = new Configuration(new File(configDir, "BBOutlineReloaded.cfg"));
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
        config.save();
    }

    private Setting SetupBooleanProperty(Configuration config, String category, String settingName, Boolean defaultValue, String comment) {
        Setting property = config.get(category, settingName, defaultValue);
        property.comment = comment;
        property.set(property.getBoolean(defaultValue));
        return property;
    }

    private Setting SetupIntegerProperty(Configuration config, String category, String settingName, int defaultValue, String comment) {
        Setting property = config.get(category, settingName, defaultValue);
        property.comment = comment;
        property.set(property.getInt(defaultValue));
        return property;
    }
}