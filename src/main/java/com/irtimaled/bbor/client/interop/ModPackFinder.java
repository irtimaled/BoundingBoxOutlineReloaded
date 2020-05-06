package com.irtimaled.bbor.client.interop;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.VanillaPack;

import java.util.Map;

public class ModPackFinder implements IPackFinder {
    private static final String BBOR = "bbor";
    private final IResourcePack modPack;

    public ModPackFinder() {
        modPack = new VanillaPack(BBOR);
    }

    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> map, ResourcePackInfo.IFactory<T> factory) {
        T resourcePackInfo = ResourcePackInfo.createResourcePack(BBOR,
                true,
                () -> this.modPack,
                factory,
                ResourcePackInfo.Priority.BOTTOM);
        if (resourcePackInfo != null) {
            map.put(BBOR, resourcePackInfo);
        }
    }
}
