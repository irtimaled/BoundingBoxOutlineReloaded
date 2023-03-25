package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.StructureProcessor;
import com.irtimaled.bbor.common.events.DataPackReloaded;
import com.irtimaled.bbor.common.events.PlayerLoggedIn;
import com.irtimaled.bbor.common.events.PlayerLoggedOut;
import com.irtimaled.bbor.common.events.PlayerSubscribed;
import com.irtimaled.bbor.common.events.ServerTick;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import com.irtimaled.bbor.common.events.WorldLoaded;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import com.irtimaled.bbor.mixin.access.IServerPlayNetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.structure.Structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class CommonInterop {
    public static void chunkLoaded(WorldChunk chunk) {
        DimensionId dimensionId = DimensionId.from(chunk.getWorld().getRegistryKey());
        Map<String, StructureStart> structures = new HashMap<>();
        final Registry<Structure> structureFeatureRegistry = chunk.getWorld().getRegistryManager().get(RegistryKeys.STRUCTURE);
        for (var es : chunk.getStructureStarts().entrySet()) {
            final Optional<RegistryKey<Structure>> optional = structureFeatureRegistry.getKey(es.getKey());
            optional.ifPresent(key -> structures.put(key.getValue().toString(), es.getValue()));
        }
        if (structures.size() > 0) EventBus.publish(new StructuresLoaded(structures, dimensionId));
    }

    public static void loadWorlds(Collection<ServerWorld> worlds) {
        for (ServerWorld world : worlds) {
            loadWorld(world);
        }
    }

    public static void loadServerStructures(MinecraftServer server) {
        try {
            final Registry<Structure> structureFeatureRegistry = server.getRegistryManager().get(RegistryKeys.STRUCTURE);
            loadStructuresFromRegistry(structureFeatureRegistry);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void loadStructuresFromRegistry(Registry<Structure> structureFeatureRegistry) {
        System.out.println("Registring structures: " + Arrays.toString(structureFeatureRegistry.getEntrySet().stream().map(entry -> entry.getKey().getValue().toString()).distinct().toArray(String[]::new)));
        for (var entry : structureFeatureRegistry.getEntrySet()) {
            final Identifier value = entry.getKey().getValue();
            final BoundingBoxType boundingBoxType = BoundingBoxType.register("structure:" + value);
            StructureProcessor.registerSupportedStructure(boundingBoxType);
            BoundingBoxTypeHelper.registerType(boundingBoxType, ConfigManager.structureShouldRender(value.toString()), ConfigManager.structureColor(value.toString()));
        }
    }

    public static void loadStructuresInitial() {
        final List<RegistryEntry.Reference<Structure>> references = BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(RegistryKeys.STRUCTURE).streamEntries().toList();
        System.out.println("Registring structures: " + Arrays.toString(references.stream().map(entry -> entry.getKey().get().getValue().toString()).distinct().toArray(String[]::new)));
        for (var entry : references) {
            final Identifier value = entry.getKey().get().getValue();
            final BoundingBoxType boundingBoxType = BoundingBoxType.register("structure:" + value);
            StructureProcessor.registerSupportedStructure(boundingBoxType);
            BoundingBoxTypeHelper.registerType(boundingBoxType, ConfigManager.structureShouldRender(value.toString()), ConfigManager.structureColor(value.toString()));
        }
    }

    public static void loadWorld(ServerWorld world) {
        EventBus.publish(new WorldLoaded(world));
    }

    public static void tick() {
        EventBus.publish(new ServerTick());
    }

    public static void playerLoggedIn(ServerPlayerEntity player) {
        ServerPlayNetworkHandler connection = player.networkHandler;
        if (connection == null) return;

        ClientConnection networkManager = ((IServerPlayNetworkHandler) connection).getConnection();
        if (networkManager.isLocal()) return;

        EventBus.publish(new PlayerLoggedIn(new ServerPlayer(player)));
    }

    public static void playerLoggedOut(ServerPlayerEntity player) {
        EventBus.publish(new PlayerLoggedOut(player.getId()));
    }

    public static void playerSubscribed(ServerPlayerEntity player) {
        EventBus.publish(new PlayerSubscribed(player.getId(), new ServerPlayer(player)));
    }

    public static void dataPackReloaded() {
        EventBus.publish(new DataPackReloaded());
    }

    public static <T extends AbstractBoundingBox> AbstractRenderer<T> registerRenderer(Class<? extends T> type, Supplier<AbstractRenderer<T>> renderer) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return ClientRenderer.registerRenderer(type, renderer);
        }
        return null;
    }
}
