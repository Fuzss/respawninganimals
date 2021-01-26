package com.fuzs.respawnableanimals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(PigEntity.class)
public abstract class PigEntityMixin extends AnimalEntity {

    protected PigEntityMixin(EntityType<? extends AnimalEntity> type, World worldIn) {

        super(type, worldIn);
    }

    @Inject(method = "setSaddled", at = @At("HEAD"))
    public void setSaddled(boolean saddled, CallbackInfo callbackInfo) {

        if (saddled) {

            // enable persistence for pigs that have been saddled
            this.enablePersistence();
        }
    }

}
