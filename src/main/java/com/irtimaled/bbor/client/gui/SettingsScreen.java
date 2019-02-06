package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.renderers.Renderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.config.ConfigManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class SettingsScreen extends Screen {
    private static final int CONTROLS_WIDTH = 310;

    private final Screen lastScreen;
    private final int tabIndex;

    private Set<IRenderableControl> controls = new HashSet<>();

    SettingsScreen(Screen lastScreen, int tabIndex) {
        super(new StringTextComponent(ClientProxy.Name));
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
                    (x, y1, width) -> new AbstractButton(x, y, width, label, index != tabIndex) {
                        @Override
                        public void onPressed() {
                            Minecraft.getInstance().displayGuiScreen(new SettingsScreen(lastScreen, index));
                        }
                    });
            column++;
        }

        //done button
        addControl(new AbstractButton(this.width / 2 - 100, getY(5.5), 200, "Done") {
            @Override
            public void onPressed() {
                ConfigManager.saveConfig();
                minecraft.displayGuiScreen(lastScreen);
            }
        });
    }

    private int getX(int width, int column, int offset) {
        return ((this.width - CONTROLS_WIDTH) / 2) + (column * (width + offset));
    }

    private IControl addControl(int offset, int column, int y, int width, CreateControl createControl) {
        int x = getX(width, column, offset);
        IControl control = createControl.create(x, y, width);
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
    protected void init() {
        this.controls = new HashSet<>();
        this.addTabs("General", "Structures");

        buildTab(0,
                (x, y, width) -> new AbstractButton(x, y, width, "Active", this.minecraft.world != null) {
                    @Override
                    public void onPressed() {
                        ClientProxy.toggleActive();
                    }

                    @Override
                    protected int getState() {
                        return active ? ClientProxy.active ? 2 : 1 : 0;
                    }
                },
                (x, y, width) -> new BoolSettingButton(x, y, width, "Outer Box Only", ConfigManager.outerBoxesOnly),
                (x, y, width) -> new BoolSettingButton(x, y, width, "Fill", ConfigManager.fill),

                (x, y, width) -> (IRowHeight) () -> 0.5,

                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Spawn Chunks", BoundingBoxType.WorldSpawn),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Lazy Chunks", BoundingBoxType.LazySpawnChunks),
                (x, y, width) -> new MaxYSettingSlider(x, y, width, 39, ConfigManager.worldSpawnMaxY),

                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Slime Chunks", BoundingBoxType.SlimeChunks),
                (x, y, width) -> new MaxYSettingSlider(x, y, width, 39, ConfigManager.slimeChunkMaxY),
                (x, y, width) -> (IRowHeight) () -> 0,

                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Mob Spawners", BoundingBoxType.MobSpawner),
                (x, y, width) -> new BoolSettingButton(x, y, width, "Spawn Area", ConfigManager.renderMobSpawnerSpawnArea),
                (x, y, width) -> new BoolSettingButton(x, y, width, "Activation Lines", ConfigManager.renderMobSpawnerActivationLines));
        buildTab(1,
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Desert Temples", BoundingBoxType.DesertTemple),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Jungle Temples", BoundingBoxType.JungleTemple),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Witch Huts", BoundingBoxType.WitchHut),

                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Mansions", BoundingBoxType.Mansion),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Monuments", BoundingBoxType.OceanMonument),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Igloos", BoundingBoxType.Igloo),

                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Ocean Ruins", BoundingBoxType.OceanRuin),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Buried Treasure", BoundingBoxType.BuriedTreasure),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Shipwrecks", BoundingBoxType.Shipwreck),

                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Pillager Outposts", BoundingBoxType.PillagerOutpost),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Strongholds", BoundingBoxType.Stronghold),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Mineshafts", BoundingBoxType.MineShaft),

                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "Fortresses", BoundingBoxType.NetherFortress),
                (x, y, width) -> new BoundingBoxTypeButton(x, y, width, "End Cities", BoundingBoxType.EndCity));
    }

    private void drawScreen(int top, int bottom) {
        this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);

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
        GLX.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);

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
        GLX.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void render(int mouseX, int mouseY, float unknown) {
        if (this.minecraft.world == null) {
            this.renderBackground();
            this.drawScreen(getY(-1), getY(5.5) - 4);
        }
        this.drawCenteredString(this.font, title.getUnformattedComponentText(), this.width / 2, 15, 16777215);
        for (IRenderableControl control : controls) {
            control.render(mouseX, mouseY);
        }
    }
}
