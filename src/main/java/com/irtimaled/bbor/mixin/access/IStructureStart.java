package com.irtimaled.bbor.mixin.access;

import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureStart.class)
public interface IStructureStart {

    @Accessor
    BlockBox getBoundingBox();

}
