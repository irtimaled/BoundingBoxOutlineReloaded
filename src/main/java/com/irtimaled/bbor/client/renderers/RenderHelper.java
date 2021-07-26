package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
    public static final int QUADS = GL11.GL_QUADS;
    public static final int LINES = GL11.GL_LINES;
    public static final int LINE_LOOP = GL11.GL_LINE_LOOP;
    public static final int POINTS = GL11.GL_POINTS;

    //public static final VertexFormat.DrawMode CUSTOM = ReflectionHelper.getPrivateInstanceBuilder(VertexFormat.DrawMode.class,)
    public static void beforeRender() {
        enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        lineWidth2();
        disableTexture();
        RenderSystem.disableCull();
        enableDepthTest();

        if (ConfigManager.alwaysVisible.get()) {
            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    public static void setTexture(Identifier texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
    }

    public static void resetShader() {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
    }

    public static void afterRender() {
        polygonModeFill();
        RenderSystem.enableCull();
        enableTexture();
    }

    public static void beforeRenderFont(OffsetPoint offsetPoint) {
       // RenderSystem.pushMatrix();
        polygonModeFill();
        //RenderSystem.translated(offsetPoint.getX(), offsetPoint.getY() + 0.002D, offsetPoint.getZ());
        //RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
        //RenderSystem.rotatef(0.0F, 0.0F, 1.0F, 0.0F);
        //RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
        //RenderSystem.scalef(-0.0175F, -0.0175F, 0.0175F);
        enableTexture();
        enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        depthMaskTrue();
    }

    public static void afterRenderFont() {
        disableTexture();
        disableBlend();
        //RenderSystem.popMatrix();
        enableDepthTest();
    }

    public static void disableLighting() {
        //RenderSystem.disableLighting();
    }

    public static void disableDepthTest() {
        RenderSystem.disableDepthTest();
    }

    public static void enableDepthTest() {
        RenderSystem.enableDepthTest();
    }

    public static void disableBlend() {
        RenderSystem.disableBlend();
    }

    public static void enableBlend() {
        RenderSystem.enableBlend();
    }

    public static void disableAlphaTest() {
        //RenderSystem.disableAlphaTest();
    }

    public static void enableAlphaTest() {
        //RenderSystem.enableAlphaTest();
    }

    public static void disableTexture() {
        RenderSystem.disableTexture();
    }

    public static void enableTexture() {
        RenderSystem.enableTexture();
    }

    public static void shadeModelSmooth() {
        //RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }

    public static void shadeModelFlat() {
        //RenderSystem.shadeModel(GL11.GL_FLAT);
    }

    public static void enablePointSmooth() {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
    }

    public static void lineWidth2() {
        RenderSystem.lineWidth(2f);
    }

    public static void polygonModeLine() {
        RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    }

    public static void polygonModeFill() {
        RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public static void polygonOffsetMinusOne() {
        RenderSystem.polygonOffset(-1.f, -1.f);
    }

    public static void enablePolygonOffsetLine() {
        //RenderSystem.enableLineOffset();
    }

    public static void depthMaskTrue() {
        RenderSystem.depthMask(true);
    }

    public static void pointSize5() {
        GL11.glPointSize(5);
    }

    public static void blendFuncGui() {
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
    }

    public static void depthFuncAlways() {
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
    }

    public static void depthFuncLessEqual() {
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }
}
