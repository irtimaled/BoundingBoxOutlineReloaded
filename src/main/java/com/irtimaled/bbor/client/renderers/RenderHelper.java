package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
    public static final int QUADS = GL11.GL_QUADS;
    public static final int LINES = GL11.GL_LINES;
    public static final int LINE_LOOP = GL11.GL_LINE_LOOP;
    public static final int POINTS = GL11.GL_POINTS;

    public static void beforeRender() {
        enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        lineWidth2();
        disableTexture();
        GlStateManager.disableCull();
        enableDepthTest();

        if (ConfigManager.alwaysVisible.get()) {
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        }
    }

    public static void afterRender() {
        polygonModeFill();
        GlStateManager.enableCull();
        enableTexture();
    }

    public static void beforeRenderFont(OffsetPoint offsetPoint) {
        disableDepthTest();
        GlStateManager.pushMatrix();
        polygonModeFill();
        GlStateManager.translated(offsetPoint.getX(), offsetPoint.getY() + 0.002D, offsetPoint.getZ());
        GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scalef(-0.0175F, -0.0175F, 0.0175F);
        enableTexture();
        enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        depthMaskTrue();
    }

    public static void afterRenderFont() {
        disableTexture();
        disableBlend();
        GlStateManager.popMatrix();
        enableDepthTest();
    }

    public static void disableLighting() {
        GlStateManager.disableLighting();
    }

    public static void disableDepthTest() {
        GlStateManager.disableDepthTest();
    }

    public static void enableDepthTest() {
        GlStateManager.enableDepthTest();
    }

    public static void disableFog() {
        GlStateManager.disableFog();
    }

    public static void disableBlend() {
        GlStateManager.disableBlend();
    }

    public static void enableBlend() {
        GlStateManager.enableBlend();
    }

    public static void disableAlphaTest() {
        GlStateManager.disableAlphaTest();
    }

    public static void enableAlphaTest() {
        GlStateManager.enableAlphaTest();
    }

    public static void disableTexture() {
        GlStateManager.disableTexture();
    }

    public static void enableTexture() {
        GlStateManager.enableTexture();
    }

    public static void shadeModelSmooth() {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
    }

    public static void shadeModelFlat() {
        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    public static void enablePointSmooth() {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
    }

    public static void lineWidth2() {
        GlStateManager.lineWidth(2f);
    }

    public static void polygonModeLine() {
        GlStateManager.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    }

    public static void polygonModeFill() {
        GlStateManager.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public static void polygonOffsetMinusOne() {
        GlStateManager.polygonOffset(-1.f, -1.f);
    }

    public static void enablePolygonOffsetLine() {
        GlStateManager.enableLineOffset();
    }

    public static void depthMaskTrue() {
        GlStateManager.depthMask(true);
    }

    public static void pointSize5() {
        GL11.glPointSize(5);
    }

    public static void blendFuncGui() {
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
    }
}
