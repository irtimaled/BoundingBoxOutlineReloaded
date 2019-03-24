package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.ServerPlayer;

public class PlayerSubscribed {
    private final ServerPlayer player;

    public PlayerSubscribed(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
