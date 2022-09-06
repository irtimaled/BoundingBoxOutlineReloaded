package com.irtimaled.bbor.common.messages;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.bukkit.NMS.api.NMSClassFunction;
import com.irtimaled.bbor.bukkit.NMS.api.NMSClassName;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PayloadBuilder {

    private static final Map<String, Object> packetNames = new HashMap<>();

    @NotNull
    @Contract("_ -> new")
    static PayloadBuilder clientBound(String name) {
        return new PayloadBuilder(packetNames.computeIfAbsent(name, NMSHelper::minecraftKeyNew), NMSHelper.packetPlayOutCustomPayloadNewFunction());
    }

    private final Object name;
    private final NMSClassFunction packetBuilder;
    private final Object buffer;

    private PayloadBuilder(Object name, NMSClassFunction packetBuilder) {
        this.name = name;
        this.buffer = NMSHelper.packetDataSerializerNew(Unpooled.buffer());
        this.packetBuilder = packetBuilder;
    }

    private Object packet;

    public Object build() {
        if (packet == null)
            packet = packetBuilder.apply(name, buffer);
        return packet;
    }

    PayloadBuilder writeLong(long value) {
        NMSHelper.packetDataSerializerWriteLong(buffer, value);
        packet = null;
        return this;
    }

    PayloadBuilder writeInt(int value) {
        NMSHelper.packetDataSerializerWriteInt(buffer, value);
        packet = null;
        return this;
    }

    PayloadBuilder writeVarInt(int value) {
        NMSHelper.packetDataSerializerWriteVarInt(buffer, value);
        packet = null;
        return this;
    }

    PayloadBuilder writeChar(char value) {
        NMSHelper.packetDataSerializerWriteChar(buffer, value);
        packet = null;
        return this;
    }

    PayloadBuilder writeCoords(@NotNull Coords coords) {
        return writeVarInt(coords.getX())
                .writeVarInt(coords.getY())
                .writeVarInt(coords.getZ());
    }

    public PayloadBuilder writeDimensionId(@NotNull DimensionId dimensionId) {
        NMSHelper.packetDataSerializerWriteMinecraftKey(buffer, NMSHelper.cast(NMSClassName.MinecraftKey, dimensionId.value()));
        packet = null;
        return this;
    }
}
