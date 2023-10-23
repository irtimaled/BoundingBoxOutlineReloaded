package com.irtimaled.bbor.mixin.network.common.c2s;

import com.irtimaled.bbor.common.BBORCustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomPayloadC2SPacket.class)
public class MixinCCustomPayloadPacket {

    @Inject(method = "readPayload", at = @At(value = "HEAD"), cancellable = true)
    private static void onOnCustomPayloadR(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomPayload> cir) {
        if (id.getNamespace().equals("bbor")) {
            cir.setReturnValue(new BBORCustomPayload(id, buf));
        }
    }
}
