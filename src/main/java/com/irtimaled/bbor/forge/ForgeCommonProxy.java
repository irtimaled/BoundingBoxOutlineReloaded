package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.PayloadReader;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeCommonProxy {
    void init() {
        registerMessageConsumers();
        new CommonProxy().init();
    }

    void registerMessageConsumers() {
        ForgeNetworkHelper.addServerConsumer(SubscribeToServer.NAME, this::playerSubscribed);
    }

    private void playerSubscribed(PayloadReader ignored, ServerPlayerEntity serverPlayerEntity) {
        CommonInterop.playerSubscribed(serverPlayerEntity);
    }

    @SubscribeEvent
    public void worldEvent(WorldEvent.Load event) {
        IWorld world = event.getWorld();
        if (world instanceof ServerWorld) {
            CommonInterop.loadWorld((ServerWorld) world);
        }
    }

    @SubscribeEvent
    public void chunkEvent(ChunkEvent.Load event) {
        IWorld world = event.getWorld();
        IChunk chunk = event.getChunk();
        if (world instanceof ServerWorld && chunk instanceof Chunk) {
            CommonInterop.chunkLoaded((Chunk) chunk);
        }
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent evt) {
        PlayerEntity player = evt.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            CommonInterop.playerLoggedIn((ServerPlayerEntity) player);
        }
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent evt) {
        PlayerEntity player = evt.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            CommonInterop.playerLoggedOut((ServerPlayerEntity) player);
        }
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            CommonInterop.tick();
    }
}
