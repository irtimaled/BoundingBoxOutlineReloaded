package com.irtimaled.bbor.common.events;

import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerLoggedOut {
    private final EntityPlayerMP player;

    public PlayerLoggedOut(EntityPlayerMP player) {
        this.player = player;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }
}
