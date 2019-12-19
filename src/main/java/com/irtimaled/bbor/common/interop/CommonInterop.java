package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.*;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import net.minecraft.server.v1_16_R2.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommonInterop {
    public static void chunkLoaded(Chunk chunk) {
        DimensionId dimensionId = DimensionId.from(((WorldServer) chunk.getWorld()).getDimensionKey());
        Map<String, StructureStart<?>> structures = new HashMap<>();
        chunk.h().forEach((key, value) -> structures.put(key.i(), value));
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
