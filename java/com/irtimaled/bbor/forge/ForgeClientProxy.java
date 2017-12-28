package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ForgeClientProxy extends ForgeCommonProxy {
    private ClientProxy proxy;

    @Override
    public ClientProxy getProxy() {
        if (proxy == null) {
            proxy = new ClientProxy();
        }
        return proxy;
    }

    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent evt) {
        getProxy().keyPressed();
    }

    @Override
    protected boolean isRemotePlayer(EntityPlayer player) {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            EntityPlayer singlePlayer = Minecraft.getMinecraft().player;
            if (singlePlayer == null)
                return false;
            return player.getGameProfile() != singlePlayer.getGameProfile();
        }
        return true;
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        getProxy().render(event.getPartialTicks());
    }

    @SubscribeEvent
    public void clientConnectionToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent evt) {
        if (!evt.isLocal()) {
            getProxy().playerConnectedToServer(evt.getManager());
        }
    }

    @SubscribeEvent
    public void clientDisconnectionFromServerEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent evt) {
        getProxy().playerDisconnectedFromServer();
    }
}
