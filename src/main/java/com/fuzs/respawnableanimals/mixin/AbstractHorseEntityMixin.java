package com.fuzs.respawnableanimals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity {

    public AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> type, World worldIn) {

        super(type, worldIn);
    }

    @Inject(method = "setHorseTamed", at = @At("HEAD"))
    public void setHorseTamed(boolean tamed, CallbackInfo callbackInfo) {

        // only needed for skeleton horses as other horses are made persistent when tamed
        if (tamed) {

            this.enablePersistence();
        }
    }

}
