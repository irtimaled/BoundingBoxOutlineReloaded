package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.network.NetworkEvent;

public class ForgeCommonProxy {
    void init() {
        new CommonProxy().init();
        ForgeNetworkHelper.addConsumer(SubscribeToServer.NAME, this::playerSubscribed);
    }

    private <T extends NetworkEvent> void playerSubscribed(final T packet) {
        CommonInterop.playerSubscribed(packet.getSource().get().getSender());
    }

    @SubscribeEvent
    public void worldEvent(WorldEvent.Load event) {
        IWorld world = event.getWorld();
        if (world instanceof WorldServer) {
            CommonInterop.loadWorld((WorldServer) world);
        }
    }

    @SubscribeEvent
    public void chunkEvent(ChunkEvent.Load event) {
        IChunk chunk = event.getChunk();
        if (chunk instanceof Chunk) {
            CommonInterop.chunkLoaded((Chunk) chunk);
        }
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent evt) {
        EntityPlayer player = evt.getPlayer();
        if (player instanceof EntityPlayerMP) {
            CommonInterop.playerLoggedIn((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent evt) {
        EntityPlayer player = evt.getPlayer();
        if (player instanceof EntityPlayerMP) {
            CommonInterop.playerLoggedOut((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public void worldTickEvent(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END &&
                event.world instanceof WorldServer) {
            CommonInterop.worldTick((WorldServer) event.world);
        }
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            CommonInterop.tick();
    }

    @SubscribeEvent
    public void blockBreakEvent(BlockEvent.BreakEvent event) {
        Block block = event.getState().getBlock();
        IWorld world = event.getWorld();
        if (world instanceof World) {
            CommonInterop.tryHarvestBlock(block, event.getPos(), (World) world);
        }
    }
}
