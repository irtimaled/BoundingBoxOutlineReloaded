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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.ArrayUtils;

public class ForgeClientProxy extends ClientProxy {
    @Override
    public void init() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, lastScreen) -> new SettingsScreen(lastScreen));
        super.init();
    }
}
