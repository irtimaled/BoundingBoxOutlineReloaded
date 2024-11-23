package com.irtimaled.bbor.mixin.network.common;

import com.irtimaled.bbor.common.BBORCustomPayload;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.network.packet.CustomPayload$1")
public class MixinCustomPayload$1 {

    private static final Identifier BBOR_SERVUX_CHANNEL = Identifier.tryParse("servux:structures");

    @Inject(
            method = "decode(Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/network/packet/CustomPayload;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/CustomPayload$1;getCodec(Lnet/minecraft/util/Identifier;)Lnet/minecraft/network/codec/PacketCodec;"),
            cancellable = true
    )
    private <B extends PacketByteBuf> void onCustomPayloadDecode(B packetByteBuf, CallbackInfoReturnable<CustomPayload> cir, @Local @NotNull Identifier identifier) {
        if (identifier.getNamespace().equals("bbor") || identifier.equals(BBOR_SERVUX_CHANNEL)) {
            cir.setReturnValue(new BBORCustomPayload(identifier, packetByteBuf));
        }
    }

    @Inject(
            method = "encode(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/packet/CustomPayload;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/CustomPayload$1;getCodec(Lnet/minecraft/util/Identifier;)Lnet/minecraft/network/codec/PacketCodec;"),
            cancellable = true
    )
    private <T extends CustomPayload, B extends PacketByteBuf> void onCustomPayloadEncode(B value, CustomPayload.Id<T> id, CustomPayload payload, CallbackInfo ci) {
        if (payload instanceof BBORCustomPayload bborCustomPayload) {
            value.writeBytes(bborCustomPayload.byteBuf());
            ci.cancel();
        }
    }
}
