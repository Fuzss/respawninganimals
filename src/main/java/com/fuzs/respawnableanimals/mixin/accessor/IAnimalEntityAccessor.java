package com.fuzs.respawnableanimals.mixin.accessor;

import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimalEntity.class)
public interface IAnimalEntityAccessor {

    @Accessor
    int getInLove();

}
