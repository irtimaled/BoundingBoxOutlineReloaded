package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SharedConstants;

public class SettingsScreen extends ListScreen {
    private static final String pillagerOutpostVersionPattern = "(?:1\\.1[4-9]|1\\.[2-9][0-9]|18w(?:4[7-9]|5[0-9])|19w|2[0-9]w).*";
    private static final String bastionRemnantVersionPattern = "(?:1\\.1[6-9]|1\\.[2-9][0-9]|20w(?:1[6-9]|[2-5][0-9])|2[1-9]w).*";
    private static final String netherFossilVersionPattern = "(?:1\\.1[6-9]|1\\.[2-9][0-9]|20w(?:1[1-9]|[2-5][0-9])|2[1-9]w).*";

    public static void show() {
        ClientInterop.displayScreen(new SettingsScreen(null));
    }

    SettingsScreen(Screen lastScreen) {
        super(lastScreen);
    }

    @Override
    protected void onDoneClicked() {
        ConfigManager.saveConfig();
        super.onDoneClicked();
    }

    @Override
    protected void setup() {
        String version = SharedConstants.getVersion().getName();
        ControlList controlList = this.getControlList();
        if (this.minecraft.world != null) controlList.setTransparentBackground();

        controlList
                .section(null,
                        (x, width) -> new BoolButton(width, I18n.format("bbor.options.active"), this.minecraft.world != null) {
                            @Override
                            public void onPressed() {
                                ClientRenderer.toggleActive();
                            }

                            @Override
                            protected boolean getValue() {
                                return ClientRenderer.getActive();
                            }
                        },
                        (x, width) -> new BoolSettingButton(width, I18n.format("bbor.options.outerBoxOnly"), ConfigManager.outerBoxesOnly),
                        (x, width) -> new BoolSettingButton(width, I18n.format("bbor.options.fill"), ConfigManager.fill))
                .section(I18n.format("bbor.features.spawnChunks"),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.spawnChunks"), BoundingBoxType.WorldSpawn),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.lazyChunks"), BoundingBoxType.LazySpawnChunks),
                        (x, width) -> new MaxYSettingSlider(width, 39, ConfigManager.worldSpawnMaxY))
                .section(I18n.format("bbor.features.slimeChunks"),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.slimeChunks"), BoundingBoxType.SlimeChunks),
                        (x, width) -> new MaxYSettingSlider(width, 39, ConfigManager.slimeChunkMaxY))
                .section(I18n.format("bbor.features.biomeBorders"),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.biomeBorders"), BoundingBoxType.BiomeBorder),
                        (x, width) -> new MaxYSettingSlider(width, 1, ConfigManager.biomeBordersMaxY),
                        (x, width) -> new IntSettingSlider(width, 1, 3, "bbor.options.distance", ConfigManager.biomeBordersRenderDistance)
                                .addDisplayValue(1, I18n.format("bbor.options.distance.nearest"))
                                .addDisplayValue(2, I18n.format("bbor.options.distance.nearer"))
                                .addDisplayValue(3, I18n.format("bbor.options.distance.normal")))
                .section(I18n.format("bbor.features.mobSpawners"),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.mobSpawners"), BoundingBoxType.MobSpawner),
                        (x, width) -> new BoolSettingButton(width, I18n.format("bbor.features.mobSpawners.spawnArea"), ConfigManager.renderMobSpawnerSpawnArea),
                        (x, width) -> new BoolSettingButton(width, I18n.format("bbor.features.mobSpawners.activationLines"), ConfigManager.renderMobSpawnerActivationLines))
                .section(I18n.format("bbor.sections.beaconsAndConduits"),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.beacons"), BoundingBoxType.Beacon),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.conduits"), BoundingBoxType.Conduit),
                        (x, width) -> new BoolSettingButton(width, I18n.format("bbor.features.conduits.mobHarmArea"), ConfigManager.renderConduitMobHarmArea))
                .section(I18n.format("bbor.features.spawnableBlocks"),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.spawnableBlocks"), BoundingBoxType.SpawnableBlocks),
                        (x, width) -> new IntSettingSlider(width, 1, 3, "bbor.options.distance.y", ConfigManager.spawnableBlocksRenderHeight)
                                .addDisplayValue(1, "2")
                                .addDisplayValue(2, "4")
                                .addDisplayValue(3, "8"),
                        (x, width) -> new IntSettingSlider(width, 1, 3, "bbor.options.distance.xz", ConfigManager.spawnableBlocksRenderWidth)
                                .addDisplayValue(1, "8")
                                .addDisplayValue(2, "16")
                                .addDisplayValue(3, "32"))
                .section(I18n.format("bbor.features.spawningSpheres"),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.features.spawningSpheres"), BoundingBoxType.AFKSphere),
                        (x, width) -> new BoolSettingButton(width, I18n.format("bbor.features.spawnableBlocks"), ConfigManager.renderAFKSpawnableBlocks))
                .section(I18n.format("bbor.tabs.structures"),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.desertTemples"), BoundingBoxType.DesertTemple),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.jungleTemples"), BoundingBoxType.JungleTemple),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.witchHuts"), BoundingBoxType.WitchHut),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.mansions"), BoundingBoxType.Mansion),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.monuments"), BoundingBoxType.OceanMonument),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.igloos"), BoundingBoxType.Igloo),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.oceanRuins"), BoundingBoxType.OceanRuin),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.buriedTreasure"), BoundingBoxType.BuriedTreasure),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.shipwrecks"), BoundingBoxType.Shipwreck),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.strongholds"), BoundingBoxType.Stronghold),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.mineshafts"), BoundingBoxType.MineShaft),
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.villages"), BoundingBoxType.Village),
                        (x, width) -> version.matches(pillagerOutpostVersionPattern) ? new BoundingBoxTypeButton(width, I18n.format("bbor.structures.pillagerOutposts"), BoundingBoxType.PillagerOutpost) : null,
                        (x, width) -> version.matches(bastionRemnantVersionPattern) ? new BoundingBoxTypeButton(width, I18n.format("bbor.structures.ruinedPortal"), BoundingBoxType.RuinedPortal) : null,
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.fortresses"), BoundingBoxType.NetherFortress),
                        (x, width) -> version.matches(netherFossilVersionPattern) ? new BoundingBoxTypeButton(width, I18n.format("bbor.structures.netherFossils"), BoundingBoxType.NetherFossil) : null,
                        (x, width) -> version.matches(bastionRemnantVersionPattern) ? new BoundingBoxTypeButton(width, I18n.format("bbor.structures.bastionRemnants"), BoundingBoxType.BastionRemnant) : null,
                        (x, width) -> new BoundingBoxTypeButton(width, I18n.format("bbor.structures.endCities"), BoundingBoxType.EndCity));
    }
}
