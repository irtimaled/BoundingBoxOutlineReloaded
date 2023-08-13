package com.irtimaled.bbor.mixin.access;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayNetworkHandler.class)
public interface IServerPlayNetworkHandler {

    @Accessor
    ClientConnection getConnection();

}
