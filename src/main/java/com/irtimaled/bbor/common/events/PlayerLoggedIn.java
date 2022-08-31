package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.ServerPlayer;

public class PlayerLoggedIn {

    private final ServerPlayer player;

    public PlayerLoggedIn(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
