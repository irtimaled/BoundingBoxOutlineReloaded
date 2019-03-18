package com.irtimaled.bbor.common.messages;

public class SubscribeToServer {
    public static final String NAME = "bbor:subscribe";

    public static PayloadBuilder getPayload() {
        return PayloadBuilder.serverBound(NAME);
    }
}
