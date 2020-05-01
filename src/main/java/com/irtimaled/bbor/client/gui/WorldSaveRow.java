package com.irtimaled.bbor.client.gui;

import com.google.common.hash.Hashing;
import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorldSaveRow extends ControlListEntry implements Comparable<WorldSaveRow> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    private static final int ICON_SIZE = 20;
    private final Minecraft client;
    private final WorldSummary worldSummary;
    private final ISaveFormat saveLoader;
    private final ResourceLocation iconLocation;
    private final DynamicTexture icon;

    private File iconFile;
    private long lastClickTime;

    WorldSaveRow(WorldSummary worldSummary, ISaveFormat saveLoader) {
        this.worldSummary = worldSummary;
        this.saveLoader = saveLoader;
        this.client = Minecraft.getInstance();
        this.iconLocation = new ResourceLocation("worlds/" + Hashing.sha1().hashUnencodedChars(worldSummary.getFileName()) + "/icon");
        this.iconFile = saveLoader.getFile(worldSummary.getFileName(), "icon.png");
        if (!this.iconFile.isFile()) {
            this.iconFile = null;
        }

        this.icon = this.loadIcon();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.list.setSelectedIndex(this.index);
        if (Util.milliTime() - this.lastClickTime < 250L) {
            loadWorld();
            return true;
        } else {
            this.lastClickTime = Util.milliTime();
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    void loadWorld() {
        String fileName = this.worldSummary.getFileName();
        WorldInfo worldInfo = saveLoader.getWorldInfo(fileName);
        long seed = worldInfo.getSeed();
        ClientInterop.saveLoaded(fileName, seed);
    }

    private DynamicTexture loadIcon() {
        if (this.iconFile == null || !this.iconFile.isFile()) {
            this.client.getTextureManager().deleteTexture(this.iconLocation);
            return null;
        }

        try (InputStream stream = new FileInputStream(this.iconFile)) {
            DynamicTexture texture = new DynamicTexture(NativeImage.read(stream));
            this.client.getTextureManager().loadTexture(this.iconLocation, texture);
            return texture;
        } catch (Throwable exception) {
            LOGGER.error("Invalid icon for world {}", this.worldSummary.getFileName(), exception);
            this.iconFile = null;
            return null;
        }
    }

    @Override
    public void render(int mouseX, int mouseY) {
        String displayName = this.worldSummary.getDisplayName();
        String details = this.worldSummary.getFileName() + " (" + DATE_FORMAT.format(new Date(this.worldSummary.getLastTimePlayed())) + ")";

        int x = this.getX();
        int y = this.getY();
        this.client.fontRenderer.drawString(displayName, (float) (x + ICON_SIZE + 3), (float) (y + 1), 16777215);
        this.client.fontRenderer.drawString(details, (float) (x + ICON_SIZE + 3), (float) (y + 1 + this.client.fontRenderer.FONT_HEIGHT + 1), 8421504);
        this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : ICON_MISSING);
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, 32.0F, 32.0F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public int getControlWidth() {
        return 310;
    }

    @Override
    public void filter(String lowerValue) {
        setVisible(lowerValue == "" ||
                this.worldSummary.getDisplayName().toLowerCase().contains(lowerValue) ||
                this.worldSummary.getFileName().toLowerCase().contains(lowerValue));
    }

    @Override
    public void close() {
        if (this.icon != null) {
            this.icon.close();
        }
    }

    @Override
    public int compareTo(WorldSaveRow other) {
        return this.worldSummary.compareTo(other.worldSummary);
    }
}
