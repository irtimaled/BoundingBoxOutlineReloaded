package com.irtimaled.bbor.mixin.server.management;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerList {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void playerLoggedIn(ClientConnection netManager, ServerPlayerEntity player, CallbackInfo ci) {
        CommonInterop.playerLoggedIn(player);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void playerLoggedOut(ServerPlayerEntity player, CallbackInfo ci) {
        CommonInterop.playerLoggedOut(player);
    }
}
