package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import net.minecraft.server.v1_14_R1.Chunk;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.StructureStart;
import net.minecraft.server.v1_14_R1.WorldServer;

import java.util.Collection;
import java.util.Map;

public class CommonInterop {
    public static void chunkLoaded(Chunk chunk) {
        DimensionId dimensionId = DimensionId.from(((WorldServer) chunk.getWorld()).worldProvider.getDimensionManager());
        Map<String, StructureStart> structures = chunk.h();
        if (structures.size() > 0) EventBus.publish(new StructuresLoaded(structures, dimensionId));
    }

    public static void loadWorlds(Collection<WorldServer> worlds) {
        for (WorldServer world : worlds) {
            loadWorld(world);
        }
    }

    public static void loadWorld(WorldServer world) {
        EventBus.publish(new WorldLoaded(world));
    }

    public static void tick() {
        EventBus.publish(new ServerTick());
    }

    public static void playerLoggedIn(EntityPlayer player) {
        EventBus.publish(new PlayerLoggedIn(new ServerPlayer(player)));
    }

    public static void playerLoggedOut(EntityPlayer player) {
        EventBus.publish(new PlayerLoggedOut(player.getId()));
    }

    public static void playerSubscribed(EntityPlayer player) {
        EventBus.publish(new PlayerSubscribed(player.getId(), new ServerPlayer(player)));
    }
}
