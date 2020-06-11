package com.irtimaled.bbor.client.interop;

import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProfile.Factory;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;

import java.util.function.Consumer;

public class ModPackFinder implements ResourcePackProvider {
    private static final String BBOR = "bbor";
    private final ResourcePack modPack;

    public ModPackFinder() {
        modPack = new DefaultResourcePack(BBOR);
    }

    @Override
    public <T extends ResourcePackProfile> void register(Consumer<T> consumer, Factory<T> factory) {
        T resourcePackInfo = ResourcePackProfile.of(BBOR,
                true,
                () -> this.modPack,
                factory,
                ResourcePackProfile.InsertionPosition.BOTTOM,
                ResourcePackSource.PACK_SOURCE_WORLD);
        if (resourcePackInfo != null) {
            consumer.accept(resourcePackInfo);
        }
    }
}
