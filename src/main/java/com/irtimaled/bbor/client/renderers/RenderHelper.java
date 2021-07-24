package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
    public static final int QUADS = GL11.GL_QUADS;
    public static final int LINES = GL11.GL_LINES;
    public static final int LINE_LOOP = GL11.GL_LINE_LOOP;
    public static final int POINTS = GL11.GL_POINTS;

    public static void beforeRender() {
        enableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        lineWidth2();
        disableTexture();
        GlStateManager._disableCull();
        enableDepthTest();

        if (ConfigManager.alwaysVisible.get()) {
            GlStateManager._clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    public static void afterRender() {
        polygonModeFill();
        GlStateManager._enableCull();
        enableTexture();
    }

    public static void beforeRenderFont(OffsetPoint offsetPoint) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushMatrix();
        polygonModeFill();
        GL11.glTranslated(offsetPoint.getX(), offsetPoint.getY() + 0.002D, offsetPoint.getZ());
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-0.0175F, -0.0175F, 0.0175F);
        enableTexture();
        enableBlend();
        GlStateManager._blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        depthMaskTrue();
    }

    public static void afterRenderFont() {
        disableTexture();
        disableBlend();
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopMatrix();
        enableDepthTest();
    }

    public static void disableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void disableDepthTest() {
        GlStateManager._disableDepthTest();
    }

    public static void enableDepthTest() {
        GlStateManager._enableDepthTest();
    }

    public static void disableFog() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDisable(GL11.GL_FOG);
    }

    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    public static void enableBlend() {
        GlStateManager._enableBlend();
    }

    public static void disableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void enableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public static void disableTexture() {
        GlStateManager._disableTexture();
    }

    public static void enableTexture() {
        GlStateManager._enableTexture();
    }

    public static void shadeModelSmooth() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glShadeModel(GL11.GL_SMOOTH);
    }

    public static void shadeModelFlat() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glShadeModel(GL11.GL_FLAT);
    }

    public static void enablePointSmooth() {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
    }

    public static void lineWidth2() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLineWidth(2f);
    }

    public static void polygonModeLine() {
        GlStateManager._polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    }

    public static void polygonModeFill() {
        GlStateManager._polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public static void polygonOffsetMinusOne() {
        GlStateManager._polygonOffset(-1.f, -1.f);
    }

    public static void enablePolygonOffsetLine() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
    }

    public static void depthMaskTrue() {
        GlStateManager._depthMask(true);
    }

    public static void pointSize5() {
        GL11.glPointSize(5);
    }

    public static void blendFuncGui() {
        GlStateManager._blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
    }

    public static void depthFuncAlways() {
        GlStateManager._depthFunc(GL11.GL_ALWAYS);
    }

    public static void depthFuncLessEqual() {
        GlStateManager._depthFunc(GL11.GL_LEQUAL);
    }
}
