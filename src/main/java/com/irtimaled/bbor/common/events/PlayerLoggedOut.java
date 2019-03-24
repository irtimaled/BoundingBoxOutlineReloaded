package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.ServerPlayer;

public class PlayerLoggedOut {
    private final ServerPlayer player;

    public PlayerLoggedOut(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
