package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.StructureProcessor;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.resource.language.I18n;

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
    protected ControlList buildList(int top, int bottom) {
        String version = SharedConstants.getGameVersion().getName();
        ControlList controlList = new ControlList(this.width, this.height, top, bottom);
        if (this.client.world != null) controlList.setTransparentBackground();

        controlList
                .section(null,
                        width -> new BoolButton(width, I18n.translate("bbor.options.active"), this.client.world != null) {
                            @Override
                            public void appendNarrations(NarrationMessageBuilder narrationMessageBuilder) {
                                this.appendDefaultNarrations(narrationMessageBuilder);
                            }

                            @Override
                            public void onPressed() {
                                ClientRenderer.toggleActive();
                            }

                            @Override
                            protected boolean getValue() {
                                return ClientRenderer.getActive();
                            }
                        },
                        width -> new BoolSettingButton(width, I18n.translate("bbor.options.outerBoxOnly"), ConfigManager.outerBoxesOnly),
                        width -> new BoolSettingButton(width, I18n.translate("bbor.options.fill"), ConfigManager.fill),
                        width -> new IntSettingSlider(width, 0, 2, "bbor.options.fastRender", ConfigManager.fastRender)
                                .addDisplayValue(0, I18n.translate("bbor.options.fastRender.0"))
                                .addDisplayValue(1, I18n.translate("bbor.options.fastRender.1"))
                                .addDisplayValue(2, I18n.translate("bbor.options.fastRender.2")))
                .section(I18n.translate("bbor.features.spawnChunks"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.spawnChunks"), BoundingBoxType.WorldSpawn),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.lazyChunks"), BoundingBoxType.LazySpawnChunks),
                        width -> new MaxYSettingSlider(width, 39, ConfigManager.worldSpawnMaxY))
                .section(I18n.translate("bbor.features.slimeChunks"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.slimeChunks"), BoundingBoxType.SlimeChunks),
                        width -> new MaxYSettingSlider(width, 39, ConfigManager.slimeChunkMaxY))
                .section(I18n.translate("bbor.features.biomeBorders"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.biomeBorders"), BoundingBoxType.BiomeBorder),
                        width -> new MaxYSettingSlider(width, 1, ConfigManager.biomeBordersMaxY),
                        width -> new IntSettingSlider(width, 1, 6, "bbor.options.distance", ConfigManager.biomeBordersRenderDistance)
                                .addDisplayValue(1, I18n.translate("bbor.options.distance.nearest"))
                                .addDisplayValue(2, I18n.translate("bbor.options.distance.nearer"))
                                .addDisplayValue(3, I18n.translate("bbor.options.distance.normal"))
                                .addDisplayValue(4, I18n.translate("bbor.options.distance.farther"))
                                .addDisplayValue(5, I18n.translate("bbor.options.distance.far"))
                                .addDisplayValue(6, I18n.translate("bbor.options.distance.veryFar")))
                .section(I18n.translate("bbor.features.flowerForests"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.flowerForests"), BoundingBoxType.FlowerForest),
                        width -> new IntSettingSlider(width, 1, 6, "bbor.options.distance", ConfigManager.flowerForestsRenderDistance)
                                .addDisplayValue(1, I18n.translate("bbor.options.distance.nearest"))
                                .addDisplayValue(2, I18n.translate("bbor.options.distance.nearer"))
                                .addDisplayValue(3, I18n.translate("bbor.options.distance.normal"))
                                .addDisplayValue(4, I18n.translate("bbor.options.distance.farther"))
                                .addDisplayValue(5, I18n.translate("bbor.options.distance.far"))
                                .addDisplayValue(6, I18n.translate("bbor.options.distance.veryFar")))
                .section(I18n.translate("bbor.features.bedrockCeilingBlocks"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.bedrockCeilingBlocks"), BoundingBoxType.BedrockCeiling))
                .section(I18n.translate("bbor.features.mobSpawners"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.mobSpawners"), BoundingBoxType.MobSpawner),
                        width -> new BoolSettingButton(width, I18n.translate("bbor.features.mobSpawners.spawnArea"), ConfigManager.renderMobSpawnerSpawnArea),
                        width -> new BoolSettingButton(width, I18n.translate("bbor.features.mobSpawners.activationLines"), ConfigManager.renderMobSpawnerActivationLines))
                .section(I18n.translate("bbor.sections.beaconsAndConduits"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.beacons"), BoundingBoxType.Beacon),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.conduits"), BoundingBoxType.Conduit),
                        width -> new BoolSettingButton(width, I18n.translate("bbor.features.conduits.mobHarmArea"), ConfigManager.renderConduitMobHarmArea))
                .section(I18n.translate("bbor.features.spawnableBlocks"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.spawnableBlocks"), BoundingBoxType.SpawnableBlocks),
                        width -> new IntSettingSlider(width, 1, 3, "bbor.options.distance.y", ConfigManager.spawnableBlocksRenderHeight)
                                .addDisplayValue(1, "2")
                                .addDisplayValue(2, "4")
                                .addDisplayValue(3, "8"),
                        width -> new IntSettingSlider(width, 1, 3, "bbor.options.distance.xz", ConfigManager.spawnableBlocksRenderWidth)
                                .addDisplayValue(1, "8")
                                .addDisplayValue(2, "16")
                                .addDisplayValue(3, "32"),
                        width -> new SafeLightSettingsSlider(width, ConfigManager.spawnableBlocksSafeLight))
                .section(I18n.translate("bbor.features.spawningSpheres"),
                        width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.features.spawningSpheres"), BoundingBoxType.AFKSphere),
                        width -> new BoolSettingButton(width, I18n.translate("bbor.features.spawnableBlocks"), ConfigManager.renderAFKSpawnableBlocks))
                .section(I18n.translate("bbor.tabs.structures"), 2,
                        generateStructureControls());
        return controlList;
    }

    private CreateControl[] generateStructureControls() {
        return StructureProcessor.supportedStructureIds
                .stream()
                .map(key -> (CreateControl) (width -> new BoundingBoxTypeButton(width, I18n.translate("bbor.structures." + key.replaceAll(":", ".")), BoundingBoxType.getByNameHash(("structure:" + key).hashCode()))))
                .distinct()
                .toArray(CreateControl[]::new);
    }

}
