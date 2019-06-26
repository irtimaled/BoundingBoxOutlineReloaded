package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

public class ForgeClientProxy extends ForgeCommonProxy {
    private ClientProxy clientProxy;

    @Override
    void init() {
        clientProxy = new ClientProxy();
        clientProxy.init();
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.addAll(Minecraft.getMinecraft().gameSettings.keyBindings, KeyListener.keyBindings());
    }

    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent evt) {
        int keyCode = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        boolean down = Keyboard.getEventKeyState();
        KeyListener.setKeyBindState(keyCode, down);
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ClientInterop.render(event.getPartialTicks(), player);
    }

    @SubscribeEvent
    public void clientConnectionToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent evt) {
        NetworkManager networkManager = evt.getManager();
        networkManager.channel().pipeline().addBefore("packet_handler", "bbor", new ForgeClientChannelHandler(networkManager));

        ClientInterop.connectedToRemoteServer(networkManager);
    }

    @SubscribeEvent
    public void clientDisconnectionFromServerEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent evt) {
        ClientInterop.disconnectedFromRemoteServer();
    }

    @SubscribeEvent
    public void clientChatEvent(ClientChatEvent event) {
        String message = event.getMessage();
        if (ClientInterop.interceptChatMessage(message))
            event.setMessage("");
    }
}
