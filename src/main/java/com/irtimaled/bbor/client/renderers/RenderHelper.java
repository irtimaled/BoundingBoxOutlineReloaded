package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    public static void beforeRender() {
        enableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableCull();
        enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        if (ConfigManager.alwaysVisible.get()) {
            RenderSystem.disableDepthTest();
        }
    }

    public static void afterRender() {
        disableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        disableDepthTest();
        RenderSystem.enableCull();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void disableDepthTest() {
        GlStateManager._disableDepthTest();
    }

    public static void enableDepthTest() {
        GlStateManager._enableDepthTest();
    }

    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    public static void enableBlend() {
        GlStateManager._enableBlend();
    }

    public static void disableTexture() {
        GlStateManager._disableTexture();
    }

    public static void enableTexture() {
        GlStateManager._enableTexture();
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
