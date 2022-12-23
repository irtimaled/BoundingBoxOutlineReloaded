package com.irtimaled.bbor.client.interop;

import com.irtimaled.bbor.common.EventBus;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public class ClientWorldUpdateTracker {

    public static void reset() {
        EventBus.publish(new WorldResetEvent());
    }

    public static void onChunkLoad(int x, int z) {
        EventBus.publish(new ChunkLoadEvent(x, z));
    }

    public static void onChunkUnload(int x, int z) {
        EventBus.publish(new ChunkUnloadEvent(x, z));
    }

    public static void onLightingUpdate(int x, int z) {
        EventBus.publish(new LightingUpdateEvent(x, z));
    }

    public static void onBlockChange(int x, int y, int z, BlockState oldState, BlockState newState) {
        EventBus.publish(new BlockChangeEvent(x, y, z, oldState, newState));
    }

    public static void addBlockEntity(BlockEntity blockEntity) {
        EventBus.publish(new BlockEntityAddEvent(blockEntity));
    }

    public static void updateBlockEntity(BlockEntity blockEntity) {
        EventBus.publish(new BlockEntityUpdateEvent(blockEntity));
    }

    public static void removeBlockEntity(int x, int y, int z) {
        EventBus.publish(new BlockEntityRemoveEvent(x, y, z));
    }

    public record WorldResetEvent() {
    }

    public record ChunkLoadEvent(int x, int z) {
    }

    public record ChunkUnloadEvent(int x, int z) {
    }

    public record LightingUpdateEvent(int x, int z) {
    }

    public record BlockChangeEvent(int x, int y, int z, BlockState oldState, BlockState newState) {
    }

    public record BlockEntityAddEvent(BlockEntity blockEntity) {
    }

    public record BlockEntityUpdateEvent(BlockEntity blockEntity) {
    }

    public record BlockEntityRemoveEvent(int x, int y, int z) {
    }

}
