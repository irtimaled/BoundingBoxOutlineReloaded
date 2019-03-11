package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.ArrayUtils;

public class ForgeClientProxy extends ForgeCommonProxy {
    @Override
    void init() {
        new ClientProxy().init();
        Minecraft.getInstance().gameSettings.keyBindings = ArrayUtils.addAll(Minecraft.getInstance().gameSettings.keyBindings, KeyListener.keyBindings());
        ForgeNetworkHelper.addBusEventConsumer(AddBoundingBox.NAME, AddBoundingBox::getEvent);
        ForgeNetworkHelper.addBusEventConsumer(RemoveBoundingBox.NAME, RemoveBoundingBox::getEvent);
        ForgeNetworkHelper.addConsumer(InitializeClient.NAME, this::initializeClient);
    }

    private <T extends NetworkEvent> void initializeClient(final T packet) {
        EventBus.publish(InitializeClient.getEvent(new PayloadReader(packet.getPayload())));

        NetworkManager networkManager = Minecraft.getInstance().getConnection().getNetworkManager();
        networkManager.channel().pipeline().addBefore("packet_handler", "bbor", new ForgeClientChannelHandler());

        networkManager.sendPacket(SubscribeToServer.getPayload().build());
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        ClientInterop.render(event.getPartialTicks(), Minecraft.getInstance().player);
    }

    @SubscribeEvent
    public void worldUnloadedEvent(WorldEvent.Unload event) {
        if (event.getWorld() instanceof WorldClient && !Minecraft.getInstance().getConnection().getNetworkManager().isChannelOpen()) {
            ClientInterop.disconnectedFromRemoteServer();
        }
    }

    @SubscribeEvent
    public void clientChatEvent(ClientChatEvent event) {
        if (ClientInterop.interceptChatMessage(event.getMessage()))
            event.setMessage("");
    }

    @SubscribeEvent
    public void clientChatReceivedEvent(ClientChatReceivedEvent event) {
        ClientInterop.handleSeedMessage(event.getMessage());
    }
}
