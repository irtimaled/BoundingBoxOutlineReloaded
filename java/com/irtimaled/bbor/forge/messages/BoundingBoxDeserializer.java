package com.irtimaled.bbor.forge.messages;

import com.irtimaled.bbor.BoundingBox;
import com.irtimaled.bbor.BoundingBoxStructure;
import com.irtimaled.bbor.BoundingBoxVillage;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class BoundingBoxDeserializer {
    public static BoundingBox deserialize(ByteBuf buf) {
        char type = (char) ByteBufUtils.readVarShort(buf);
        switch (type) {
            case 'V':
                return deserializeVillage(buf);
            case 'S':
                return deserializeStructure(buf);
        }
        return null;
    }

    private static BoundingBox deserializeStructure(ByteBuf buf) {
        BlockPos minBlockPos = deserializeBlockPos(buf);
        BlockPos maxBlockPos = deserializeBlockPos(buf);
        Color color = new Color(ByteBufUtils.readVarInt(buf, 5));
        return BoundingBoxStructure.from(minBlockPos, maxBlockPos, color);
    }

    private static BoundingBox deserializeVillage(ByteBuf buf) {
        BlockPos center = deserializeBlockPos(buf);
        int radius = ByteBufUtils.readVarInt(buf, 5);
        boolean spawnsIronGolems = ByteBufUtils.readVarShort(buf) == 1;
        Color color = new Color(ByteBufUtils.readVarInt(buf, 5));
        Set<BlockPos> doors = new HashSet<BlockPos>();
        while (buf.isReadable()) {
            BlockPos door = deserializeBlockPos(buf);
            doors.add(door);
        }
        return BoundingBoxVillage.from(center, radius, color, spawnsIronGolems, doors);
    }

    private static BlockPos deserializeBlockPos(ByteBuf buf) {
        int x = ByteBufUtils.readVarInt(buf, 5);
        int y = ByteBufUtils.readVarInt(buf, 5);
        int z = ByteBufUtils.readVarInt(buf, 5);
        return new BlockPos(x, y, z);
    }
}