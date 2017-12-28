package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.VillageColorCache;
import com.irtimaled.bbor.common.VillageProcessor;
import com.irtimaled.bbor.common.models.*;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientProxy extends CommonProxy {
    private boolean active;
    private boolean outerBoxOnly;
    private KeyBinding activeHotKey;
    private KeyBinding outerBoxOnlyHotKey;
    private ClientBoundingBoxProvider clientBoundingBoxProvider;

    public void keyPressed() {
        if (activeHotKey.isPressed()) {
            active = !active;
            if (active)
                PlayerData.setActiveY();
        } else if (outerBoxOnlyHotKey.isPressed()) {
            outerBoxOnly = !outerBoxOnly;
        }
    }

    @Override
    public void init() {
        String category = "Bounding Box Outline Reloaded";
        activeHotKey = new KeyBinding("Toggle On/Off", Keyboard.KEY_B, category);
        outerBoxOnlyHotKey = new KeyBinding("Toggle Display Outer Box Only", Keyboard.KEY_O, category);
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.addAll(Minecraft.getMinecraft().gameSettings.keyBindings, activeHotKey, outerBoxOnlyHotKey);
        ClientDimensionCache clientDimensionCache = new ClientDimensionCache();
        clientBoundingBoxProvider = new ClientBoundingBoxProvider(clientDimensionCache);
        dimensionCache = clientDimensionCache;
    }

    public void render(float partialTicks) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().player;
        PlayerData.setPlayerPosition(partialTicks, entityPlayer);

        if (this.active) {
            DimensionType dimensionType = DimensionType.getById(entityPlayer.dimension);
            Map<BoundingBox, Set<BoundingBox>> boundingBoxes = null;
            BoundingBoxCache boundingBoxCache = dimensionCache.getBoundingBoxes(dimensionType);
            if (boundingBoxCache != null) {
                boundingBoxes = boundingBoxCache.getBoundingBoxes();
            }
            renderBoundingBoxes(boundingBoxes, clientBoundingBoxProvider.getClientBoundingBoxes(dimensionType));
        }
    }

    public void playerConnectedToServer(NetworkManager networkManager) {
        SocketAddress remoteAddress = networkManager.getRemoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) remoteAddress;
            NBTFileParser.loadLocalDatFiles(socketAddress.getHostName(), socketAddress.getPort(), dimensionCache);
        }
    }

    public void playerDisconnectedFromServer() {
        active = false;
        if (ConfigManager.keepCacheBetweenSessions.getBoolean()) return;
        VillageColorCache.clear();
        dimensionCache.clear();
        villageProcessors.forEach(VillageProcessor::clear);
    }

    private void renderBoundingBoxes(Map<BoundingBox, Set<BoundingBox>> map, Set<BoundingBox> clientBoundingBoxes) {
        if (map == null && clientBoundingBoxes == null)
            return;

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (ConfigManager.alwaysVisible.getBoolean()) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        }

        if (map != null)
            for (BoundingBox bb : map.keySet()) {
                if (outerBoxOnly) {
                    renderBoundingBoxSet(map.get(bb));
                } else {
                    renderBoundingBoxByType(bb);
                }
            }

        if (clientBoundingBoxes != null)
            renderBoundingBoxSet(clientBoundingBoxes);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void renderBoundingBoxSet(Set<BoundingBox> bbList) {
        if (bbList != null)
            for (BoundingBox bb : bbList) {
                renderBoundingBoxByType(bb);
            }
    }

    private void renderBoundingBoxByType(BoundingBox bb) {
        WorldClient world = Minecraft.getMinecraft().world;
        if (!world.isAreaLoaded(bb.getMinBlockPos(), bb.getMaxBlockPos())) {
            return;
        }

        if (bb instanceof BoundingBoxVillage) {
            BoundingBoxVillage villageBB = (BoundingBoxVillage) bb;
            if (ConfigManager.renderVillageAsSphere.getBoolean()) {
                renderBoundingBoxVillageAsSphere(villageBB);
            } else {
                renderBoundingBox(villageBB);
            }
            if (ConfigManager.drawIronGolemSpawnArea.getBoolean() &&
                    villageBB.getSpawnsIronGolems()) {
                renderIronGolemSpawnArea(villageBB);
            }
            if (ConfigManager.drawVillageDoors.getBoolean())
                renderVillageDoors(villageBB);
        } else if (bb instanceof BoundingBoxSlimeChunk) {
            renderSlimeChunk((BoundingBoxSlimeChunk) bb);
        } else if (bb instanceof BoundingBoxWorldSpawn) {
            renderWorldSpawn((BoundingBoxWorldSpawn) bb);
        } else {
            renderBoundingBox(bb);
        }
    }

    private void renderBoundingBox(BoundingBox bb) {
        AxisAlignedBB aaBB = bb.toAxisAlignedBB();
        Color color = bb.getColor();
        renderCuboid(aaBB, color, fill());
    }

    private void renderWorldSpawn(BoundingBoxWorldSpawn bb) {
        AxisAlignedBB aaBB = bb.toAxisAlignedBB(false);
        Color color = bb.getColor();
        double y = PlayerData.getMaxY(ConfigManager.worldSpawnMaxY.getInt()) + 0.001F;
        renderRectangle(aaBB, y, y, color, false);
    }

    private void renderSlimeChunk(BoundingBoxSlimeChunk bb) {
        AxisAlignedBB aaBB = bb.toAxisAlignedBB();
        Color color = bb.getColor();
        renderCuboid(aaBB, color, fill());

        double maxY = PlayerData.getMaxY(ConfigManager.slimeChunkMaxY.getInt());
        if (maxY > 39) {
            renderRectangle(aaBB, 39, maxY, color, fill());
        }
    }

    private void renderRectangle(AxisAlignedBB aaBB, double minY, double maxY, Color color, Boolean fill) {
        aaBB = new AxisAlignedBB(aaBB.minX, minY, aaBB.minZ, aaBB.maxX, maxY, aaBB.maxZ);
        renderCuboid(aaBB, color, fill);
    }

    private boolean fill() {
        return ConfigManager.fill.getBoolean();
    }

    private void renderIronGolemSpawnArea(BoundingBoxVillage villageBB) {
        BlockPos center = villageBB.getCenter();
        AxisAlignedBB abb = new AxisAlignedBB(new BlockPos(center.getX() - 8,
                center.getY() - 3,
                center.getZ() - 8),
                new BlockPos(center.getX() + 8,
                        center.getY() + 3,
                        center.getZ() + 8))
                .offset(villageBB.getCenterOffsetX(), 0.0, villageBB.getCenterOffsetZ());

        renderCuboid(abb, villageBB.getColor(), false);
    }

    private void renderVillageDoors(BoundingBoxVillage villageBB) {
        OffsetPoint center = new OffsetPoint(villageBB.getCenter())
                .add(villageBB.getCenterOffsetX(), 0.0, villageBB.getCenterOffsetZ());
        Color color = villageBB.getColor();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        worldRenderer.begin(GL11.GL_LINES, worldRenderer.getVertexFormat());
        for (BlockPos door : villageBB.getDoors()) {
            OffsetPoint point = new OffsetPoint(door).add(0.5, 0, 0.5);

            worldRenderer.pos(point.getX(), point.getY(), point.getZ()).color(colorR, colorG, colorB, 255).endVertex();
            worldRenderer.pos(center.getX(), center.getY(), center.getZ()).color(colorR, colorG, colorB, 255).endVertex();
        }
        tessellator.draw();
    }

    private void renderCuboid(AxisAlignedBB aaBB, Color color, boolean fill) {
        aaBB = offsetAxisAlignedBB(aaBB);
        if (fill) {
            renderFilledCuboid(aaBB, color);
        }
        renderUnfilledCuboid(aaBB, color);
    }

    private void renderFilledCuboid(AxisAlignedBB aaBB, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        renderCuboid(aaBB, 30, color);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.f, -1.f);
    }

    private void renderUnfilledCuboid(AxisAlignedBB aaBB, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderCuboid(aaBB, 255, color);
    }

    private void renderCuboid(AxisAlignedBB bb, int alphaChannel, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();

        if (bb.minY != bb.maxY) {
            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
        }
        tessellator.draw();
    }

    private AxisAlignedBB offsetAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        double growXZ = 0.001F;
        double growY = 0;
        if (axisAlignedBB.minY != axisAlignedBB.maxY) {
            growY = growXZ;
        }
        return axisAlignedBB
                .grow(growXZ, growY, growXZ)
                .offset(-PlayerData.getX(), -PlayerData.getY(), -PlayerData.getZ());
    }

    private void renderBoundingBoxVillageAsSphere(BoundingBoxVillage bb) {
        OffsetPoint center = new OffsetPoint(bb.getCenter())
                .add(bb.getCenterOffsetX(), 0.0, bb.getCenterOffsetZ());
        ;
        int radius = bb.getRadius();
        Color color = bb.getColor();
        renderSphere(center, radius, color);
    }

    private void renderSphere(OffsetPoint center, double radius, Color color) {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(2f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (OffsetPoint point : buildPoints(center, radius)) {
            worldRenderer.pos(point.getX(), point.getY(), point.getZ())
                    .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                    .endVertex();
        }
        tessellator.draw();
    }

    private class OffsetPoint {
        private final double x;
        private final double y;
        private final double z;

        public OffsetPoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public OffsetPoint(BlockPos blockPos) {
            x = blockPos.getX();
            y = blockPos.getY();
            z = blockPos.getZ();
        }

        public double getX() {
            return x - PlayerData.getX();
        }

        public double getY() {
            return y - PlayerData.getY();
        }

        public double getZ() {
            return z - PlayerData.getZ();
        }

        public OffsetPoint add(double x, double y, double z) {
            return new OffsetPoint(this.x + x, this.y + y, this.z + z);
        }
    }

    private Set<OffsetPoint> buildPoints(OffsetPoint center, double radius) {
        Set<OffsetPoint> points = new HashSet<>(1200);

        double tau = 6.283185307179586D;
        double pi = tau / 2D;
        double segment = tau / 48D;

        for (double t = 0.0D; t < tau; t += segment)
            for (double theta = 0.0D; theta < pi; theta += segment) {
                double dx = radius * Math.sin(t) * Math.cos(theta);
                double dz = radius * Math.sin(t) * Math.sin(theta);
                double dy = radius * Math.cos(t);

                points.add(center.add(dx, dy, dz));
            }
        return points;
    }
}
