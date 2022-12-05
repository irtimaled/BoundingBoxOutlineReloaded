package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.Logger;
import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.StructureProcessor;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommonInterop {

    public static void chunkLoaded(@NotNull Object chunk) {
        Object world = NMSHelper.chunkGetWorld(chunk);
        DimensionId dimensionId = DimensionId.from(NMSHelper.worldGetResourceKey(world));
        Map<String, Object> structures = new HashMap<>();
        final Object structureFeatureRegistry = NMSHelper.worldGetStructureFeatureRegistry(world);
        for (var es : NMSHelper.chunkGetStructureMap(chunk).entrySet()) {
            final Optional<?> optional = NMSHelper.registryGetOptionalResourceKey(structureFeatureRegistry, es.getKey());
            optional.ifPresent(key -> structures.put("structure:" + NMSHelper.resourceKeyGetValue(key).toString(), es.getValue()));
        }
        if (structures.size() > 0) {
            EventBus.publish(new StructuresLoaded(structures, dimensionId));
        }
    }

    @Deprecated
    public static void loadWorlds(@NotNull Collection<Object> worlds) {
        for (Object world : worlds) {
            loadWorld(world);
            loadWorldStructures(world);
        }
    }

    @Deprecated(forRemoval = true)
    public static void loadWorldStructures(Object world) {
        try {
            final Object structureFeatureRegistry = NMSHelper.worldGetStructureFeatureRegistry(world);
            loadStructuresFromRegistry(structureFeatureRegistry);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void loadServerStructures(Object server) {
        try {
            final Object structureFeatureRegistry = NMSHelper.serverGetStructureFeatureRegistry(server);
            loadStructuresFromRegistry(structureFeatureRegistry);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void loadStructuresFromRegistry(@NotNull Object structureFeatureRegistry) {
        Logger.info("Registering structures: " + Arrays.toString(NMSHelper.registryGetAllResourceKeySet(structureFeatureRegistry).stream().map(entry -> NMSHelper.resourceKeyGetValue(entry.getKey()).toString()).distinct().toArray(String[]::new)));
        for (var entry : NMSHelper.registryGetAllResourceKeySet(structureFeatureRegistry)) {
            final Object value = NMSHelper.resourceKeyGetValue(entry.getKey());
            final BoundingBoxType boundingBoxType = BoundingBoxType.register("structure:" + value);
            StructureProcessor.registerSupportedStructure(boundingBoxType);
        }
    }


    public static void loadWorld(Object world) {
        EventBus.publish(new WorldLoaded(world));
    }

    public static void tick() {
        EventBus.publish(new ServerTick());
    }

    public static void playerLoggedIn(Object player) {
        EventBus.publish(new PlayerLoggedIn(new ServerPlayer(player)));
    }

    public static void playerLoggedOut(@NotNull Object player) {
        EventBus.publish(new PlayerLoggedOut(NMSHelper.playerGetEntityID(player)));
    }

    public static void dataPackReloaded() {
        EventBus.publish(new DataPackReloaded());
    }

    public static void playerSubscribed(@NotNull Object player) {
        EventBus.publish(new PlayerSubscribed(NMSHelper.playerGetEntityID(player), new ServerPlayer(player)));
    }
}
