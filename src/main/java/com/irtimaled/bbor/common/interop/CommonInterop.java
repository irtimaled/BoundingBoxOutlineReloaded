package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.StructureProcessor;
import com.irtimaled.bbor.common.events.PlayerLoggedIn;
import com.irtimaled.bbor.common.events.PlayerLoggedOut;
import com.irtimaled.bbor.common.events.PlayerSubscribed;
import com.irtimaled.bbor.common.events.ServerTick;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import com.irtimaled.bbor.common.events.WorldLoaded;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommonInterop {
    public static void chunkLoaded(Chunk chunk) {
        DimensionId dimensionId = DimensionId.from(chunk.q.ab());
        Map<String, StructureStart> structures = new HashMap<>();
        final IRegistry<Structure> structureFeatureRegistry = chunk.q.s().b(IRegistry.aN);
        for (var es : chunk.g().entrySet()) {
            final Optional<ResourceKey<Structure>> optional = structureFeatureRegistry.c(es.getKey());
            optional.ifPresent(key -> structures.put("structure:" + key.a().toString(), es.getValue()));
        }
        if (structures.size() > 0) {
            EventBus.publish(new StructuresLoaded(structures, dimensionId));
        }
    }

    public static void loadWorlds(Collection<WorldServer> worlds) {
        for (WorldServer world : worlds) {
            loadWorld(world);
            loadWorldStructures(world);
        }
    }

    public static void loadWorldStructures(WorldServer world) {
        try {
            final IRegistry<Structure> structureFeatureRegistry = world.s().b(IRegistry.aN);
            loadStructuresFromRegistry(structureFeatureRegistry);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void loadStructuresFromRegistry(IRegistry<Structure> structureFeatureRegistry) {
        // System.out.println("Registring structures: " + Arrays.toString(structureFeatureRegistry.getEntrySet().stream().map(entry -> entry.getKey().getValue().toString()).distinct().toArray(String[]::new)));
        for (var entry : structureFeatureRegistry.f()) {
            final MinecraftKey value = entry.getKey().a();
            final BoundingBoxType boundingBoxType = BoundingBoxType.register("structure:" + value);
            StructureProcessor.registerSupportedStructure(boundingBoxType);
            //  BoundingBoxTypeHelper.registerType(boundingBoxType, ConfigManager.structureShouldRender(value.toString()), ConfigManager.structureColor(value.toString()));
        }
    }


    public static void loadWorld(WorldServer world) {
        EventBus.publish(new WorldLoaded(world));
    }

    public static void tick() {
        EventBus.publish(new ServerTick());
    }

    public static void playerLoggedIn(EntityPlayer player) {
        EventBus.publish(new PlayerLoggedIn(new ServerPlayer(player)));
    }

    public static void playerLoggedOut(EntityPlayer player) {
        EventBus.publish(new PlayerLoggedOut(player.ae()));
    }

    public static void playerSubscribed(EntityPlayer player) {
        EventBus.publish(new PlayerSubscribed(player.ae(), new ServerPlayer(player)));
    }
}
