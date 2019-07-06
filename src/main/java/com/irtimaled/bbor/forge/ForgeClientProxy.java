package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.PayloadReader;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.ArrayUtils;

public class ForgeClientProxy extends ForgeCommonProxy {
    @Override
    void init() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, lastScreen) -> new SettingsScreen(lastScreen));
        registerMessageConsumers();
        ClientProxy.registerKeyBindings();
        GameSettings gameSettings = Minecraft.getInstance().gameSettings;
        gameSettings.keyBindings = ArrayUtils.addAll(gameSettings.keyBindings, KeyListener.keyBindings());
        new ClientProxy().init();
    }

    private void initializeClient(PayloadReader payload) {
        EventBus.publish(InitializeClient.getEvent(payload));
        Minecraft.getInstance().getConnection().sendPacket(SubscribeToServer.getPayload().build());
    }

    @Override
    void registerMessageConsumers() {
        super.registerMessageConsumers();
        ForgeNetworkHelper.addBusEventConsumer(AddBoundingBox.NAME, reader -> AddBoundingBox.getEvent(reader, AddBoundingBox.NAME));
        ForgeNetworkHelper.addBusEventConsumer(AddBoundingBox.LEGACY, reader -> AddBoundingBox.getEvent(reader, AddBoundingBox.LEGACY));
        ForgeNetworkHelper.addClientConsumer(InitializeClient.NAME, this::initializeClient);
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        ClientInterop.render(event.getPartialTicks(), Minecraft.getInstance().player);
    }

    @SubscribeEvent
    public void loggedOutEvent(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientInterop.disconnectedFromRemoteServer();
    }

    @SubscribeEvent
    public void clientChatEvent(ClientChatEvent event) {
        String message = event.getMessage();
        if (ClientInterop.interceptChatMessage(message)) {
            Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(message);
            event.setMessage("");
        }
    }

    @SubscribeEvent
    public void clientChatReceivedEvent(ClientChatReceivedEvent event) {
        ClientInterop.handleSeedMessage(event.getMessage());
    }
}
