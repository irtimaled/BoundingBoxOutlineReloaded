package com.irtimaled.bbor.bukkit.NMS.version;

import com.irtimaled.bbor.bukkit.NMS.api.INMSMethod;
import com.irtimaled.bbor.bukkit.NMS.api.NMSClassFunction;
import com.irtimaled.bbor.bukkit.NMS.api.NMSFieldDescribe;
import com.irtimaled.bbor.bukkit.NMS.api.NMSFieldGetter;
import com.irtimaled.bbor.bukkit.NMS.api.NMSFunctionDescribe;
import com.irtimaled.bbor.bukkit.NMS.api.NMSMethodConsumer;
import com.irtimaled.bbor.bukkit.NMS.api.NMSMethodDescribe;
import com.irtimaled.bbor.bukkit.NMS.api.NMSMethodInvoker;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class BaseNMSMethod implements INMSMethod {

    public BaseNMSMethod() throws ReflectiveOperationException {

    }

    protected final Map<String, NMSMethodInvoker> methodInvokerCache = new HashMap<>();
    protected final Map<String, NMSClassFunction> classFunctionCache = new HashMap<>();
    protected final Map<String, NMSMethodConsumer> methodConsumerCache = new HashMap<>();
    protected final Map<String, NMSFieldGetter> fieldCache = new HashMap<>();

    protected void addFieldCache(String name, NMSFieldDescribe describe) throws NoSuchFieldException {
        fieldCache.put(name, new NMSFieldGetter(describe));
    }

    protected void addMethodCache(String name, NMSMethodDescribe describe) throws ReflectiveOperationException {
        methodInvokerCache.put(name, new NMSMethodInvoker(describe));
    }

    protected void addClassFunctionCache(String name, NMSFunctionDescribe describe) throws NoSuchMethodException {
        classFunctionCache.put(name, new NMSClassFunction(describe));
    }

    protected void addConsumerCache(String name, NMSMethodDescribe describe) throws NoSuchMethodException {
        methodConsumerCache.put(name, new NMSMethodConsumer(describe, null));
    }

    @Nullable
    protected Object getField(String name, Object obj) {
        return fieldCache.get(name).get(obj);
    }

    @Nullable
    protected Object invokeMethod(String name, Object obj, Object... parameters) {
        return methodInvokerCache.get(name).invoke(obj, parameters);
    }

    @Nullable
    protected Object applyFunction(String name, Object... parameters) {
        return classFunctionCache.get(name).apply(parameters);
    }

    @Nullable
    protected NMSClassFunction getNewFunction(String name) {
        return classFunctionCache.get(name).clone();
    }

    @Nullable
    protected NMSMethodConsumer getNewConsumer(String name, Object obj) {
        return methodConsumerCache.get(name).toNew(obj);
    }

    @Override
    public Object chunkGetWorld(Object chunk) {
        return getField("chunkGetWorld", chunk);
    }

    @Override
    public Map<?, ?> chunkGetStructureMap(Object chunk) {
        return (Map<?, ?>) invokeMethod("chunkGetStructureMap", chunk);
    }

    @Override
    public Object worldGetStructureFeatureRegistry(Object world) {
        return invokeMethod("worldGetStructureFeatureRegistry2", invokeMethod("worldGetStructureFeatureRegistry1", world), getField("worldGetStructureFeatureRegistry3", null));
    }

    @Override
    public Object worldGetResourceKey(Object world) {
        return invokeMethod("worldGetResourceKey", world);
    }

    @Override
    public Object worldGetWorldData(Object world) {
        return getField("worldGetWorldData", world);
    }

    @Override
    public Object worldGetOverloadWorldKey() {
        return getField("worldGetOverloadWorldKey", null);
    }

    @Override
    public long worldGetSeed(Object world) {
        return (long) invokeMethod("worldGetSeed", world);
    }

    @Override
    public int worldDataGetSpawnX(Object worldData) {
        return (int) invokeMethod("worldDataGetSpawnX", worldData);
    }

    @Override
    public int worldDataGetSpawnZ(Object worldData) {
        return (int) invokeMethod("worldDataGetSpawnZ", worldData);
    }

    @Override
    public Optional<?> registryGetOptionalResourceKey(Object registry, Object structure) {
        return (Optional<?>) invokeMethod("registryGetOptionalResourceKey", registry, structure);
    }

    @Override
    public Set<Map.Entry<?, ?>> registryGetAllResourceKeySet(Object registry) {
        return (Set<Map.Entry<?, ?>>) invokeMethod("registryGetAllResourceKeySet", registry);
    }

    @Override
    public Object resourceKeyGetValue(Object resourceKey) {
        return invokeMethod("resourceKeyGetValue", resourceKey);
    }

    @Override
    public int playerGetEntityID(Object player) {
        return (int) invokeMethod("playerGetEntityID", player);
    }

    @Override
    public Object playerGetWorld(Object player) {
        return getField("playerGetWorld", player);
    }

    @Override
    public NMSMethodConsumer playerGetPacketConsumer(Object player) {
        return getNewConsumer("playerGetPacketConsumer2", getField("playerGetPacketConsumer1", player));
    }

    @Override
    public NMSClassFunction packetPlayOutCustomPayloadNewFunction() {
        return getNewFunction("packetPlayOutCustomPayloadNewFunction");
    }

    @Override
    public Object minecraftKeyNew(String name) {
        return applyFunction("minecraftKeyNew", name);
    }

    @Override
    public Object packetDataSerializerNew(ByteBuf bytebuf) {
        return applyFunction("packetDataSerializerNew", bytebuf);
    }

    @Override
    public void packetDataSerializerWriteLong(Object packetDataSerializer, long value) {
        invokeMethod("packetDataSerializerWriteLong", packetDataSerializer, value);
    }

    @Override
    public void packetDataSerializerWriteInt(Object packetDataSerializer, int value) {
        invokeMethod("packetDataSerializerWriteInt", packetDataSerializer, value);
    }

    @Override
    public void packetDataSerializerWriteVarInt(Object packetDataSerializer, int value) {
        invokeMethod("packetDataSerializerWriteVarInt", packetDataSerializer, value);
    }

    @Override
    public void packetDataSerializerWriteChar(Object packetDataSerializer, char value) {
        invokeMethod("packetDataSerializerWriteChar", packetDataSerializer, value);
    }

    @Override
    public void packetDataSerializerWriteMinecraftKey(Object packetDataSerializer, Object value) {
        invokeMethod("packetDataSerializerWriteMinecraftKey", packetDataSerializer, value);
    }

    @Override
    public void packetDataSerializerWriteString(Object packetDataSerializer, String value) {
        invokeMethod("packetDataSerializerWriteString", packetDataSerializer, value);
    }

    @Override
    public Object structureStartGetBox(Object structureStart) {
        return invokeMethod("structureStartGetBox", structureStart);
    }

    @Override
    public List<?> structureStartGetPiece(Object structureStart) {
        return (List<?>) invokeMethod("structureStartGetPiece", structureStart);
    }

    @Override
    public Object structurePieceGetBox(Object structurePiece) {
        return invokeMethod("structurePieceGetBox", structurePiece);
    }

    @Override
    public int structureBoundingBoxGetMinX(Object structureBoundingBox) {
        return (int) invokeMethod("structureBoundingBoxGetMinX", structureBoundingBox);
    }

    @Override
    public int structureBoundingBoxGetMinY(Object structureBoundingBox) {
        return (int) invokeMethod("structureBoundingBoxGetMinY", structureBoundingBox);
    }

    @Override
    public int structureBoundingBoxGetMinZ(Object structureBoundingBox) {
        return (int) invokeMethod("structureBoundingBoxGetMinZ", structureBoundingBox);
    }

    @Override
    public int structureBoundingBoxGetMaxX(Object structureBoundingBox) {
        return (int) invokeMethod("structureBoundingBoxGetMaxX", structureBoundingBox);
    }

    @Override
    public int structureBoundingBoxGetMaxY(Object structureBoundingBox) {
        return (int) invokeMethod("structureBoundingBoxGetMaxY", structureBoundingBox);
    }

    @Override
    public int structureBoundingBoxGetMaxZ(Object structureBoundingBox) {
        return (int) invokeMethod("structureBoundingBoxGetMaxZ", structureBoundingBox);
    }

    @Override
    public Object serverGetStructureFeatureRegistry(Object world) {
        return invokeMethod("serverGetStructureFeatureRegistry2", invokeMethod("serverGetStructureFeatureRegistry1", world), getField("serverGetStructureFeatureRegistry3", null));
    }
}
