package com.irtimaled.bbor.common.events;

import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerChangedDimension {
    private final EntityPlayerMP player;

    public PlayerChangedDimension(EntityPlayerMP player) {
        this.player = player;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }
}
