package com.irtimaled.bbor.bukkit.NMS.api;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface INMSMethod {

    Object chunkGetWorld(Object chunk);

    Map<?, ?> chunkGetStructureMap(Object chunk);

    Object worldGetStructureFeatureRegistry(Object world);

    Object worldGetResourceKey(Object world);

    Object worldGetWorldData(Object world);

    Object worldGetOverloadWorldKey();

    long worldGetSeed(Object world);

    int worldDataGetSpawnX(Object worldData);

    int worldDataGetSpawnZ(Object worldData);

    Optional<?> registryGetOptionalResourceKey(Object registry, Object structure);

    Set<Map.Entry<?, ?>> registryGetAllResourceKeySet(Object registry);

    Object resourceKeyGetValue(Object resourceKey);

    int playerGetEntityID(Object player);

    Object playerGetWorld(Object player);

    NMSMethodConsumer playerGetPacketConsumer(Object player);

    NMSClassFunction packetPlayOutCustomPayloadNewFunction();

    Object minecraftKeyNew(String name);

    Object packetDataSerializerNew(ByteBuf bytebuf);

    void packetDataSerializerWriteLong(Object packetDataSerializer, long value);

    void packetDataSerializerWriteInt(Object packetDataSerializer, int value);

    void packetDataSerializerWriteVarInt(Object packetDataSerializer, int value);

    void packetDataSerializerWriteChar(Object packetDataSerializer, char value);

    void packetDataSerializerWriteMinecraftKey(Object packetDataSerializer, Object value);

    void packetDataSerializerWriteString(Object packetDataSerializer, String value);

    Object structureStartGetBox(Object structureStart);

    List<?> structureStartGetPiece(Object structureStart);

    Object structurePieceGetBox(Object structurePiece);

    int structureBoundingBoxGetMinX(Object structureBoundingBox);

    int structureBoundingBoxGetMinY(Object structureBoundingBox);

    int structureBoundingBoxGetMinZ(Object structureBoundingBox);

    int structureBoundingBoxGetMaxX(Object structureBoundingBox);

    int structureBoundingBoxGetMaxY(Object structureBoundingBox);

    int structureBoundingBoxGetMaxZ(Object structureBoundingBox);

    Object serverGetStructureFeatureRegistry(Object server);
}
