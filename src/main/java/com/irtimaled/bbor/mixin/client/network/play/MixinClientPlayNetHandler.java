package com.irtimaled.bbor.mixin.client.network.play;

import com.irtimaled.bbor.client.events.GameJoin;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.servux.ServuxStructurePackets;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetHandler {
    @Shadow
    private ClientWorld world;

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        ClientInterop.disconnectedFromRemoteServer();
    }

    @Inject(method = "onUnloadChunk", at = @At("RETURN"))
    private void onChunkUnload(UnloadChunkS2CPacket packet, CallbackInfo ci) {
        ClientInterop.unloadChunk(packet.getX(), packet.getZ());
    }

//    @Inject(method = "onSynchronizeTags", at = @At("RETURN"))
//    private void onSynchronizeTags(SynchronizeTagsS2CPacket packet, CallbackInfo ci) {
//        CommonInterop.loadWorldStructures(this.world);
//    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onGameJoin(CallbackInfo ci) {
        EventBus.publish(new GameJoin());
    }

    @Inject(method = "onCustomPayload", at = @At(value = "CONSTANT", args = "stringValue=Unknown custom packed identifier: {}"), cancellable = true)
    private void suppressWarning(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (packet.getChannel().equals(ServuxStructurePackets.CHANNEL)) ci.cancel();
    }


    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    private void interceptSendCommand(String command, CallbackInfo ci) {
        if (ClientInterop.interceptCommandUsage(command)) {
            ci.cancel();
        }
    }
}
