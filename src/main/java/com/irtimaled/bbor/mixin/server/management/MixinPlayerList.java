package com.irtimaled.bbor.mixin.server.management;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {
    @Inject(method = "initializeConnectionToPlayer", at = @At("RETURN"))
    private void playerLoggedIn(NetworkManager networkManager, ServerPlayerEntity player, CallbackInfo ci) {
        CommonInterop.playerLoggedIn(player);
    }

    @Inject(method = "playerLoggedOut", at = @At("HEAD"))
    private void playerLoggedOut(ServerPlayerEntity player, CallbackInfo ci) {
        CommonInterop.playerLoggedOut(player);
    }
}
