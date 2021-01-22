package com.fuzs.respawnableanimals.mixin;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends AgeableEntity {

    protected AnimalEntityMixin(EntityType<? extends AgeableEntity> type, World worldIn) {

        super(type, worldIn);
    }

    @Redirect(method = "livingTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particles/IParticleData;DDDDDD)V"))
    public void addParticle(World world, IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {

        if (!world.isRemote) {

            // call correct method for sending spawned particles to the client
            ((ServerWorld) world).spawnParticle(particleData, x, y, z, 1, xSpeed, ySpeed, zSpeed, 0.0);
        }
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
