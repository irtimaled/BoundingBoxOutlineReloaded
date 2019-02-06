package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.renderers.*;
import com.irtimaled.bbor.common.models.*;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientRenderer {
    private final ClientBoundingBoxProvider clientBoundingBoxProvider;
    private static final Map<Class<? extends BoundingBox>, Renderer> boundingBoxRendererMap = new HashMap<>();

    ClientRenderer(ClientDimensionCache dimensionCache) {
        this.clientBoundingBoxProvider = new ClientBoundingBoxProvider(dimensionCache);
        boundingBoxRendererMap.put(BoundingBoxVillage.class, new VillageRenderer());
        boundingBoxRendererMap.put(BoundingBoxSlimeChunk.class, new SlimeChunkRenderer());
        boundingBoxRendererMap.put(BoundingBoxWorldSpawn.class, new WorldSpawnRenderer());
        boundingBoxRendererMap.put(BoundingBoxStructure.class, new StructureRenderer());
    }

    public void render(DimensionType dimensionType, Boolean outerBoxesOnly) {
        Set<BoundingBox> boundingBoxes = clientBoundingBoxProvider.getBoundingBoxes(dimensionType, outerBoxesOnly);
        if (boundingBoxes == null || boundingBoxes.size() == 0)
            return;

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (ConfigManager.alwaysVisible.getBoolean()) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        }
        for (BoundingBox bb : boundingBoxes) {
            Renderer renderer = boundingBoxRendererMap.get(bb.getClass());
            if (renderer != null) {
                renderer.render(bb);
            }
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
