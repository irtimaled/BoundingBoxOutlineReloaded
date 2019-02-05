package com.irtimaled.bbor.litemod.mixins;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient extends World {
    public MixinWorldClient() {
        super(null, null, null, null, true);
    }

    @Inject(method = "sendQuittingDisconnectingPacket",
    at = @At("RETURN"))
    private void onDisconnecting(CallbackInfo ci) {
        BoundingBoxOutlineReloaded.playerDisconnectedFromServer();
    }
}
