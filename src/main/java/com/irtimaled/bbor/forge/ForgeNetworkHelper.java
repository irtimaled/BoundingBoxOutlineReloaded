package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.PayloadReader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;

import java.util.function.Consumer;
import java.util.function.Function;

class ForgeNetworkHelper {
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    static <T extends NetworkEvent> void addBusEventConsumer(String name, Function<PayloadReader, ?> getEventSupplier) {
        addConsumer(name, e -> EventBus.publish(getEventSupplier.apply(new PayloadReader(e.getPayload()))));
    }

    static <T extends NetworkEvent> void addConsumer(String name, Consumer<T> consumer) {
        NetworkRegistry.newEventChannel(new ResourceLocation(name), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals)
                .addListener(consumer);
    }
}
