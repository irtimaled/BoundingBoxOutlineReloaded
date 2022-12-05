package com.irtimaled.bbor.bukkit.NMS.version;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.bukkit.NMS.api.NMSClassName;
import com.irtimaled.bbor.bukkit.NMS.api.NMSFieldDescribe;
import com.irtimaled.bbor.bukkit.NMS.api.NMSFunctionDescribe;
import com.irtimaled.bbor.bukkit.NMS.api.NMSMethodDescribe;
import io.netty.buffer.ByteBuf;

public class v1_18_R2_NMSMethod extends BaseNMSMethod {

    public v1_18_R2_NMSMethod() throws ReflectiveOperationException {
        addFieldCache("chunkGetWorld", NMSFieldDescribe.of(NMSClassName.Chunk, "q"));
        addMethodCache("chunkGetStructureMap", NMSMethodDescribe.of(NMSClassName.Chunk, "g"));
        addMethodCache("worldGetStructureFeatureRegistry1", NMSMethodDescribe.of(NMSClassName.WorldServer, "s"));
        addMethodCache("worldGetStructureFeatureRegistry2", NMSMethodDescribe.of(NMSClassName.IRegistryCustom, "b", NMSHelper.getNMSClass(NMSClassName.ResourceKey)));
        addFieldCache("worldGetStructureFeatureRegistry3", NMSFieldDescribe.of(NMSClassName.IRegistry, "aL"));
        addMethodCache("worldGetResourceKey", NMSMethodDescribe.of(NMSClassName.WorldServer, "aa"));
        addMethodCache("worldGetWorldData", NMSMethodDescribe.of(NMSClassName.WorldServer, "n_"));
        addFieldCache("worldGetOverloadWorldKey", NMSFieldDescribe.of(NMSClassName.World, "e"));
        addMethodCache("worldGetSeed", NMSMethodDescribe.of(NMSClassName.WorldServer, "D"));
        addMethodCache("worldDataGetSpawnX", NMSMethodDescribe.of(NMSClassName.WorldData, "a"));
        addMethodCache("worldDataGetSpawnZ", NMSMethodDescribe.of(NMSClassName.WorldData, "c"));
        addMethodCache("registryGetOptionalResourceKey", NMSMethodDescribe.of(NMSClassName.IRegistry, "c", Object.class));
        addMethodCache("registryGetAllResourceKeySet", NMSMethodDescribe.of(NMSClassName.IRegistry, "e"));
        addMethodCache("resourceKeyGetValue", NMSMethodDescribe.of(NMSClassName.ResourceKey, "a"));
        addMethodCache("playerGetEntityID", NMSMethodDescribe.of(NMSClassName.EntityPlayer, "ae"));
        addFieldCache("playerGetWorld", NMSFieldDescribe.of(NMSClassName.EntityPlayer, "s"));
        addFieldCache("playerGetPacketConsumer1", NMSFieldDescribe.of(NMSClassName.EntityPlayer, "b"));
        addConsumerCache("playerGetPacketConsumer2", NMSMethodDescribe.of(NMSClassName.PlayerConnection, "a", NMSHelper.getNMSClass(NMSClassName.Packet)));
        addClassFunctionCache("packetPlayOutCustomPayloadNewFunction", NMSFunctionDescribe.of(NMSClassName.PacketPlayOutCustomPayload, NMSHelper.getNMSClass(NMSClassName.MinecraftKey), NMSHelper.getNMSClass(NMSClassName.PacketDataSerializer)));
        addClassFunctionCache("minecraftKeyNew", NMSFunctionDescribe.of(NMSClassName.MinecraftKey, String.class));
        addClassFunctionCache("packetDataSerializerNew", NMSFunctionDescribe.of(NMSClassName.PacketDataSerializer, ByteBuf.class));
        addMethodCache("packetDataSerializerWriteLong", NMSMethodDescribe.of(NMSClassName.PacketDataSerializer, "writeLong", long.class));
        addMethodCache("packetDataSerializerWriteInt", NMSMethodDescribe.of(NMSClassName.PacketDataSerializer, "writeInt", int.class));
        addMethodCache("packetDataSerializerWriteVarInt", NMSMethodDescribe.of(NMSClassName.PacketDataSerializer, "d", int.class));
        addMethodCache("packetDataSerializerWriteChar", NMSMethodDescribe.of(NMSClassName.PacketDataSerializer, "writeChar", int.class));
        addMethodCache("packetDataSerializerWriteMinecraftKey", NMSMethodDescribe.of(NMSClassName.PacketDataSerializer, "a", NMSHelper.getNMSClass(NMSClassName.MinecraftKey)));
        addMethodCache("packetDataSerializerWriteString", NMSMethodDescribe.of(NMSClassName.PacketDataSerializer, "a", String.class));
        addMethodCache("structureStartGetBox", NMSMethodDescribe.of(NMSClassName.StructureStart, "a"));
        addMethodCache("structureStartGetPiece", NMSMethodDescribe.of(NMSClassName.StructureStart, "i"));
        addMethodCache("structurePieceGetBox", NMSMethodDescribe.of(NMSClassName.StructurePiece, "f"));
        addMethodCache("structureBoundingBoxGetMinX", NMSMethodDescribe.of(NMSClassName.StructureBoundingBox, "g"));
        addMethodCache("structureBoundingBoxGetMinY", NMSMethodDescribe.of(NMSClassName.StructureBoundingBox, "h"));
        addMethodCache("structureBoundingBoxGetMinZ", NMSMethodDescribe.of(NMSClassName.StructureBoundingBox, "i"));
        addMethodCache("structureBoundingBoxGetMaxX", NMSMethodDescribe.of(NMSClassName.StructureBoundingBox, "j"));
        addMethodCache("structureBoundingBoxGetMaxY", NMSMethodDescribe.of(NMSClassName.StructureBoundingBox, "k"));
        addMethodCache("structureBoundingBoxGetMaxZ", NMSMethodDescribe.of(NMSClassName.StructureBoundingBox, "l"));
        addMethodCache("serverGetStructureFeatureRegistry1", NMSMethodDescribe.of(NMSClassName.MinecraftServer, "aU"));
        addMethodCache("serverGetStructureFeatureRegistry2", NMSMethodDescribe.of(NMSClassName.IRegistryCustom, "b", NMSHelper.getNMSClass(NMSClassName.ResourceKey)));
        addFieldCache("serverGetStructureFeatureRegistry3", NMSFieldDescribe.of(NMSClassName.IRegistry, "aL"));
    }

    @Override
    public Object worldGetWorldData(Object world) {
        return invokeMethod("worldGetWorldData", world);
    }
}
