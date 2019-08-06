package com.irtimaled.bbor.client.interop;

import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;

import java.util.Map;

public class ModPackFinder implements ResourcePackProvider {
    private static final String BBOR = "bbor";
    private final ResourcePack modPack;

    public ModPackFinder() {
        modPack = new DefaultResourcePack(BBOR);
    }

    @Override
    public <T extends ResourcePackProfile> void register(Map<String, T> map, ResourcePackProfile.Factory<T> factory) {
        T resourcePackInfo = ResourcePackProfile.of(BBOR,
                true,
                () -> this.modPack,
                factory,
                ResourcePackProfile.InsertionPosition.BOTTOM);
        if (resourcePackInfo != null) {
            map.put(BBOR, resourcePackInfo);
        }
    }
}
