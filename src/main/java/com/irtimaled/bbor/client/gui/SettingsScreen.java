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
        addControl(new AbstractButton(200, this.width / 2 - 100, getY(5.5), 200, "Done") {
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
        this.addTabs("General", "Structures", "Villages");

        buildTab(0,
                (id, x, y, width) -> new AbstractButton(id, x, y, width, "Active", this.mc.world != null) {
                    @Override
                    public void onPressed() {
                        ClientRenderer.toggleActive();
                    }

                    @Override
                    protected int getState() {
                        return enabled ? ClientRenderer.getActive() ? 2 : 1 : 0;
                    }
                },
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Outer Box Only", ConfigManager.outerBoxesOnly),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Fill", ConfigManager.fill),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Spawn Chunks", BoundingBoxType.WorldSpawn),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Lazy Chunks", BoundingBoxType.LazySpawnChunks),
                (id, x, y, width) -> new MaxYSettingSlider(id, x, y, width, 39, ConfigManager.worldSpawnMaxY),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Slime Chunks", BoundingBoxType.SlimeChunks),
                (id, x, y, width) -> new MaxYSettingSlider(id, x, y, width, 39, ConfigManager.slimeChunkMaxY),
                (id, x, y, width) -> (IRowHeight) () -> 0,

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Mob Spawners", BoundingBoxType.MobSpawner),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Spawn Area", ConfigManager.renderMobSpawnerSpawnArea),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Activation Lines", ConfigManager.renderMobSpawnerActivationLines),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Spawn Sphere", BoundingBoxType.AFKSphere),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Spawnable Blocks", ConfigManager.renderAFKSpawnableBlocks),
                (id, x, y, width) -> new IntSettingSlider(id, x, y, width, 1, 3, "Distance", ConfigManager.afkSpawnableBlocksRenderDistance)
                        .addDisplayValue(1, "Nearest")
                        .addDisplayValue(2, "Nearer")
                        .addDisplayValue(3, "Normal"),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Biome Borders", BoundingBoxType.BiomeBorder),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Only This Biome", ConfigManager.renderOnlyCurrentBiome),
                (id, x, y, width) -> new IntSettingSlider(id, x, y, width, 1, 3, "Distance", ConfigManager.biomeBordersRenderDistance)
                        .addDisplayValue(1, "Nearest")
                        .addDisplayValue(2, "Nearer")
                        .addDisplayValue(3, "Normal"));
        buildTab(1,
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Desert Temples", BoundingBoxType.DesertTemple),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Jungle Temples", BoundingBoxType.JungleTemple),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Witch Huts", BoundingBoxType.WitchHut),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Mansions", BoundingBoxType.Mansion),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Monuments", BoundingBoxType.OceanMonument),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Igloos", BoundingBoxType.Igloo),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Ocean Ruins", BoundingBoxType.OceanRuin),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Buried Treasure", BoundingBoxType.BuriedTreasure),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Shipwrecks", BoundingBoxType.Shipwreck),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Strongholds", BoundingBoxType.Stronghold),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Mineshafts", BoundingBoxType.MineShaft),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Pillager Outposts", BoundingBoxType.PillagerOutpost, false),

                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Villages", BoundingBoxType.Village),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "Fortresses", BoundingBoxType.NetherFortress),
                (id, x, y, width) -> new BoundingBoxTypeButton(id, x, y, width, "End Cities", BoundingBoxType.EndCity));
        buildTab(2,
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Village Spheres", ConfigManager.drawVillageSpheres),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Door Lines", ConfigManager.drawVillageDoors),
                (id, x, y, width) -> new BoolSettingButton(id, x, y, width, "Golem Spawn", ConfigManager.drawIronGolemSpawnArea),

                (id, x, y, width) -> new IntSettingSlider(id, x, y, width, 1, 5, "Dot Size", ConfigManager.villageSphereDotSize),
                (id, x, y, width) -> new IntSettingSlider(id, x, y, width, 1, 5, "Density", ConfigManager.villageSphereDensity)
                        .addDisplayValue(1, "Fewest")
                        .addDisplayValue(2, "Fewer")
                        .addDisplayValue(3, "Normal")
                        .addDisplayValue(4, "More")
                        .addDisplayValue(5, "Most"));
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
