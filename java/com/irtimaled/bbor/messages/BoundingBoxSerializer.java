package com.irtimaled.bbor.messages;

import com.irtimaled.bbor.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.awt.*;

public class BoundingBoxSerializer {

    public static void serialize(BoundingBox boundingBox, ByteBuf buf) {
        if (boundingBox instanceof BoundingBoxVillage) {
            serializeVillage((BoundingBoxVillage) boundingBox, buf);
        }
        if (boundingBox instanceof BoundingBoxStructure) {
            serializeStructure((BoundingBoxStructure) boundingBox, buf);
        }
    }


    private static void serializeVillage(BoundingBoxVillage boundingBox, ByteBuf buf) {
        ByteBufUtils.writeVarShort(buf, 'V');
        serializeBlockPos(boundingBox.getCenter(), buf);
        ByteBufUtils.writeVarInt(buf, boundingBox.getRadius(), 5);
        ByteBufUtils.writeVarShort(buf, boundingBox.getSpawnsIronGolems() ? 1 : 0);
        serializeColor(boundingBox.getColor(), buf);
    }

    private static void serializeStructure(BoundingBoxStructure boundingBox, ByteBuf buf) {
        ByteBufUtils.writeVarShort(buf, 'S');
        serializeCuboid(boundingBox, buf);
        serializeColor(boundingBox.getColor(), buf);
    }

    private static void serializeColor(Color color, ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, color.getRGB(), 5);
    }

    private static void serializeCuboid(BoundingBox boundingBox, ByteBuf buf) {
        serializeBlockPos(boundingBox.getMinBlockPos(), buf);
        serializeBlockPos(boundingBox.getMaxBlockPos(), buf);
    }

    private static void serializeBlockPos(BlockPos blockPos, ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, blockPos.getX(), 5);
        ByteBufUtils.writeVarInt(buf, blockPos.getY(), 5);
        ByteBufUtils.writeVarInt(buf, blockPos.getZ(), 5);
    }
}
