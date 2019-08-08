package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.ServerPlayer;

public class PlayerSubscribed {
    private final int playerId;
    private final ServerPlayer player;

    public PlayerSubscribed(int playerId, ServerPlayer player) {
        this.playerId = playerId;
        this.player = player;
    }

    public int getPlayerId() {
        return playerId;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
