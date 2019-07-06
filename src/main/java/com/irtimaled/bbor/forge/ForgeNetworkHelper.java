package com.irtimaled.bbor.forge;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.messages.PayloadReader;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

class ForgeNetworkHelper {
    static void addBusEventConsumer(String name, Function<PayloadReader, ?> getEventSupplier) {
        addClientConsumer(name, (payload) -> EventBus.publish(getEventSupplier.apply(payload)));
    }

    private static <T extends NetworkEvent> void addConsumer(String name, Consumer<T> consumer) {
        NetworkRegistry
                .newEventChannel(new ResourceLocation(name), () -> "BBOR", value -> true, value -> true)
                .addListener(consumer);
    }

    static void addClientConsumer(String name, Consumer<PayloadReader> consumer) {
        addConsumer(name, (NetworkEvent.ServerCustomPayloadEvent e) -> {
            NetworkEvent.Context context = e.getSource().get();
            consumer.accept(new PayloadReader(e.getPayload()));
            context.setPacketHandled(true);
        });
    }

    static void addServerConsumer(String name, BiConsumer<PayloadReader, ServerPlayerEntity> consumer) {
        addConsumer(name, (NetworkEvent.ClientCustomPayloadEvent e) -> {
            NetworkEvent.Context context = e.getSource().get();
            consumer.accept(new PayloadReader(e.getPayload()), context.getSender());
            context.setPacketHandled(true);
        });
    }
}
