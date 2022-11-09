package com.irtimaled.bbor.client.gui;

import com.google.common.hash.Hashing;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.renderers.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
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
    private static final Identifier ICON_MISSING = new Identifier("textures/misc/unknown_server.png");
    private static final int ICON_SIZE = 20;
    private final MinecraftClient client;
    private final LevelSummary worldSummary;
    private final LevelStorage saveLoader;
    private final Consumer<ControlListEntry> setSelectedEntry;
    private final Identifier iconLocation;
    private final NativeImageBackedTexture icon;

    private File iconFile;
    private long lastClickTime;

    WorldSaveRow(LevelSummary worldSummary, LevelStorage saveLoader, Consumer<ControlListEntry> setSelectedEntry) {
        this.worldSummary = worldSummary;
        this.saveLoader = saveLoader;
        this.setSelectedEntry = setSelectedEntry;
        this.client = MinecraftClient.getInstance();
        this.iconLocation = new Identifier("worlds/" + Hashing.sha1().hashUnencodedChars(worldSummary.getName()) + "/icon");
        this.iconFile = worldSummary.getFile();
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
        if (Util.getMeasuringTimeMs() - this.lastClickTime < 250L) {
            done();
        } else {
            this.lastClickTime = Util.getMeasuringTimeMs();
        }
        return true;
    }

    @Override
    public void done() {
        String fileName = this.worldSummary.getName();
        LevelStorage.Session worldInfo = null;
        try {
            worldInfo = saveLoader.createSession(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            long seed = NbtIo.readCompressed(new FileInputStream(worldInfo.getDirectory(WorldSavePath.LEVEL_DAT).toFile()))
                    .getCompound("Data")
                    .getCompound("WorldGenSettings").getLong("seed");
            worldInfo.close();
            ClientInterop.saveLoaded(fileName, seed);
        } catch (IOException ignored) {
        }
    }

    private NativeImageBackedTexture loadIcon() {
        if (this.iconFile == null || !this.iconFile.isFile()) {
            this.client.getTextureManager().destroyTexture(this.iconLocation);
            return null;
        }

        try (InputStream stream = new FileInputStream(this.iconFile)) {
            NativeImageBackedTexture texture = new NativeImageBackedTexture(NativeImage.read(stream));
            this.client.getTextureManager().registerTexture(this.iconLocation, texture);
            return texture;
        } catch (Throwable exception) {
            LOGGER.error("Invalid icon for world {}", this.worldSummary.getName(), exception);
            this.iconFile = null;
            return null;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        String displayName = this.worldSummary.getDisplayName();
        String details = this.worldSummary.getName() + " (" + DATE_FORMAT.format(new Date(this.worldSummary.getLastPlayed())) + ")";

        int x = this.getX();
        int y = this.getY();
        this.client.textRenderer.draw(matrixStack, displayName, (float) (x + ICON_SIZE + 3), (float) (y + 1), 16777215);
        this.client.textRenderer.draw(matrixStack, details, (float) (x + ICON_SIZE + 3), (float) (y + 1 + this.client.textRenderer.fontHeight + 1), 8421504);
        this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : ICON_MISSING);
        RenderHelper.enableBlend();
        DrawableHelper.drawTexture(matrixStack, x, y, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, 32, 32);
        RenderHelper.disableBlend();
    }

    @Override
    public void filter(String lowerValue) {
        super.setVisible(lowerValue.isEmpty() ||
                this.worldSummary.getDisplayName().toLowerCase().contains(lowerValue) ||
                this.worldSummary.getName().toLowerCase().contains(lowerValue));
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
