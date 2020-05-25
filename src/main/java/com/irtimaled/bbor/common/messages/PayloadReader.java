package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.network.PacketBuffer;

public class PayloadReader {
    private final PacketBuffer buffer;

    public PayloadReader(PacketBuffer buffer) {
        this.buffer = buffer;
    }

    long readLong() {
        return buffer.readLong();
    }

    int readInt() {
        return buffer.readInt();
    }

    int readVarInt() {
        return buffer.readVarInt();
    }

    boolean isReadable() {
        return buffer.isReadable();
    }

    boolean isReadable(int count) {
        return buffer.isReadable(count);
    }

    char readChar() {
        return buffer.readChar();
    }

    Coords readCoords() {
        int x = readVarInt();
        int y = readVarInt();
        int z = readVarInt();
        return new Coords(x, y, z);
    }

    DimensionId readDimensionId() {
        return DimensionId.from(buffer.readResourceLocation());
    }
}
