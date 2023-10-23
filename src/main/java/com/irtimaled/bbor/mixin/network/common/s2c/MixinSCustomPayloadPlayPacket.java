package com.irtimaled.bbor.mixin.network.common.s2c;

import com.irtimaled.bbor.common.BBORCustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinSCustomPayloadPlayPacket {

    @Inject(method = "readPayload", at = @At(value = "HEAD"), cancellable = true)
    private static void onOnCustomPayloadR(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomPayload> cir) {
        if (id.getNamespace().equals("bbor") || id.toString().equals("servux:structures")) {
            cir.setReturnValue(new BBORCustomPayload(id, buf));
        }
    }
}
