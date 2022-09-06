package com.irtimaled.bbor.bukkit.NMS.version;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.bukkit.NMS.api.*;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public abstract class BaseNMSMethod implements INMSMethod {

    public BaseNMSMethod() throws ReflectiveOperationException {

    }

    protected final Map<String, NMSMethodInvoker> methodInvokerCache = new HashMap<>();
    protected final Map<String, NMSClassFunction> classFunctionCache = new HashMap<>();
    protected final Map<String, Field> fieldCache = new HashMap<>();

    protected void addFieldCache(String name, NMSClassName className, String fieldName) throws NoSuchFieldException {
        Class<?> clazz = NMSHelper.getNMSClass(className);
        Field field = null;

        do {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        } while (clazz.getSuperclass() != null);

        if (field != null) {
            field.setAccessible(true);
            fieldCache.put(name, field);
        } else {
            throw new NoSuchFieldException(fieldName);
        }

    }

    protected void addMethodCache(String name, NMSMethodDescribe describe) throws ReflectiveOperationException {
        methodInvokerCache.put(name, new NMSMethodInvoker(describe));
    }

    protected void addClassFunctionCache(String name, NMSClassName className, Class<?>... parameterTypes) throws NoSuchMethodException {
        classFunctionCache.put(name, new NMSClassFunction(className, parameterTypes));
    }

    @Nullable
    protected Object getField(String name, NMSClassName className, Object obj) {
        try {
            return fieldCache.get(name).get(NMSHelper.getNMSClass(className).cast(obj));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    protected Object invokeMethod(String name, Object obj, Object... parameters) {
        return methodInvokerCache.get(name).invoke(obj, parameters);
    }

    @Nullable
    protected Object applyFunction(String name, Object... parameters) {
        return classFunctionCache.get(name).apply(parameters);
    }

    @Override
    public Object chunkGetWorld(Object chunk) {
        return getField("chunkGetWorld", NMSClassName.Chunk, chunk);
    }

    @Override
    public Map<?, ?> chunkGetStructureMap(Object chunk) {
        return (Map<?, ?>) invokeMethod("chunkGetStructureMap", chunk);
    }

    @Override
    public Object worldGetStructureFeatureRegistry(Object world) {
        return invokeMethod("worldGetStructureFeatureRegistry2", invokeMethod("worldGetStructureFeatureRegistry1", world), getField("worldGetStructureFeatureRegistry3", NMSClassName.IRegistry, null));
    }

    @Override
    public Object worldGetResourceKey(Object world) {
        return invokeMethod("worldGetResourceKey", world);
    }

    @Override
    public Object worldGetWorldData(Object world) {
        return getField("worldGetWorldData", NMSClassName.WorldServer, world);
    }

    @Override
    public Object worldGetOverloadWorldKey() {
        return getField("worldGetOverloadWorldKey", NMSClassName.World, null);
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
        return getField("playerGetWorld", NMSClassName.EntityPlayer, player);
    }

    @Override
    public NMSMethodConsumer playerGetPacketConsumer(Object player) {
        try {
            return new NMSMethodConsumer(NMSMethodDescribe.of(NMSClassName.PlayerConnection, "a", NMSHelper.getNMSClass(NMSClassName.Packet)), getField("playerGetPacketConsumer", NMSClassName.EntityPlayer, player));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public NMSClassFunction packetPlayOutCustomPayloadNewFunction() {
        try {
            return new NMSClassFunction(NMSClassName.PacketPlayOutCustomPayload, NMSHelper.getNMSClass(NMSClassName.MinecraftKey), NMSHelper.getNMSClass(NMSClassName.PacketPlayOutCustomPayload));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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
}
