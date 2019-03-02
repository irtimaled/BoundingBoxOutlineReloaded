package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxSlimeChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class OverworldChunkProcessor extends ChunkProcessor {
    private final long seed;

    public OverworldChunkProcessor(BoundingBoxCache boundingBoxCache, long seed) {
        super(boundingBoxCache);
        this.seed = seed;
        supportedStructures.add(BoundingBoxType.DesertTemple);
        supportedStructures.add(BoundingBoxType.JungleTemple);
        supportedStructures.add(BoundingBoxType.WitchHut);
        supportedStructures.add(BoundingBoxType.OceanMonument);
        supportedStructures.add(BoundingBoxType.Stronghold);
        supportedStructures.add(BoundingBoxType.Mansion);
        supportedStructures.add(BoundingBoxType.MineShaft);
        supportedStructures.add(BoundingBoxType.Shipwreck);
        supportedStructures.add(BoundingBoxType.OceanRuin);
        supportedStructures.add(BoundingBoxType.BuriedTreasure);
        supportedStructures.add(BoundingBoxType.Igloo);
        supportedStructures.add(BoundingBoxType.PillagerOutpost);
    }

    private boolean isSlimeChunk(int chunkX, int chunkZ) {
        Random r = new Random(seed +
                (long) (chunkX * chunkX * 4987142) +
                (long) (chunkX * 5947611) +
                (long) (chunkZ * chunkZ) * 4392871L +
                (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }

    private void addSlimeChunk(ChunkPos chunk) {
        if(!isSlimeChunk(chunk.x, chunk.z)) return;
        BlockPos minBlockPos = new BlockPos(chunk.getXStart(), 1, chunk.getZStart());
        BlockPos maxBlockPos = new BlockPos(chunk.getXEnd(), 38, chunk.getZEnd());
        boundingBoxCache.addBoundingBox(BoundingBoxSlimeChunk.from(minBlockPos, maxBlockPos));
    }

    @Override
    public void process(Chunk chunk) {
        super.process(chunk);
        addSlimeChunk(chunk.getPos());
    }
}
