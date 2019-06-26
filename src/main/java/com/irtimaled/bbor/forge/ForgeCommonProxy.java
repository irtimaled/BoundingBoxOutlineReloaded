package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ForgeCommonProxy {
    void init() {
        new CommonProxy().init();
    }

    @SubscribeEvent
    public void worldEvent(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world instanceof WorldServer) {
            CommonInterop.loadWorld((WorldServer) world);
        }
    }

    @SubscribeEvent
    public void chunkEvent(ChunkEvent.Load event) {
        if (event.getWorld() instanceof WorldServer) {
            CommonInterop.chunkLoaded(event.getChunk());
        }
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent evt) {
        if (evt.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) evt.player;
            NetworkManager networkManager = player.connection.netManager;
            networkManager.channel().pipeline().addBefore("packet_handler", "bbor", new ForgeServerChannelHandler(player));

            CommonInterop.playerLoggedIn(player);
        }
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent evt) {
        EntityPlayer player = evt.player;
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
        CommonInterop.tryHarvestBlock(block, event.getPos(), event.getWorld());
    }
}
