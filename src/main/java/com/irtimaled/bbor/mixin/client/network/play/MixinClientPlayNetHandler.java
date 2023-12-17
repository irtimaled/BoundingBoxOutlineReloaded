package com.irtimaled.bbor.mixin.client.network.play;

import com.irtimaled.bbor.client.events.GameJoin;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.BBORCustomPayload;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.AddBoundingBox;
import com.irtimaled.bbor.common.messages.InitializeClient;
import com.irtimaled.bbor.common.messages.PayloadReader;
import com.irtimaled.bbor.common.messages.StructureListSync;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import com.irtimaled.bbor.common.messages.protocols.PacketSplitter;
import com.irtimaled.bbor.common.messages.servux.ServuxStructurePackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetHandler {

    @Inject(method = "onUnloadChunk", at = @At("RETURN"))
    private void onChunkUnload(UnloadChunkS2CPacket packet, CallbackInfo ci) {
        ClientInterop.unloadChunk(packet.pos().x, packet.pos().z);
    }

//    @Inject(method = "onSynchronizeTags", at = @At("RETURN"))
//    private void onSynchronizeTags(SynchronizeTagsS2CPacket packet, CallbackInfo ci) {
//        CommonInterop.loadWorldStructures(this.world);
//    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onGameJoin(CallbackInfo ci) {
        EventBus.publish(new GameJoin());
    }

    @Inject(method = "warnOnUnknownPayload", at = @At(value = "CONSTANT", args = "stringValue=Unknown custom packet payload: {}"), cancellable = true)
    private void suppressWarning(CustomPayload payload, CallbackInfo ci) {
        if (payload.id().equals(ServuxStructurePackets.CHANNEL)) ci.cancel();
    }


    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    private void interceptSendCommand(String command, CallbackInfo ci) {
        if (ClientInterop.interceptCommandUsage(command)) {
            ci.cancel();
        }
    }

    @Inject(method = "onCustomPayload(Lnet/minecraft/network/packet/CustomPayload;)V", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayload customPayload, CallbackInfo ci) {
        if (customPayload instanceof BBORCustomPayload payload) {
            if (payload.id().getNamespace().equals("bbor")) {
                PayloadReader reader = new PayloadReader(payload);
                switch (payload.id().toString()) {
                    case InitializeClient.NAME -> {
                        EventBus.publish(InitializeClient.getEvent(reader));
                        ((ClientPlayNetworkHandler) (Object) this).sendPacket(SubscribeToServer.getPayload().build());
                    }
                    case AddBoundingBox.NAME -> {
                        EventBus.publish(AddBoundingBox.getEvent(reader));
                    }
                    case StructureListSync.NAME -> {
                        StructureListSync.handleEvent(reader);
                    }
                }
                ci.cancel();
            } else if (payload.id().toString().equals("servux:structures")) {
                PacketByteBuf data = null;
                try {
                    data = PacketSplitter.receive((ClientPlayPacketListener) this, payload);
                    if (data != null) {
                        PayloadReader reader = new PayloadReader(data);
                        // ServuxStructurePackets.handleEvent(reader);  TODO fix
                    }
                } finally {
                    if (data != null)
                        data.release();
                }
            }
        }
    }
}
