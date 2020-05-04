package com.irtimaled.bbor.common.events;

public class PlayerLoggedOut {
    private final int playerId;

    public PlayerLoggedOut(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
