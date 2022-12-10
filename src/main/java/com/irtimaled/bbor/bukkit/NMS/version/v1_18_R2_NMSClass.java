package com.irtimaled.bbor.bukkit.NMS.version;

import com.irtimaled.bbor.bukkit.NMS.api.NMSClassName;

public class v1_18_R2_NMSClass extends BaseNMSClass {

    public v1_18_R2_NMSClass() throws ClassNotFoundException {
        addClassCache(NMSClassName.Chunk, "net.minecraft.world.level.chunk.Chunk");
        addClassCache(NMSClassName.EntityPlayer, "net.minecraft.server.level.EntityPlayer");
        addClassCache(NMSClassName.IRegistry, "net.minecraft.core.IRegistry");
        addClassCache(NMSClassName.IRegistryCustom, "net.minecraft.core.IRegistryCustom");
        addClassCache(NMSClassName.MinecraftServer, "net.minecraft.server.MinecraftServer");
        addClassCache(NMSClassName.MinecraftKey, "net.minecraft.resources.MinecraftKey");
        addClassCache(NMSClassName.Packet, "net.minecraft.network.protocol.Packet");
        addClassCache(NMSClassName.PacketDataSerializer, "net.minecraft.network.PacketDataSerializer");
        addClassCache(NMSClassName.PacketPlayOutCustomPayload, "net.minecraft.network.protocol.game.PacketPlayOutCustomPayload");
        addClassCache(NMSClassName.PlayerConnection, "net.minecraft.server.network.PlayerConnection");
        addClassCache(NMSClassName.ResourceKey, "net.minecraft.resources.ResourceKey");
        addClassCache(NMSClassName.Structure, "net.minecraft.world.level.levelgen.feature.StructureFeature");
        addClassCache(NMSClassName.StructureBoundingBox, "net.minecraft.world.level.levelgen.structure.StructureBoundingBox");
        addClassCache(NMSClassName.StructurePiece, "net.minecraft.world.level.levelgen.structure.StructurePiece");
        addClassCache(NMSClassName.StructureStart, "net.minecraft.world.level.levelgen.structure.StructureStart");
        addClassCache(NMSClassName.World, "net.minecraft.world.level.World");
        addClassCache(NMSClassName.WorldData, "net.minecraft.world.level.storage.WorldData");
        addClassCache(NMSClassName.WorldServer, "net.minecraft.server.level.WorldServer");
    }
}
