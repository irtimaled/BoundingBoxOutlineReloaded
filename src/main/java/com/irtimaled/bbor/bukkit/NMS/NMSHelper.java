package com.irtimaled.bbor.bukkit.NMS;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.bukkit.NMS.api.*;
import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

public class NMSHelper {

    public static final int lowestSupportVersion = 11802;
    public static final int lowestUnSupportVersion = 11902;

    private static final Map<Class<?>, Class<?>> craftClassCache = new HashMap<>();
    private static final Map<Class<?>, Method> craftMethodCache = new HashMap<>();

    private static INMSClass nmsClassCache;
    private static INMSMethod nmsMethodCache;

    private static boolean isInit = false;

    public static boolean init(@NotNull JavaPlugin plugin) {
        if (!isInit) {
            try {
                String packVersion = getPackVersion();
                addCraftGetCache(Chunk.class, "CraftChunk", packVersion);
                addCraftGetCache(World.class, "CraftWorld", packVersion);
                addCraftGetCache(Player.class, "entity.CraftPlayer", packVersion);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return false;
            }

            int version = getVersion();
            while (nmsClassCache == null || nmsMethodCache == null) {
                String packVersion = getPackVersion(version);
                Class<?> nmsClassCacheClass = null;
                Class<?> nmsMethodCacheClass = null;

                try {
                    nmsClassCacheClass = Class.forName("com.irtimaled.bbor.bukkit.NMS.version." + packVersion + "_NMSClass");
                    nmsMethodCacheClass = Class.forName("com.irtimaled.bbor.bukkit.NMS.version." + packVersion + "_NMSMethod");
                } catch (ReflectiveOperationException e) {
                    if (--version < lowestSupportVersion) {
                        Logger.error("NMSHelper init cache fail");
                        return false;
                    }
                }

                if (nmsClassCacheClass != null && nmsMethodCacheClass != null) {
                    try {
                        nmsClassCache = (INMSClass) nmsClassCacheClass.getDeclaredConstructor().newInstance();
                        nmsMethodCache = (INMSMethod) nmsMethodCacheClass.getDeclaredConstructor().newInstance();
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }

            isInit = true;
            Logger.info("NMSHelper init over");
            return true;
        } else {
            return false;
        }
    }

    public static void addCraftGetCache(@NotNull Class<?> bukkitClass, @NotNull String classPath, @Nullable String packVersion) throws ReflectiveOperationException {
        addCraftClass(bukkitClass, classPath, packVersion);
        addCraftMethod(bukkitClass, "getHandle");
    }

    private static void addCraftClass(@NotNull Class<?> bukkitClass, @NotNull String classPath, @Nullable String packVersion) throws ClassNotFoundException {
        if (!isInit && !craftClassCache.containsKey(bukkitClass)) {
            craftClassCache.put(bukkitClass, Class.forName("org.bukkit.craftbukkit." + (packVersion == null ? getPackVersion() : packVersion) + "." + classPath));
        }
    }

    private static void addCraftMethod(@NotNull Class<?> bukkitClass, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws NoSuchMethodException {
        if (!isInit && !craftMethodCache.containsKey(bukkitClass)) {
            Method method = craftClassCache.get(bukkitClass).getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            craftMethodCache.put(bukkitClass, method);
        }
    }

    public static int getVersion() {
        String[] version = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        return Integer.parseInt(version[0]) * 10000 + Integer.parseInt(version[1]) * 100 + ((version.length > 2) ? Integer.parseInt(version[2]) : 0);
    }

    public static int toVersion(String packVersion) {
        packVersion = packVersion.substring(1).replaceAll("R", "");
        String[] version = packVersion.split("_");
        return Integer.parseInt(version[0]) * 10000 + Integer.parseInt(version[1]) * 100 + ((version.length > 2) ? Integer.parseInt(version[2]) : 0);
    }

    @NotNull
    public static String getPackVersion(int version) {
        int mainVersion = version / 10000;
        int minorVersion = (version - mainVersion * 10000) / 100;
        int revisionVersion = (version - mainVersion * 10000 - minorVersion * 100);

        String packVersion = "v" + mainVersion + "_" + minorVersion + "_R" + ((revisionVersion == 0) ? "1" : revisionVersion);
        return packVersion.equals("v1_19_R2") ? "v1_19_R1" : packVersion;
    }

    @NotNull
    public static String getPackVersion() {
        return getPackVersion(getVersion());
    }

    @Nullable
    public static Object getNMSChunk(@NotNull Chunk chunk) {
        return getNMSObject(Chunk.class, NMSClassName.Chunk, chunk);
    }

    @Nullable
    public static Object getNMSWorld(@NotNull World world) {
        return getNMSObject(World.class, NMSClassName.WorldServer, world);
    }

    @Nullable
    public static Object getNMSPlayer(@NotNull Player player) {
        return getNMSObject(Player.class, NMSClassName.EntityPlayer, player);
    }

    @Nullable
    public static <T> Object getNMSObject(@NotNull Class<T> bukkitClass, @NotNull NMSClassName nmsClass, T bukkitObject) {
        try {
            return getNMSClass(nmsClass).cast(craftMethodCache.get(bukkitClass).invoke(craftClassCache.get(bukkitClass).cast(bukkitObject)));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static INMSClass getNmsClassCache() {
        return nmsClassCache;
    }

    public static INMSMethod getNmsMethodCache() {
        return nmsMethodCache;
    }

    @NotNull
    public static Class<?> getNMSClass(@NotNull NMSClassName name) {
        return nmsClassCache.getNMSClass(name);
    }

    @Nullable
    @Contract("_, null -> null")
    public static Object cast(@NotNull NMSClassName name, @Nullable Object object) {
        return nmsClassCache.cast(name, object);
    }

    public static Object chunkGetWorld(Object chunk) {
        return nmsMethodCache.chunkGetWorld(chunk);
    }

    public static Map<?, ?> chunkGetStructureMap(Object chunk) {
        return nmsMethodCache.chunkGetStructureMap(chunk);
    }

    public static Object worldGetStructureFeatureRegistry(Object world) {
        return nmsMethodCache.worldGetStructureFeatureRegistry(world);
    }

    public static Object worldGetResourceKey(Object world) {
        return nmsMethodCache.worldGetResourceKey(world);
    }

    public static Object worldGetWorldData(Object world) {
        return nmsMethodCache.worldGetWorldData(world);
    }

    public static Object worldGetOverloadWorldKey() {
        return nmsMethodCache.worldGetOverloadWorldKey();
    }

    public static long worldGetSeed(Object world) {
        return nmsMethodCache.worldGetSeed(world);
    }

    public static int worldDataGetSpawnX(Object worldData) {
        return nmsMethodCache.worldDataGetSpawnX(worldData);
    }

    public static int worldDataGetSpawnZ(Object worldData) {
        return nmsMethodCache.worldDataGetSpawnZ(worldData);
    }

    public static Optional<?> registryGetOptionalResourceKey(Object registry, Object structure) {
        return nmsMethodCache.registryGetOptionalResourceKey(registry, structure);
    }

    public static Set<Map.Entry<?, ?>> registryGetAllResourceKeySet(Object registry) {
        return nmsMethodCache.registryGetAllResourceKeySet(registry);
    }

    public static Object resourceKeyGetValue(Object resourceKey) {
        return nmsMethodCache.resourceKeyGetValue(resourceKey);
    }

    public static int playerGetEntityID(Object player) {
        return nmsMethodCache.playerGetEntityID(player);
    }

    public static Object playerGetWorld(Object player) {
        return nmsMethodCache.playerGetWorld(player);
    }

    public static NMSMethodConsumer playerGetPacketConsumer(Object player) {
        return nmsMethodCache.playerGetPacketConsumer(player);
    }

    public static NMSClassFunction packetPlayOutCustomPayloadNewFunction() {
        return nmsMethodCache.packetPlayOutCustomPayloadNewFunction();
    }

    public static Object minecraftKeyNew(String name) {
        return nmsMethodCache.minecraftKeyNew(name);
    }

    public static Object packetDataSerializerNew(ByteBuf bytebuf) {
        return nmsMethodCache.packetDataSerializerNew(bytebuf);
    }

    public static void packetDataSerializerWriteLong(Object packetDataSerializer, long value) {
        nmsMethodCache.packetDataSerializerWriteLong(packetDataSerializer, value);
    }

    public static void packetDataSerializerWriteInt(Object packetDataSerializer, int value) {
        nmsMethodCache.packetDataSerializerWriteInt(packetDataSerializer, value);
    }

    public static void packetDataSerializerWriteVarInt(Object packetDataSerializer, int value) {
        nmsMethodCache.packetDataSerializerWriteVarInt(packetDataSerializer, value);
    }

    public static void packetDataSerializerWriteChar(Object packetDataSerializer, char value) {
        nmsMethodCache.packetDataSerializerWriteChar(packetDataSerializer, value);
    }

    public static void packetDataSerializerWriteMinecraftKey(Object packetDataSerializer, Object value){
        nmsMethodCache.packetDataSerializerWriteMinecraftKey(packetDataSerializer, value);
    }

    public static Object structureStartGetBox(Object structureStart) {
        return nmsMethodCache.structureStartGetBox(structureStart);
    }

    public static List<?> structureStartGetPiece(Object structureStart) {
        return nmsMethodCache.structureStartGetPiece(structureStart);
    }

    public static Object structurePieceGetBox(Object structurePiece) {
        return nmsMethodCache.structurePieceGetBox(structurePiece);
    }

    public static int structureBoundingBoxGetMinX(Object structureBoundingBox) {
        return nmsMethodCache.structureBoundingBoxGetMinX(structureBoundingBox);
    }

    public static int structureBoundingBoxGetMinY(Object structureBoundingBox) {
        return nmsMethodCache.structureBoundingBoxGetMinY(structureBoundingBox);
    }

    public static int structureBoundingBoxGetMinZ(Object structureBoundingBox) {
        return nmsMethodCache.structureBoundingBoxGetMinZ(structureBoundingBox);
    }

    public static int structureBoundingBoxGetMaxX(Object structureBoundingBox) {
        return nmsMethodCache.structureBoundingBoxGetMaxX(structureBoundingBox);
    }

    public static int structureBoundingBoxGetMaxY(Object structureBoundingBox) {
        return nmsMethodCache.structureBoundingBoxGetMaxY(structureBoundingBox);
    }

    public static int structureBoundingBoxGetMaxZ(Object structureBoundingBox) {
        return nmsMethodCache.structureBoundingBoxGetMaxZ(structureBoundingBox);
    }
}
