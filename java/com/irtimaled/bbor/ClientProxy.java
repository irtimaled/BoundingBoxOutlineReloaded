package com.irtimaled.bbor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
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
    private KeyBinding hotKey;
    private double playerX;
    private double playerY;
    private double playerZ;
    private BoundingBox worldSpawnBoundingBox;
    private BoundingBox spawnChunksBoundingBox;
    private BoundingBox lazySpawnChunksBoundingBox;

    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent evt) {
        if (hotKey.isPressed()) {
            active = !active;
            if (active)
                activeY = playerY;
        }
    }

    @Override
    public void init() {
        super.init();
        hotKey = new KeyBinding("key.bbor.hotKey", Keyboard.KEY_B, "key.categories.bbor");
        ClientRegistry.registerKeyBinding(hotKey);
    }

    @Override
    protected boolean isRemotePlayer(EntityPlayer player) {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            EntityPlayer singlePlayer = Minecraft.getMinecraft().thePlayer;
            if (singlePlayer == null)
                return false;
            return player.getGameProfile() != singlePlayer.getGameProfile();
        }
        return true;
    }

    @Override
    public void setWorldData(long seed, int spawnX, int spawnZ) {
        worldSpawnBoundingBox = null;
        spawnChunksBoundingBox = null;
        lazySpawnChunksBoundingBox = null;
        super.setWorldData(seed, spawnX, spawnZ);
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        playerX = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) event.partialTicks;
        playerY = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) event.partialTicks;
        playerZ = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) event.partialTicks;

        if (this.active) {
            int activeDimensionId = entityPlayer.worldObj.provider.getDimensionId();
            if (boundingBoxCacheMap.containsKey(activeDimensionId)) {
                renderBoundingBoxes(boundingBoxCacheMap.get(activeDimensionId).getBoundingBoxes());
            }
        }
    }

    @SubscribeEvent
    public void clientConnectionToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent evt) {
        if (!evt.isLocal) {
            SocketAddress remoteAddress = evt.manager.getRemoteAddress();
            if (remoteAddress instanceof InetSocketAddress) {
                InetSocketAddress socketAddress = (InetSocketAddress) remoteAddress;
                loadLocalStructures(socketAddress.getHostString(), socketAddress.getPort());
            }
        }
    }

    private void loadLocalStructures(String host, int port) {
        String path = String.format("BBOutlineReloaded%s%s", File.separator, host);
        File localStructuresFolder = new File(configManager.configDir, path);
        if (!localStructuresFolder.exists()) {
            path = String.format("%s,%d", path, port);
            localStructuresFolder = new File(configManager.configDir, path);
        }
        if (!localStructuresFolder.exists())
            return;
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
        if (configManager.drawMineShafts.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "Mineshaft.dat", StructureType.MineShaft.getColor(), "*");
        }
        if (configManager.drawStrongholds.getBoolean()) {
            loadStructureNbtFile(localStructuresFolder, cache, "Stronghold.dat", StructureType.Stronghold.getColor(), "*");
        }
        if (configManager.drawVillages.getBoolean()) {
            loadVillageNbtFile(localStructuresFolder, cache, "Villages.dat");
        }

        boundingBoxCacheMap.put(0, cache);
    }

    private void loadNetherStructures(File localStructuresFolder) {
        BoundingBoxCache cache = new BoundingBoxCache();
        if (configManager.drawNetherFortresses.getBoolean())
            loadStructureNbtFile(localStructuresFolder, cache, "Fortress.dat", StructureType.NetherFortress.getColor(), "*");
        if (configManager.drawVillages.getBoolean()) {
            loadVillageNbtFile(localStructuresFolder, cache, "villages_nether.dat");
        }
        boundingBoxCacheMap.put(-1, cache);
    }

    private void loadEndStructures(File localStructuresFolder) {
        BoundingBoxCache cache = new BoundingBoxCache();
        if (configManager.drawVillages.getBoolean()) {
            loadVillageNbtFile(localStructuresFolder, cache, "Villages_end.dat");
        }
        boundingBoxCacheMap.put(1, cache);
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
            int numVillagers = village.getInteger("PopSize");
            int numVillageDoors = village.getTagList("Doors", Constants.NBT.TAG_COMPOUND).tagCount();
            BoundingBox boundingBox = BoundingBoxVillage.from(center, radius, numVillagers, numVillageDoors);
            cache.addBoundingBox(boundingBox);
        }

        FMLLog.info("Loaded %s (%d villages)", fileName, villages.length);
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
            cache.addBoundingBox(structure, boundingBoxes);
        }

        FMLLog.info("Loaded %s (%d structures with type %s)", fileName, loadedStructureCount, id);
    }

    private NBTTagCompound[] getChildCompoundTags(NBTTagCompound parent, String key) {
        NBTTagList tagList = parent.getTagList(key, Constants.NBT.TAG_COMPOUND);
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
        setWorldData(data.getLong("RandomSeed"), data.getInteger("SpawnX"), data.getInteger("SpawnZ"));
        FMLLog.info("Loaded level.dat (seed: %d, spawn: %d,%d)", seed, spawnX, spawnZ);
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

    @SubscribeEvent
    public void clientDisconnectionFromServerEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent evt) {
        active = false;
        if (configManager.keepCacheBetweenSessions.getBoolean()) return;
        initialized = false;
        worldSpawnBoundingBox = null;
        spawnChunksBoundingBox = null;
        for (BoundingBoxCache cache : boundingBoxCacheMap.values()) {
            cache.close();
        }
        boundingBoxCacheMap.clear();
    }

    private void renderBoundingBoxes(Map<BoundingBox, Set<BoundingBox>> map) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(3.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (configManager.alwaysVisible.getBoolean()) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        }

        for (BoundingBox bb : map.keySet()) {
            renderBoundingBoxes(map.get(bb));
        }

        renderBoundingBoxes(getClientBoundingBoxes());

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        if (configManager.showDebugInfo.getBoolean()) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution var5 = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int screenWidth = var5.getScaledWidth();
            mc.entityRenderer.setupOverlayRendering();
            int count = 0;
            for (BoundingBox bb : map.keySet()) {
                count += map.get(bb).size();
            }
            String debug = String.format("%d/%d", map.keySet().size(), count);
            int width = screenWidth - mc.fontRendererObj.getStringWidth(debug);

            mc.fontRendererObj.drawStringWithShadow(debug, width - 2, 2, 16777215);
        }
    }

    private void renderBoundingBoxes(Set<BoundingBox> bbList) {
        World world = Minecraft.getMinecraft().theWorld;
        Set activeChunks = getActiveChunks(world);
        for (BoundingBox bb : bbList) {

            if (activeChunks.contains(world.getChunkFromBlockCoords(bb.getMinBlockPos()).getChunkCoordIntPair()) ||
                    activeChunks.contains(world.getChunkFromBlockCoords(bb.getMaxBlockPos()).getChunkCoordIntPair())) {

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
                } else if (bb instanceof BoundingBoxSlimeChunk) {
                    renderSlimeChunk((BoundingBoxSlimeChunk) bb);
                } else if (bb instanceof BoundingBoxWorldSpawn) {
                    renderWorldSpawn((BoundingBoxWorldSpawn) bb);

                } else {
                    renderBoundingBox(bb);
                }
            }
        }
    }

    private Set getActiveChunks(World world) {
        return ReflectionHelper.getPrivateValue(World.class, world, 33);
    }

    private void renderBoundingBox(BoundingBox bb) {
        AxisAlignedBB aaBB = bb.toAxisAlignedBB();
        Color color = bb.getColor();
        renderCuboid(aaBB, color, fill());
    }

    private void renderWorldSpawn(BoundingBoxWorldSpawn bb) {
        AxisAlignedBB aaBB = bb.toAxisAlignedBB(false);
        Color color = bb.getColor();
        double y = getMaxY(configManager.worldSpawnMaxY.getInt());
        renderRectangle(aaBB, y, y, color);
    }

    private void renderSlimeChunk(BoundingBoxSlimeChunk bb) {
        AxisAlignedBB aaBB = bb.toAxisAlignedBB();
        Color color = bb.getColor();
        renderCuboid(aaBB, color, fill());

        double maxY = getMaxY(configManager.slimeChunkMaxY.getInt());
        if (maxY > 39) {
            renderRectangle(aaBB, 39, maxY, color);
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

    private void renderRectangle(AxisAlignedBB aaBB, double minY, double maxY, Color color) {
        aaBB = new AxisAlignedBB(aaBB.minX, minY, aaBB.minZ, aaBB.maxX, maxY, aaBB.maxZ);
        renderCuboid(aaBB, color, fill());
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
        GL11.glLineWidth(2.0f);
        renderCuboid(abb.addCoord(1, 1, 1), villageBB.getColor(), false);
        GL11.glLineWidth(3.0f);
    }

    private void renderCuboid(AxisAlignedBB aaBB, Color color, boolean fill) {
        aaBB = offsetAxisAlignedBB(aaBB);
        if (fill) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            renderCuboid(aaBB, 30, color);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
            GL11.glPolygonOffset(-1.f, -1.f);
        }
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderCuboid(aaBB, 255, color);
    }

    private void renderCuboid(AxisAlignedBB bb, int alphaChannel, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        worldRenderer.startDrawing(GL11.GL_QUADS);
        worldRenderer.setColorRGBA(colorR, colorG, colorB, alphaChannel);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        tessellator.draw();

        if (bb.minY == bb.maxY) {
            return;
        }

        worldRenderer.startDrawing(GL11.GL_QUADS);
        worldRenderer.setColorRGBA(colorR, colorG, colorB, alphaChannel);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        tessellator.draw();

        worldRenderer.startDrawing(GL11.GL_QUADS);
        worldRenderer.setColorRGBA(colorR, colorG, colorB, alphaChannel);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        tessellator.draw();

        worldRenderer.startDrawing(GL11.GL_QUADS);
        worldRenderer.setColorRGBA(colorR, colorG, colorB, alphaChannel);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        tessellator.draw();

        worldRenderer.startDrawing(GL11.GL_QUADS);
        worldRenderer.setColorRGBA(colorR, colorG, colorB, alphaChannel);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        tessellator.draw();

        worldRenderer.startDrawing(GL11.GL_QUADS);
        worldRenderer.setColorRGBA(colorR, colorG, colorB, alphaChannel);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        tessellator.draw();
    }

    private AxisAlignedBB offsetAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        double expandBy = 0.005F;
        return axisAlignedBB
                .expand(expandBy, expandBy, expandBy)
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
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_CONSTANT_COLOR);
        GL11.glPointSize(2f);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.startDrawing(GL11.GL_POINTS);
        worldRenderer.setColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), 255);
        for (OffsetPoint point : buildPoints(center, radius)) {
            worldRenderer.addVertex(point.getX(), point.getY(), point.getZ());
        }
        tessellator.draw();

        GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
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
        if (initialized) {
            World world = Minecraft.getMinecraft().theWorld;
            int dimensionId = world.provider.getDimensionId();
            if (dimensionId == 0) {
                if (configManager.drawWorldSpawn.getBoolean()) {
                    boundingBoxes.add(getWorldSpawnBoundingBox(spawnX, spawnZ));
                    boundingBoxes.add(getSpawnChunksBoundingBox(spawnX, spawnZ));
                }
                if (configManager.drawLazySpawnChunks.getBoolean()) ;
                boundingBoxes.add(getLazySpawnChunksBoundingBox(spawnX, spawnZ));

                if (configManager.drawSlimeChunks.getBoolean()) {
                    Set<ChunkCoordIntPair> activeChunks = getActiveChunks(world);
                    for (ChunkCoordIntPair chunk : activeChunks) {
                        if (isSlimeChunk(chunk.chunkXPos, chunk.chunkZPos)) {
                            boundingBoxes.add(BoundingBoxSlimeChunk.from(chunk, Color.GREEN));
                        }
                    }
                }
            }
        }
        return boundingBoxes;
    }

    private boolean isSlimeChunk(int chunkX, int chunkZ) {
        Random r = new Random(seed +
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
