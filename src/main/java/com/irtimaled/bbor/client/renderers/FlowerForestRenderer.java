package com.irtimaled.bbor.client.renderers;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.irtimaled.bbor.client.interop.FlowerForestHelper;
import com.irtimaled.bbor.client.models.BoundingBoxFlowerForest;
import com.irtimaled.bbor.common.models.Coords;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.DyeColor;

public class FlowerForestRenderer extends AbstractRenderer<BoundingBoxFlowerForest> {

	private static final Map<BlockState, Color> flowersToRGB = new HashMap<BlockState, Color>();

	static {
		flowersToRGB.put(Blocks.DANDELION.getDefaultState(), new Color(DyeColor.YELLOW.getSignColor()));
		flowersToRGB.put(Blocks.POPPY.getDefaultState(), new Color(DyeColor.RED.getSignColor()));
		flowersToRGB.put(Blocks.ALLIUM.getDefaultState(), new Color(DyeColor.MAGENTA.getSignColor()));
		flowersToRGB.put(Blocks.AZURE_BLUET.getDefaultState(), new Color(DyeColor.LIGHT_GRAY.getSignColor()));
		flowersToRGB.put(Blocks.RED_TULIP.getDefaultState(), new Color(DyeColor.RED.getSignColor()));
		flowersToRGB.put(Blocks.ORANGE_TULIP.getDefaultState(), new Color(DyeColor.ORANGE.getSignColor()));
		flowersToRGB.put(Blocks.WHITE_TULIP.getDefaultState(), new Color(DyeColor.LIGHT_GRAY.getSignColor()));
		flowersToRGB.put(Blocks.PINK_TULIP.getDefaultState(), new Color(DyeColor.PINK.getSignColor()));
		flowersToRGB.put(Blocks.OXEYE_DAISY.getDefaultState(), new Color(DyeColor.LIGHT_GRAY.getSignColor()));
		flowersToRGB.put(Blocks.CORNFLOWER.getDefaultState(), new Color(DyeColor.BLUE.getSignColor()));
		flowersToRGB.put(Blocks.LILY_OF_THE_VALLEY.getDefaultState(), new Color(DyeColor.WHITE.getSignColor()));
	}

	@Override
	public void render(BoundingBoxFlowerForest boundingBox) {
		Coords p = boundingBox.getCoords();

		renderFilledFaces(new OffsetPoint(p.getX(), p.getY() + 0.01d, p.getZ()),
				new OffsetPoint(p.getX() + 1, p.getY() + 0.01d, p.getZ() + 1),
				flowersToRGB.getOrDefault(FlowerForestHelper.getFlowerAtPos(p), new Color(0, 0, 0)), 200);

	}

}
