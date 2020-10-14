package com.irtimaled.bbor.client.gui;

import com.google.common.hash.Hashing;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.renderers.RenderHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class WorldSaveRow extends ControlListEntry implements Comparable<WorldSaveRow> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    private static final int ICON_SIZE = 20;
    private final Minecraft client;
    private final WorldSummary worldSummary;
    private final SaveFormat saveLoader;
    private final Consumer<ControlListEntry> setSelectedEntry;
    private final ResourceLocation iconLocation;
    private final DynamicTexture icon;

    private File iconFile;
    private long lastClickTime;

    WorldSaveRow(WorldSummary worldSummary, SaveFormat saveLoader, Consumer<ControlListEntry> setSelectedEntry) {
        this.worldSummary = worldSummary;
        this.saveLoader = saveLoader;
        this.setSelectedEntry = setSelectedEntry;
        this.client = Minecraft.getInstance();
        this.iconLocation = new ResourceLocation("worlds/" + Hashing.sha1().hashUnencodedChars(worldSummary.getFileName()) + "/icon");
        this.iconFile = worldSummary.getIconFile();
        if (!this.iconFile.isFile()) {
            this.iconFile = null;
        }

        this.icon = this.loadIcon();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX > this.getX() &&
                mouseX < this.getX() + ControlList.CONTROLS_WIDTH &&
                mouseY > this.getY() &&
                mouseY < this.getY() + this.getControlHeight();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) return false;

        this.setSelectedEntry.accept(this);
        if (Util.milliTime() - this.lastClickTime < 250L) {
            done();
        } else {
            this.lastClickTime = Util.milliTime();
        }
        return true;
    }

    @Override
    public void done() {
        String fileName = this.worldSummary.getFileName();
        SaveFormat.LevelSave worldInfo = null;
        try {
            worldInfo = saveLoader.getLevelSave(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            long seed = CompressedStreamTools.readCompressed(new FileInputStream(worldInfo.resolveFilePath(FolderName.LEVEL_DAT).toFile()))
                    .getCompound("Data")
                    .getCompound("WorldGenSettings").getLong("seed");
            worldInfo.close();
            ClientInterop.saveLoaded(fileName, seed);
        } catch (IOException ignored) {
        }
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        String displayName = this.worldSummary.getDisplayName();
        String details = this.worldSummary.getFileName() + " (" + DATE_FORMAT.format(new Date(this.worldSummary.getLastTimePlayed())) + ")";

        int x = this.getX();
        int y = this.getY();
        this.client.fontRenderer.drawStringWithShadow(matrixStack, displayName, (float) (x + ICON_SIZE + 3), (float) (y + 1), 16777215);
        this.client.fontRenderer.drawStringWithShadow(matrixStack, details, (float) (x + ICON_SIZE + 3), (float) (y + 1 + this.client.fontRenderer.FONT_HEIGHT + 1), 8421504);
        this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : ICON_MISSING);
        RenderHelper.enableBlend();
        AbstractGui.blit(matrixStack, x, y, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, 32, 32);
        RenderHelper.disableBlend();
    }

    @Override
    public void filter(String lowerValue) {
        super.setVisible(lowerValue.isEmpty() ||
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
