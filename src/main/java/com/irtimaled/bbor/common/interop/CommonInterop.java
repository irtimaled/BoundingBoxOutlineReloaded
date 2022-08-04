package com.irtimaled.bbor.common.interop;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.PlayerLoggedIn;
import com.irtimaled.bbor.common.events.PlayerLoggedOut;
import com.irtimaled.bbor.common.events.PlayerSubscribed;
import com.irtimaled.bbor.common.events.ServerTick;
import com.irtimaled.bbor.common.events.StructuresLoaded;
import com.irtimaled.bbor.common.events.WorldLoaded;
import com.irtimaled.bbor.common.models.DimensionId;
import com.irtimaled.bbor.common.models.ServerPlayer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommonInterop {
    public static void chunkLoaded(Chunk chunk) {
        DimensionId dimensionId = DimensionId.from(chunk.q.getTypeKey());
        Map<String, StructureStart> structures = new HashMap<>();
        chunk.g().forEach((key, value) -> structures.put(BoundingBoxType.getByStructure(key).getName(), value));
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
        EventBus.publish(new PlayerLoggedOut(player.ae()));
    }

    public static void playerSubscribed(EntityPlayer player) {
        EventBus.publish(new PlayerSubscribed(player.ae(), new ServerPlayer(player)));
    }
}
