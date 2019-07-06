package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.TypeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;

public class ForgeClientInterop {
    public static void updateWorldSpawnReceived(SSpawnPositionPacket packet) {
        ClientInterop.updateWorldSpawnReceived(packet.getSpawnPos());
    }

    public static void receivedChunk(SChunkDataPacket packet) {
        ClientInterop.receivedChunk(packet.getChunkX(), packet.getChunkZ());
    }

    public static void registerClientCommands(IClientPlayNetHandler netHandlerPlayClient) {
        TypeHelper.doIfType(netHandlerPlayClient, ClientPlayNetHandler.class, handler -> ClientInterop.registerClientCommands(handler.getCommandDispatcher()));
    }

    public static void render(float partialTicks) {
        ClientInterop.render(partialTicks, Minecraft.getInstance().player);
    }

    public static void renderDeferred() {
        ClientInterop.renderDeferred();
    }
}
