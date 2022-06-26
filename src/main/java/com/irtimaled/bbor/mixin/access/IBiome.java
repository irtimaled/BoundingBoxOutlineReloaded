package com.irtimaled.bbor.mixin.access;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class)
public interface IBiome {

    @Accessor
    SpawnSettings getSpawnSettings();

}
