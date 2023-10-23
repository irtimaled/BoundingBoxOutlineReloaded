package com.irtimaled.bbor.mixin.network.common.s2c;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMessageS2CPacket.class)
public class MixinSChatPacket {
    @Shadow
    private Text content;

    @Inject(method = "apply", at = @At("RETURN"))
    private void processPacket(CallbackInfo ci) {
        ClientInterop.handleSeedMessage(this.content);
    }
}
