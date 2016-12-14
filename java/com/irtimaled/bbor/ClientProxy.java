package com.irtimaled.bbor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ClientProxy extends CommonProxy {

    private double activeY;
    private boolean active;
    private boolean outerBoxOnly;
    private KeyBinding activeHotKey;
    private KeyBinding outerBoxOnlyHotKey;
    private double playerX;
    private double playerY;
    private double playerZ;
    private BoundingBox worldSpawnBoundingBox;
    private BoundingBox spawnChunksBoundingBox;
    private BoundingBox lazySpawnChunksBoundingBox;

    public void keyPressed() {
        if (activeHotKey.isPressed()) {
            active = !active;
            if (active)
                activeY = playerY;
        } else
        if(outerBoxOnlyHotKey.isPressed()){
            outerBoxOnly = !outerBoxOnly;
        }
    }

    @Override
    public void init(ConfigManager configManager) {
        super.init(configManager);
        String category = "Bounding Box Outline Reloaded";
        activeHotKey = new KeyBinding("Toggle On/Off", Keyboard.KEY_B, category);
        outerBoxOnlyHotKey = new KeyBinding("Toggle Display Outer Box Only", Keyboard.KEY_O, category);
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.addAll(Minecraft.getMinecraft().gameSettings.keyBindings, activeHotKey, outerBoxOnlyHotKey);
    }

    @Override
    public void setWorldData(WorldData worldData) {
        worldSpawnBoundingBox = null;
        spawnChunksBoundingBox = null;
        lazySpawnChunksBoundingBox = null;
        super.setWorldData(worldData);
    }

    public void render(float partialTicks) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().player;
        playerX = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) partialTicks;
        playerY = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) partialTicks;
        playerZ = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) partialTicks;

        if (this.active) {
            DimensionType dimensionType = entityPlayer.world.provider.getDimensionType();
            Map<BoundingBox, Set<BoundingBox>> boundingBoxes = null;
            if (boundingBoxCacheMap.containsKey(dimensionType)) {
                boundingBoxes = boundingBoxCacheMap.get(dimensionType).getBoundingBoxes();
            }
            renderBoundingBoxes(boundingBoxes);
        }
    }

    public void playerConnectedToServer(NetworkManager networkManager) {
        SocketAddress remoteAddress = networkManager.getRemoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) remoteAddress;
            loadLocalStructures(socketAddress.getHostName(), socketAddress.getPort());
        }
    }

    private void loadLocalStructures(String host, int port) {
        Logger.info("Looking for local structures (host:port=%s:%d)", host, port);
        String path = String.format("BBOutlineReloaded%s%s%s%d", File.separator, host, File.separator, port);
        File localStructuresFolder = new File(configManager.configDir, path);
        Logger.info("Looking for local structures (folder=%s)", localStructuresFolder.getAbsolutePath());
        if (!localStructuresFolder.exists()) {
            path = String.format("BBOutlineReloaded%s%s", File.separator, host);
            localStructuresFolder = new File(configManager.configDir, path);
            Logger.info("Looking for local structures (folder=%s)", localStructuresFolder.getAbsolutePath());
        }
        if (!localStructuresFolder.exists()) {
            path = String.format("BBOutlineReloaded%s%s,%d", File.separator, host, port);
            localStructuresFolder = new File(configManager.configDir, path);
            Logger.info("Looking for local structures (folder=%s)", localStructuresFolder.getAbsolutePath());
        }
        if (!localStructuresFolder.exists())        {
            Logger.info("No local structures folders found");
            return;
        }
        loadLevelDat(localStructuresFolder);
        loadNetherStructures(localStructuresFolder);
        loadOverworldStructures(localStructuresFolder);
        loadEndStructures(localStructuresFolder);
    }

    private void loadOverworldStructures(File localStructuresFolder) {
        BoundingBoxCache cache = new BoundingBoxCache();
        if (configManager.drawDesertTemples.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "Temple.dat", StructureType.DesertTemple.getColor(), "TeDP");
        }
        if (configManager.drawJungleTemples.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "Temple.dat", StructureType.JungleTemple.getColor(), "TeJP");
        }
        if (configManager.drawWitchHuts.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "Temple.dat", StructureType.WitchHut.getColor(), "TeSH");
        }
        if (configManager.drawOceanMonuments.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "Monument.dat", StructureType.OceanMonument.getColor(), "*");
        }
        if (configManager.drawStrongholds.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "Stronghold.dat", StructureType.Stronghold.getColor(), "*");
        }
        if (configManager.drawMansions.getBoolean()){
            loadStructureNbtFile(localStructuresFolder, cache, "Mansion.dat", StructureType.Mansion.getColor(), "*");
        }
        if (configManager.drawMineShafts.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "Mineshaft.dat", StructureType.MineShaft.getColor(), "*");
        }
        if (configManager.drawVillages.getBoolean()) {
            loadVillageNbtFile(localStructuresFolder, cache, "Villages.dat");
        }

        boundingBoxCacheMap.put(DimensionType.OVERWORLD, cache);
    }

    private void loadNetherStructures(File localStructuresFolder) {
        BoundingBoxCache cache = new BoundingBoxCache();
        if (configManager.drawNetherFortresses.getBoolean())
            loadStructureNbtFile(localStructuresFolder, cache, "Fortress.dat", StructureType.NetherFortress.getColor(), "*");
        if (configManager.drawVillages.getBoolean()) {
            loadVillageNbtFile(localStructuresFolder, cache, "villages_nether.dat");
        }
        boundingBoxCacheMap.put(DimensionType.NETHER, cache);
    }

    private void loadEndStructures(File localStructuresFolder) {
        BoundingBoxCache cache = new BoundingBoxCache();
        if (configManager.drawVillages.getBoolean()) {
            loadVillageNbtFile(localStructuresFolder, cache, "Villages_end.dat");
        }
        if (configManager.drawEndCities.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "EndCity.dat", StructureType.EndCity.getColor(), "*");
        }
        boundingBoxCacheMap.put(DimensionType.THE_END, cache);
    }

    private void loadVillageNbtFile(File localStructuresFolder, BoundingBoxCache cache, String fileName) {
        File file = new File(localStructuresFolder, fileName);
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound[] villages = getChildCompoundTags(nbt.getCompoundTag("data"), "Villages");
        for (NBTTagCompound village : villages) {
            BlockPos center = new BlockPos(village.getInteger("CX"), village.getInteger("CY"), village.getInteger("CZ"));
            int radius = village.getInteger("Radius");
            int population = village.getInteger("PopSize");
            Set<BlockPos> doors = getDoors(village);
            BoundingBox boundingBox = BoundingBoxVillage.from(center, radius, population, doors);
            cache.addBoundingBox(boundingBox);
        }

        Logger.info("Loaded %s (%d villages)", fileName, villages.length);
    }

    private Set<BlockPos> getDoors(NBTTagCompound village) {
        Set<BlockPos> doors = new HashSet<BlockPos>();
        for (NBTTagCompound door : getChildCompoundTags(village, "Doors")) {
            doors.add(new BlockPos(door.getInteger("X"), door.getInteger("Y"), door.getInteger("Z")));
        }
        return doors;
    }

    private void loadStructureNbtFile(File localStructuresFolder, BoundingBoxCache cache, String fileName, Color color, String id) {
        File file = new File(localStructuresFolder, fileName);
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound features = nbt.getCompoundTag("data")
                .getCompoundTag("Features");
        int loadedStructureCount = 0;
        for (Object key : features.getKeySet()) {
            NBTTagCompound feature = features.getCompoundTag((String) key);
            BoundingBox structure = BoundingBoxStructure.from(feature.getIntArray("BB"), color);
            Set<BoundingBox> boundingBoxes = new HashSet<BoundingBox>();
            NBTTagCompound[] children = getChildCompoundTags(feature, "Children");
            for (NBTTagCompound child : children) {
                if (id.equals(child.getString("id")) || id.equals("*"))
                    boundingBoxes.add(BoundingBoxStructure.from(child.getIntArray("BB"), color));
            }
            if (boundingBoxes.size() > 0)
                ++loadedStructureCount;
            cache.addBoundingBoxes(structure, boundingBoxes);
        }

        Logger.info("Loaded %s (%d structures with type %s)", fileName, loadedStructureCount, id);
    }

    private NBTTagCompound[] getChildCompoundTags(NBTTagCompound parent, String key) {
        NBTTagList tagList = parent.getTagList(key, 10);
        NBTTagCompound[] result = new NBTTagCompound[tagList.tagCount()];
        for (int index = 0; index < tagList.tagCount(); index++) {
            result[index] = tagList.getCompoundTagAt(index);
        }
        return result;
    }

    private void loadLevelDat(File localStructuresFolder) {
        File file = new File(localStructuresFolder, "level.dat");
        NBTTagCompound nbt = loadNbtFile(file);
        if (nbt == null)
            return;

        NBTTagCompound data = nbt.getCompoundTag("Data");
        long seed = data.getLong("RandomSeed");
        int spawnX = data.getInteger("SpawnX");
        int spawnZ = data.getInteger("SpawnZ");
        setWorldData(new WorldData(seed, spawnX, spawnZ));
        Logger.info("Loaded level.dat (seed: %d, spawn: %d,%d)", worldData.getSeed(), worldData.getSpawnX(), worldData.getSpawnZ());
    }

    private NBTTagCompound loadNbtFile(File file) {
        if (!file.exists())
            return null;
        try {
            return CompressedStreamTools.readCompressed(new FileInputStream(file));
        } catch (IOException e) {
        }
        return null;
    }

    public void playerDisconnectedFromServer() {
        active = false;
        if (configManager.keepCacheBetweenSessions.getBoolean()) return;
        worldData = null;
        worldSpawnBoundingBox = null;
        spawnChunksBoundingBox = null;
        for (BoundingBoxCache cache : boundingBoxCacheMap.values()) {
            cache.close();
        }
        boundingBoxCacheMap.clear();
    }

    private void renderBoundingBoxes(Map<BoundingBox, Set<BoundingBox>> map) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (configManager.alwaysVisible.getBoolean()) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        }

        if(map != null)
        for (BoundingBox bb : map.keySet()) {
            if (outerBoxOnly) {
                renderBoundingBoxSet(map.get(bb));
            } else {
                renderBoundingBoxByType(bb);
            }
        }

        renderBoundingBoxSet(getClientBoundingBoxes());

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void renderBoundingBoxSet(Set<BoundingBox> bbList) {
        if(bbList != null)
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
            if (configManager.renderVillageAsSphere.getBoolean()) {
                renderBoundingBoxVillageAsSphere(villageBB);
            } else {
                renderBoundingBox(villageBB);
            }
            if (configManager.drawIronGolemSpawnArea.getBoolean() &&
                    villageBB.getSpawnsIronGolems()) {
                renderIronGolemSpawnArea(villageBB);
            }
            if(configManager.drawVillageDoors.getBoolean())
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
        double y = getMaxY(configManager.worldSpawnMaxY.getInt()) + 0.001F;
        renderRectangle(aaBB, y, y, color, false);
    }

    private void renderSlimeChunk(BoundingBoxSlimeChunk bb) {
        AxisAlignedBB aaBB = bb.toAxisAlignedBB();
        Color color = bb.getColor();
        renderCuboid(aaBB, color, fill());

        double maxY = getMaxY(configManager.slimeChunkMaxY.getInt());
        if (maxY > 39) {
            renderRectangle(aaBB, 39, maxY, color, fill());
        }
    }

    private double getMaxY(double configMaxY) {

        if (configMaxY == -1) {
            return activeY;
        } else if ((configMaxY == 0) || (playerY < configMaxY)) {
            return playerY;
        }
        return configMaxY;
    }

    private void renderRectangle(AxisAlignedBB aaBB, double minY, double maxY, Color color, Boolean fill) {
        aaBB = new AxisAlignedBB(aaBB.minX, minY, aaBB.minZ, aaBB.maxX, maxY, aaBB.maxZ);
        renderCuboid(aaBB, color, fill);
    }

    private boolean fill() {
        return configManager.fill.getBoolean();
    }

    private void renderIronGolemSpawnArea(BoundingBoxVillage villageBB) {
        BlockPos center = villageBB.getCenter();
        AxisAlignedBB abb = new AxisAlignedBB(new BlockPos(center.getX() - 8,
                center.getY() - 3,
                center.getZ() - 8),
                new BlockPos(center.getX() + 8,
                        center.getY() + 3,
                        center.getZ() + 8));
        renderCuboid(abb.addCoord(1, 1, 1), villageBB.getColor(), false);
    }

    private void renderVillageDoors(BoundingBoxVillage villageBB) {
        OffsetPoint center = new OffsetPoint(villageBB.getCenter());
        Color color = villageBB.getColor();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldRenderer = tessellator.getBuffer();

        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        worldRenderer.begin(GL11.GL_LINES, worldRenderer.getVertexFormat());
        for (BlockPos door : villageBB.getDoors()) {
            OffsetPoint point = new OffsetPoint(door);

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
        VertexBuffer worldRenderer = tessellator.getBuffer();

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
        double expandXZ = 0.001F;
        double expandY = 0;
        if (axisAlignedBB.minY != axisAlignedBB.maxY) {
            expandY = expandXZ;
        }
        return axisAlignedBB
                .expand(expandXZ, expandY, expandXZ)
                .offset(-playerX, -playerY, -playerZ);
    }

    private void renderBoundingBoxVillageAsSphere(BoundingBoxVillage bb) {
        BlockPos center = bb.getCenter();
        int radius = bb.getRadius();
        Color color = bb.getColor();
        renderSphere(center, radius, color);
    }

    private void renderSphere(BlockPos center, double radius, Color color) {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(2f);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldRenderer = tessellator.getBuffer();
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
            return x - playerX;
        }

        public double getY() {
            return y - playerY;
        }

        public double getZ() {
            return z - playerZ;
        }

        public OffsetPoint add(double x, double y, double z) {
            return new OffsetPoint(this.x + x, this.y + y, this.z + z);
        }
    }

    private Set<OffsetPoint> buildPoints(BlockPos center, double radius) {
        Set<OffsetPoint> points = new HashSet<OffsetPoint>(1200);

        double tau = 6.283185307179586D;
        double pi = tau / 2D;
        double segment = tau / 48D;
        OffsetPoint centerPoint = new OffsetPoint(center);

        for (double t = 0.0D; t < tau; t += segment)
            for (double theta = 0.0D; theta < pi; theta += segment) {
                double dx = radius * Math.sin(t) * Math.cos(theta);
                double dz = radius * Math.sin(t) * Math.sin(theta);
                double dy = radius * Math.cos(t);

                points.add(centerPoint.add(dx, dy, dz));
            }
        return points;
    }

    private Set<BoundingBox> getClientBoundingBoxes() {
        Set<BoundingBox> boundingBoxes = new HashSet<BoundingBox>();
        if (worldData != null) {
            WorldClient world = Minecraft.getMinecraft().world;
            DimensionType dimensionType = world.provider.getDimensionType();
            if (dimensionType == DimensionType.OVERWORLD) {
                if (configManager.drawWorldSpawn.getBoolean()) {
                    boundingBoxes.add(getWorldSpawnBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
                    boundingBoxes.add(getSpawnChunksBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
                }
                if (configManager.drawLazySpawnChunks.getBoolean()) {
                    boundingBoxes.add(getLazySpawnChunksBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
                }
                if (configManager.drawSlimeChunks.getBoolean()) {
                    boundingBoxes.addAll(this.getSlimeChunks());
                }
            }
        }
        return boundingBoxes;
    }

    private Set<BoundingBoxSlimeChunk> getSlimeChunks() {
        Minecraft minecraft = Minecraft.getMinecraft();
        int renderDistanceChunks = minecraft.gameSettings.renderDistanceChunks;
        int playerChunkX = MathHelper.floor(minecraft.player.posX / 16.0D);
        int playerChunkZ = MathHelper.floor(minecraft.player.posZ / 16.0D);
        Set<BoundingBoxSlimeChunk> slimeChunks = new HashSet<BoundingBoxSlimeChunk>();
        for (int chunkX = playerChunkX-renderDistanceChunks; chunkX <= playerChunkX+renderDistanceChunks; ++chunkX) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; ++chunkZ) {
                if (isSlimeChunk(chunkX, chunkZ)) {
                    ChunkPos chunk = new ChunkPos(chunkX, chunkZ);
                    BlockPos minBlockPos = new BlockPos(chunk.getXStart(), 1, chunk.getZStart());
                    BlockPos maxBlockPos = new BlockPos(chunk.getXEnd(), 38, chunk.getZEnd());
                    if (minecraft.world.isAreaLoaded(minBlockPos, maxBlockPos)) {
                        slimeChunks.add(BoundingBoxSlimeChunk.from(minBlockPos, maxBlockPos, Color.GREEN));
                    }
                }
            }
        }
        return slimeChunks;
    }

    private boolean isSlimeChunk(int chunkX, int chunkZ) {
        Random r = new Random(worldData.getSeed() +
                (long) (chunkX * chunkX * 4987142) +
                (long) (chunkX * 5947611) +
                (long) (chunkZ * chunkZ) * 4392871L +
                (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }

    private BoundingBox getSpawnChunksBoundingBox(int spawnX, int spawnZ) {
        if (spawnChunksBoundingBox != null) {
            return spawnChunksBoundingBox;
        }
        BoundingBox boundingBox = getSpawnChunksBoundingBox(spawnX, spawnZ, 12, Color.RED);
        spawnChunksBoundingBox = boundingBox;
        return boundingBox;
    }

    private BoundingBox getLazySpawnChunksBoundingBox(int spawnX, int spawnZ) {
        if (lazySpawnChunksBoundingBox != null)
            return lazySpawnChunksBoundingBox;

        BoundingBox boundingBox = getSpawnChunksBoundingBox(spawnX, spawnZ, 16, Color.RED);
        lazySpawnChunksBoundingBox = boundingBox;
        return boundingBox;
    }

    private BoundingBox getSpawnChunksBoundingBox(int spawnX, int spawnZ, int size, Color color) {
        double chunkSize = 16;
        double midOffset = chunkSize * (size / 2);
        double midX = Math.round((float) (spawnX / chunkSize)) * chunkSize;
        double midZ = Math.round((float) (spawnZ / chunkSize)) * chunkSize;
        BlockPos minBlockPos = new BlockPos(midX - midOffset, 0, midZ - midOffset);
        if (spawnX / chunkSize % 0.5D == 0.0D && spawnZ / chunkSize % 0.5D == 0.0D) {
            midX += chunkSize;
            midZ += chunkSize;
        }
        BlockPos maxBlockPos = new BlockPos(midX + midOffset, 0, midZ + midOffset);
        return BoundingBoxWorldSpawn.from(minBlockPos, maxBlockPos, color);
    }

    private BoundingBox getWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        if (worldSpawnBoundingBox != null)
            return worldSpawnBoundingBox;

        BlockPos minBlockPos = new BlockPos(spawnX - 10, 0, spawnZ - 10);
        BlockPos maxBlockPos = new BlockPos(spawnX + 10, 0, spawnZ + 10);

        BoundingBox boundingBox = BoundingBoxWorldSpawn.from(minBlockPos, maxBlockPos, Color.RED);
        worldSpawnBoundingBox = boundingBox;
        return boundingBox;
    }
}
