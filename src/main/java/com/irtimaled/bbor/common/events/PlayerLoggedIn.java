package com.irtimaled.bbor.common.events;

import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerLoggedIn {
    private final EntityPlayerMP player;

    public PlayerLoggedIn(EntityPlayerMP player) {
        this.player = player;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }
}
