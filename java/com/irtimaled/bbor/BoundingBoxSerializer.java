package com.irtimaled.bbor;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.awt.*;

public class BoundingBoxSerializer {

    public static void serialize(BoundingBox boundingBox, ByteBuf buf) {
        if (boundingBox instanceof BoundingBoxSlimeChunk) {
            serializeSlimeChunk((BoundingBoxSlimeChunk) boundingBox, buf);
        } else if (boundingBox instanceof BoundingBoxVillage) {
            serializeVillage((BoundingBoxVillage) boundingBox, buf);
        } else if (boundingBox instanceof BoundingBoxStructure) {
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

    private static void serializeSlimeChunk(BoundingBoxSlimeChunk boundingBox, ByteBuf buf) {
        ByteBufUtils.writeVarShort(buf, 'C');
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
    /*


        public static void serialize(BoundingBox         boundingBox, StringBuilder sb) {
            if (boundingBox instanceof BoundingBoxSlimeChunk) {
                serializeSlimeChunk((BoundingBoxSlimeChunk) boundingBox, sb);
            } else if (boundingBox instanceof BoundingBoxVillage) {
                serializeVillage((BoundingBoxVillage) boundingBox, sb);
            } else if (boundingBox instanceof BoundingBoxStructure) {
                serializeStructure((BoundingBoxStructure) boundingBox, sb);
            }
        }

        private static void serializeVillage(BoundingBoxVillage boundingBox, StringBuilder sb) {
        sb.append("V/");
        serializeBlockPos(boundingBox.getCenter(), sb);
        sb.append('/');
        sb.append(boundingBox.getRadius());
        sb.append('/');
        sb.append(boundingBox.getSpawnsIronGolems());
        sb.append('/');
        serializeColor(boundingBox.getColor(), sb);
    }

    private static void serializeStructure(BoundingBoxStructure boundingBox, StringBuilder sb) {
        sb.append("S/");
        serializeCuboid(boundingBox, sb);
        sb.append('/');
        serializeColor(boundingBox.getColor(), sb);
    }

    private static void serializeSlimeChunk(BoundingBoxSlimeChunk boundingBox, StringBuilder sb) {
        sb.append("SC/");
        serializeCuboid(boundingBox, sb);
        sb.append('/');
        serializeColor(boundingBox.getColor(), sb);
    }

    private static void serializeColor(Color color, StringBuilder sb) {
        sb.append(color.getRGB());
    }

    private static void serializeCuboid(BoundingBox boundingBox, StringBuilder sb) {
        serializeBlockPos(boundingBox.getMinBlockPos(), sb);
        sb.append('/');
        serializeBlockPos(boundingBox.getMaxBlockPos(), sb);
    }

    private static void serializeBlockPos(BlockPos blockPos, StringBuilder sb) {
        sb.append(blockPos.getX());
        sb.append(',');
        sb.append(blockPos.getY());
        sb.append(',');
        sb.append(blockPos.getZ());
    }*/
}
