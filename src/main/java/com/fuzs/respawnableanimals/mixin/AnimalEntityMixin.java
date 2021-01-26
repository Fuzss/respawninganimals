package com.fuzs.respawnableanimals.mixin;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends AgeableEntity {

    protected AnimalEntityMixin(EntityType<? extends AgeableEntity> type, World worldIn) {

        super(type, worldIn);
    }

    @Inject(method = "setInLove(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"))
    public void setInLove(@Nullable PlayerEntity player, CallbackInfo callbackInfo) {

        // enable persistence for animals that have been bred
        this.enablePersistence();
    }

    @Inject(method = "setInLove(I)V", at = @At("HEAD"))
    public void setInLove(int ticks, CallbackInfo callbackInfo) {

        if (ticks > 0) {

            // enable persistence for animals that have been bred
            this.enablePersistence();
        }
    }

}
