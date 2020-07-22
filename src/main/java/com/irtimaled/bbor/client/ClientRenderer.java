package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.interop.FlowerForestHelper;
import com.irtimaled.bbor.client.models.*;
import com.irtimaled.bbor.client.providers.*;
import com.irtimaled.bbor.client.renderers.*;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.DimensionId;

import net.minecraft.client.MinecraftClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.*;
import java.util.stream.Stream;

public class ClientRenderer {
	private static final int CHUNK_SIZE = 16;
	private static final Map<Class<? extends AbstractBoundingBox>, AbstractRenderer> boundingBoxRendererMap = new HashMap<>();

	private static boolean active;
	private static final Set<IBoundingBoxProvider> providers = new HashSet<>();

	public static boolean getActive() {
		return active;
	}

	public static void toggleActive() {
		active = !active;
		if (!active)
			return;

		Player.setActiveY();
	}

	static void deactivate() {
		active = false;
	}

	static {
		registerRenderer(BoundingBoxSlimeChunk.class, new SlimeChunkRenderer());
		registerRenderer(BoundingBoxWorldSpawn.class, new WorldSpawnRenderer());
		registerRenderer(BoundingBoxCuboid.class, new CuboidRenderer());
		registerRenderer(BoundingBoxMobSpawner.class, new MobSpawnerRenderer());
		registerRenderer(BoundingBoxSpawningSphere.class, new SpawningSphereRenderer());
		registerRenderer(BoundingBoxBeacon.class, new BeaconRenderer());
		registerRenderer(BoundingBoxBiomeBorder.class, new BiomeBorderRenderer());
		registerRenderer(BoundingBoxConduit.class, new ConduitRenderer());
		registerRenderer(BoundingBoxSpawnableBlocks.class, new SpawnableBlocksRenderer());
		registerRenderer(BoundingBoxLine.class, new LineRenderer());
		registerRenderer(BoundingBoxSphere.class, new SphereRenderer());
		registerRenderer(BoundingBoxFlowerForest.class, new FlowerForestRenderer());

		registerProvider(new SlimeChunkProvider());
		registerProvider(new WorldSpawnProvider());
		registerProvider(new SpawningSphereProvider());
		registerProvider(new BeaconProvider());
		registerProvider(new CustomBoxProvider());
		registerProvider(new CustomBeaconProvider());
		registerProvider(new BiomeBorderProvider());
		registerProvider(new MobSpawnerProvider());
		registerProvider(new ConduitProvider());
		registerProvider(new SpawnableBlocksProvider());
		registerProvider(new CustomLineProvider());
		registerProvider(new CustomSphereProvider());
		registerProvider(new FlowerForestBiomeProvider());
	}

	public static <T extends AbstractBoundingBox> void registerProvider(IBoundingBoxProvider<T> provider) {
		providers.add(provider);
	}

	public static <T extends AbstractBoundingBox> void registerRenderer(Class<? extends T> type,
			AbstractRenderer<T> renderer) {
		boundingBoxRendererMap.put(type, renderer);
	}

	private static boolean isWithinRenderDistance(AbstractBoundingBox boundingBox) {
		int renderDistanceBlocks = ClientInterop.getRenderDistanceChunks() * CHUNK_SIZE;
		int minX = MathHelper.floor(Player.getX() - renderDistanceBlocks);
		int maxX = MathHelper.floor(Player.getX() + renderDistanceBlocks);
		int minZ = MathHelper.floor(Player.getZ() - renderDistanceBlocks);
		int maxZ = MathHelper.floor(Player.getZ() + renderDistanceBlocks);

		return boundingBox.intersectsBounds(minX, minZ, maxX, maxZ);
	}

	public static void render(DimensionId dimensionId) {
		if (!active)
			return;

		Set<AbstractBoundingBox> boundingBoxes = getBoundingBoxes(dimensionId);

		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glLineWidth(2.0f);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);

		if (ConfigManager.alwaysVisible.get()) {
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		}

		for (AbstractBoundingBox key : boundingBoxes) {
			AbstractRenderer renderer = boundingBoxRendererMap.get(key.getClass());
			if (renderer == null)
				continue;

			renderer.render(key);
		}

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
	}

	private static Set<AbstractBoundingBox> getBoundingBoxes(DimensionId dimensionId) {
		Set<AbstractBoundingBox> boundingBoxes = new HashSet<>();
		for (IBoundingBoxProvider<?> provider : providers) {
			if (provider.canProvide(dimensionId)) {
				for (AbstractBoundingBox boundingBox : provider.get(dimensionId)) {
					if (isWithinRenderDistance(boundingBox)) {
						boundingBoxes.add(boundingBox);
					}
				}
			}
		}
		return boundingBoxes;
	}
}
