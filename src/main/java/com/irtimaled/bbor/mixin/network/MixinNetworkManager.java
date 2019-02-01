package com.irtimaled.bbor.mixin.network;

import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at= @At("RETURN"))
    public void sendPacket(Packet<?> packetIn, GenericFutureListener<?> listener, CallbackInfo ci) {
        if (packetIn instanceof net.minecraft.network.login.client.CPacketLoginStart) {
            com.irtimaled.bbor.client.BoundingBoxOutlineReloaded.playerConnectedToServer((NetworkManager) (Object) this);
        }
    }

}
