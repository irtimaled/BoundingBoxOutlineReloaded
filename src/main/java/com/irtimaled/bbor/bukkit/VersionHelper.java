package com.irtimaled.bbor.bukkit;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class VersionHelper {

    public static final int lowestSupportVersion = 11800;
    public static final int lowestUnSupportVersion = 11900;

    private static final Map<Class<?>, Class<?>> craftClassCache = new HashMap<>();
    private static final Map<Class<?>, Method> craftMethodCache = new HashMap<>();

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

            isInit = true;
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
        if (version.length < 3) {
            return Integer.parseInt(version[0]) * 10000 + Integer.parseInt(version[1]) * 100;
        } else {
            return Integer.parseInt(version[0]) * 10000 + Integer.parseInt(version[1]) * 100 + Integer.parseInt(version[2]);

        }
    }

    @NotNull
    public static String getPackVersion() {
        String[] versionPath = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String version = "v" + versionPath[0] + "_" + versionPath[1] + "_R" + (versionPath.length > 2 ? versionPath[2] : "1");
        return version.equals("v1_19_R2") ? "v1_19_R1" : version;
    }

    @Nullable
    public static net.minecraft.world.level.chunk.Chunk getNMSChunk(@NotNull Chunk chunk) {
        return getNMSObject(Chunk.class, net.minecraft.world.level.chunk.Chunk.class, chunk);
    }

    @Nullable
    public static WorldServer getNMSWorld(@NotNull World world) {
        return getNMSObject(World.class, WorldServer.class, world);
    }

    @Nullable
    public static EntityPlayer getNMSPlayer(@NotNull Player player) {
        return getNMSObject(Player.class, EntityPlayer.class, player);
    }

    @Nullable
    public static <T, E> E getNMSObject(@NotNull Class<T> bukkitClass, @NotNull Class<E> nmsClass, T bukkitObject) {
        try {
            return nmsClass.cast(craftMethodCache.get(bukkitClass).invoke(craftClassCache.get(bukkitClass).cast(bukkitObject)));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
