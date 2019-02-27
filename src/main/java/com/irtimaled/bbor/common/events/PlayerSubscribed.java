package com.irtimaled.bbor.common.events;

import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerSubscribed {
    private final EntityPlayerMP player;

    public PlayerSubscribed(EntityPlayerMP player) {
        this.player = player;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }
}
