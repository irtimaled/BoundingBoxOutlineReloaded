package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Camera;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
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
        enableDepthTest();
        RenderSystem.enableCull();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void beforeRenderFont(MatrixStack matrixStack, OffsetPoint offsetPoint) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        matrixStack.push();
        polygonModeFill();
        matrixStack.translate(offsetPoint.getX(), offsetPoint.getY() + 0.002D, offsetPoint.getZ());
        // GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        matrixStack.multiply(new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
        matrixStack.multiply(new Quaternion(0.0F, 90.0F, 1.0F, 0.0F));
        matrixStack.scale(-0.0175F, -0.0175F, 0.0175F);
        enableTexture();
        enableBlend();
        GlStateManager._blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        depthMaskTrue();
    }

    public static void afterRenderFont(MatrixStack matrixStack) {
        disableTexture();
        disableBlend();
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        matrixStack.pop();
        enableDepthTest();
    }

//    public static void disableLighting() {
//        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
//        GL11.glDisable(GL11.GL_LIGHTING);
//    }

    public static void disableDepthTest() {
        GlStateManager._disableDepthTest();
    }

    public static void enableDepthTest() {
        GlStateManager._enableDepthTest();
    }

//    public static void disableFog() {
//        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
//        GL11.glDisable(GL11.GL_FOG);
//    }

    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    public static void enableBlend() {
        GlStateManager._enableBlend();
    }

//    public static void disableAlphaTest() {
//        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
//        GL11.glDisable(GL32.GL_ALPHA_TEST);
//    }
//
//    public static void enableAlphaTest() {
//        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
//        GL11.glEnable(GL11.GL_ALPHA_TEST);
//    }

    public static void disableTexture() {
        GlStateManager._disableTexture();
    }

    public static void enableTexture() {
        GlStateManager._enableTexture();
    }

//    public static void shadeModelSmooth() {
//        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
//        GL11.glShadeModel(GL11.GL_SMOOTH);
//    }
//
//    public static void shadeModelFlat() {
//        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
//        GL11.glShadeModel(GL11.GL_FLAT);
//    }

//    public static void enablePointSmooth() {
//        GL11.glEnable(GL11.GL_POINT_SMOOTH);
//    }

    public static void lineWidth2() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RenderSystem.lineWidth(2f);
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

    public static void blendFuncGui() {
        GlStateManager._blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
    }

    public static void depthFuncAlways() {
        GlStateManager._depthFunc(GL11.GL_ALWAYS);
    }

    public static void depthFuncLessEqual() {
        GlStateManager._depthFunc(GL11.GL_LEQUAL);
    }

    public static void drawSolidBox(Box box, VertexBuffer vertexBuffer) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION);

        bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.minZ).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).next();
        bufferBuilder.vertex(box.minX, box.minY, box.maxZ).next();

        bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).next();

        bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.minZ).next();

        bufferBuilder.vertex(box.maxX, box.minY, box.minZ).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).next();

        bufferBuilder.vertex(box.minX, box.minY, box.maxZ).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).next();

        bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
        bufferBuilder.vertex(box.minX, box.minY, box.maxZ).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();

        bufferBuilder.end();
        vertexBuffer.upload(bufferBuilder);
    }

    public static void drawOutlinedBox(Box bb, VertexBuffer vertexBuffer) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
                VertexFormats.POSITION);

        bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
        bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();

        bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
        bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();

        bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
        bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();

        bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
        bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();

        bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ).next();
        bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();

        bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ).next();
        bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();

        bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ).next();
        bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();

        bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ).next();
        bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();

        bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();
        bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();

        bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ).next();
        bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();

        bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ).next();
        bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();

        bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ).next();
        bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ).next();

        bufferBuilder.end();
        vertexBuffer.upload(bufferBuilder);
    }

    public static void applyRegionalRenderOffset(MatrixStack matrixStack)
    {

        int regionX = (((int) Camera.getX()) >> 9) << 9;
        int regionZ = (((int) Camera.getZ()) >> 9) << 9;

        matrixStack.translate(regionX - Camera.getX(), -Camera.getY(),
                regionZ - Camera.getZ());
    }
}
