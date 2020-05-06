package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.renderers.Renderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class SettingsScreen extends GuiScreen {
    private static final int CONTROLS_WIDTH = 310;

    private final GuiScreen lastScreen;
    private final int tabIndex;

    private String title;
    private Set<IRenderableControl> controls = new HashSet<>();

    SettingsScreen(GuiScreen lastScreen, int tabIndex) {
        this.lastScreen = lastScreen;
        this.tabIndex = tabIndex;
    }

    public static void show() {
        Minecraft.getInstance().displayGuiScreen(new SettingsScreen(null, 0));
    }

    private int getY(double row) {
        return ((this.height / 6) - 12) + (int) ((row + 2.0) * 24.0);
    }

    private void addControl(IRenderableControl control) {
        this.controls.add(control);
        TypeHelper.doIfType(control, IGuiEventListener.class, this.children::add);
    }

    private void addTabs(String... labels) {
        int columns = labels.length;
        int column = 0;
        int y = getY(-2);
        for (String label : labels) {
            final int index = column;
            addControl(0, column, y, CONTROLS_WIDTH / columns,
                    (id, x, y1, width) -> new AbstractButton(id, x, y, width, label, index != tabIndex) {
                        @Override
                        public void onPressed() {
                            Minecraft.getInstance().displayGuiScreen(new SettingsScreen(lastScreen, index));
                        }
                    });
            column++;
        }

        //done button
        addControl(new AbstractButton(200, this.width / 2 - 100, getY(5.5), 200, I18n.format("gui.done")) {
            @Override
            public void onPressed() {
                ConfigManager.saveConfig();
                mc.displayGuiScreen(lastScreen);
            }
        });
    }

    private int getX(int width, int column, int offset) {
        return ((this.width - CONTROLS_WIDTH) / 2) + (column * (width + offset));
    }

    private IControl addControl(int offset, int column, int y, int width, CreateControl createControl) {
        int x = getX(width, column, offset);
        int id = controls.size();
        IControl control = createControl.create(id, x, y, width);
        TypeHelper.doIfType(control, IRenderableControl.class, this::addControl);
        return control;
    }

    private void buildTab(int tabIndex, CreateControl... createControls) {
        if (tabIndex != this.tabIndex) return;

        int offset = 4;
        int width = (CONTROLS_WIDTH - (2 * offset)) / 3;
        int column = 0;
        double row = -0.75;
        for (CreateControl createControl : createControls) {
            int y = getY(row);
            IControl control = this.addControl(offset, column, y, width, createControl);
            IRowHeight rowHeight = TypeHelper.as(control, IRowHeight.class);
            if (rowHeight != null) {
                if (column > 0) {
                    row++;
                    column = 0;
                } else {
                    row += rowHeight.getRowHeight();
                }
            } else {
                column++;
            }
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    @Override
    protected void initGui() {
        this.title = "Bounding Box Outline Reloaded";

        this.controls = new HashSet<>();
        this.addTabs(I18n.format("bbor.tabs.general"),
                I18n.format("bbor.tabs.structures"),
                I18n.format("bbor.tabs.villages"));

        buildTab(0,
                (id, x, y, width) -> new AbstractButton(id, x, y, width, I18n.format("bbor.options.active"), this.mc.world != null) {
                    @Override
                    public void onPressed() {
                        ClientRenderer.toggleActive();
                    }

                    @Override
                    protected int getState() {
                        return enabled ? ClientRenderer.getActive() ? 2 : 1 : 0;
                    }
                },
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, I18n.format("bbor.options.outerBoxOnly"), ConfigManager.outerBoxesOnly),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, I18n.format("bbor.options.fill"), ConfigManager.fill),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.features.spawnChunks"), BoundingBoxType.WorldSpawn),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.features.lazyChunks"), BoundingBoxType.LazySpawnChunks),
                (id, x, y, width) -> new MaxYSettingSlider(id, x, y, width, 39, ConfigManager.worldSpawnMaxY),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.features.slimeChunks"), BoundingBoxType.SlimeChunks),
                (id, x, y, width) -> new MaxYSettingSlider(id, x, y, width, 39, ConfigManager.slimeChunkMaxY),
                (id, x, y, width) -> (IRowHeight) () -> 0,

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.features.mobSpawners"), BoundingBoxType.MobSpawner),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, I18n.format("bbor.features.mobSpawners.spawnArea"), ConfigManager.renderMobSpawnerSpawnArea),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, I18n.format("bbor.features.mobSpawners.activationLines"), ConfigManager.renderMobSpawnerActivationLines),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.features.spawningSpheres"), BoundingBoxType.AFKSphere),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, I18n.format("bbor.features.spawningSpheres.spawnableBlocks"), ConfigManager.renderAFKSpawnableBlocks),
                (id, x, y, width) -> new IntSettingSlider(id, x, y, width, 1, 3, "bbor.options.distance", ConfigManager.afkSpawnableBlocksRenderDistance)
                        .addDisplayValue(1, I18n.format("bbor.options.distance.nearest"))
                        .addDisplayValue(2, I18n.format("bbor.options.distance.nearer"))
                        .addDisplayValue(3, I18n.format("bbor.options.distance.normal")),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.features.biomeBorders"), BoundingBoxType.BiomeBorder),
                (id, x, y, width) -> new MaxYSettingSlider(id, x, y, width, 1, ConfigManager.biomeBordersMaxY),
                (id, x, y, width) -> new IntSettingSlider(id, x, y, width, 1, 3, "bbor.options.distance", ConfigManager.biomeBordersRenderDistance)
                        .addDisplayValue(1, I18n.format("bbor.options.distance.nearest"))
                        .addDisplayValue(2, I18n.format("bbor.options.distance.nearer"))
                        .addDisplayValue(3, I18n.format("bbor.options.distance.normal")));
        buildTab(1,
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.desertTemples"), BoundingBoxType.DesertTemple),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.jungleTemples"), BoundingBoxType.JungleTemple),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.witchHuts"), BoundingBoxType.WitchHut),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.mansions"), BoundingBoxType.Mansion),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.monuments"), BoundingBoxType.OceanMonument),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.igloos"), BoundingBoxType.Igloo),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.oceanRuins"), BoundingBoxType.OceanRuin),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.buriedTreasure"), BoundingBoxType.BuriedTreasure),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.shipwrecks"), BoundingBoxType.Shipwreck),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.strongholds"), BoundingBoxType.Stronghold),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.mineshafts"), BoundingBoxType.MineShaft),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.pillagerOutposts"), BoundingBoxType.PillagerOutpost, false),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.villages"), BoundingBoxType.Village),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.fortresses"), BoundingBoxType.NetherFortress),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, I18n.format("bbor.structures.endCities"), BoundingBoxType.EndCity));
        buildTab(2,
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, I18n.format("bbor.features.villageSpheres"), ConfigManager.drawVillageSpheres),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, I18n.format("bbor.features.villageSpheres.doorLines"), ConfigManager.drawVillageDoors),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, I18n.format("bbor.features.villageSpheres.golemSpawn"), ConfigManager.drawIronGolemSpawnArea),

                (id, x, y, width) -> new IntSettingSlider(id, x, y, width, 1, 5, "bbor.features.villageSpheres.dotSize", ConfigManager.villageSphereDotSize),
                (id, x, y, width) -> new IntSettingSlider(id, x, y, width, 1, 5, "bbor.features.villageSpheres.density", ConfigManager.villageSphereDensity)
                        .addDisplayValue(1, I18n.format("bbor.features.villageSpheres.density.fewest"))
                        .addDisplayValue(2, I18n.format("bbor.features.villageSpheres.density.fewer"))
                        .addDisplayValue(3, I18n.format("bbor.features.villageSpheres.density.normal"))
                        .addDisplayValue(4, I18n.format("bbor.features.villageSpheres.density.more"))
                        .addDisplayValue(5, I18n.format("bbor.features.villageSpheres.density.most")));
    }

    private void drawScreen(int top, int bottom) {
        this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Renderer.startTextured()
                .setColor(32, 32, 32)
                .setAlpha(255)
                .addPoint(0, bottom, 0, 0, bottom / 32.0F)
                .addPoint(this.width, bottom, 0, this.width / 32.0F, bottom / 32.0F)
                .addPoint(this.width, top, 0, this.width / 32.0F, top / 32.0F)
                .addPoint(0, top, 0, 0, top / 32.0F)
                .render();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(7425);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Renderer.startTextured()
                .setAlpha(0)
                .addPoint(0, top + 4, 0, 0, 1)
                .addPoint(this.width, top + 4, 0, 1, 1)
                .setAlpha(255)
                .addPoint(this.width, top, 0, 1, 0)
                .addPoint(0, top, 0, 0, 0)
                .render();

        Renderer.startTextured()
                .setAlpha(255)
                .addPoint(0, bottom, 0, 0, 1)
                .addPoint(this.width, bottom, 0, 1, 1)
                .setAlpha(0)
                .addPoint(this.width, bottom - 4, 0, 1, 0)
                .addPoint(0, bottom - 4, 0, 0, 0)
                .render();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(7424);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void render(int mouseX, int mouseY, float unknown) {
        if (this.mc.world == null) {
            this.drawDefaultBackground();
            this.drawScreen(getY(-1), getY(5.5) - 4);
        }
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 15, 16777215);
        for (IRenderableControl control : controls) {
            control.render(mouseX, mouseY);
        }
    }
}
