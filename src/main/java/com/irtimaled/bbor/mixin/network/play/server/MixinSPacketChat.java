package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SPacketChat.class)
public class MixinSPacketChat {
    @Shadow private ITextComponent chatComponent;

    @Inject(method = "processPacket",  at = @At("RETURN"))
    private void processPacket(CallbackInfo ci) {
        ClientInterop.handleSeedMessage(this.chatComponent);
    }
}
