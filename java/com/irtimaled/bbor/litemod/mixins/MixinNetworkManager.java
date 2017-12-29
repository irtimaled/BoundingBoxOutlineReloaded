package com.irtimaled.bbor.litemod.mixins;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.login.client.CPacketLoginStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V",
            at = @At("RETURN"))
    private void onLoaded(Packet<?> packetIn, CallbackInfo ci) {
        if (packetIn instanceof CPacketLoginStart) {
            BoundingBoxOutlineReloaded.playerConnectedToServer((NetworkManager)(Object)this);
        }
    }
}