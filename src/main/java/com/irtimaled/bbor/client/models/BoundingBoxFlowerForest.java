package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.Coords;

import net.minecraft.block.BlockState;

public class BoundingBoxFlowerForest extends AbstractBoundingBox {

	private Coords coords;
	private BlockState state;

	public BoundingBoxFlowerForest(Coords coords, BlockState state) {
		super(BoundingBoxType.FlowerForestFlowers);
		this.coords = coords;
		this.state = state;
	}

	@Override
	public Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ) {
		return coords.getX() >= minX && coords.getZ() >= minZ && coords.getX() <= maxX && coords.getZ() <= maxZ;
	}

	public BlockState getFlower() {
		return state;
	}

	public Coords getCoords() {
		return coords;
	}

}
