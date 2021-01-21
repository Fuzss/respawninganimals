package com.fuzs.respawnableanimals.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(WorldEntitySpawner.EntityDensityManager.class)
public abstract class EntityDensityManagerMixin {

    @Shadow
    @Final
    private int field_234981_a_;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Shadow
    @Final
    private Object2IntOpenHashMap<EntityClassification> field_234982_b_;

    @Inject(method = "func_234991_a_", at = @At("HEAD"), cancellable = true)
    private void isBelowMaxNumber(EntityClassification entityClassification, CallbackInfoReturnable<Boolean> callbackInfo) {

        // decrease chunk constant to 15^2 (from 17^2) and increase max number of animals to 15 (from 10)
        int maxNumberOfCreature = entityClassification != EntityClassification.CREATURE ? entityClassification.getMaxNumberOfCreature() : 15;
        int maxNumberInWorld = maxNumberOfCreature * this.field_234981_a_ / (int) Math.pow(15.0, 2.0);
        boolean isBelowMaxNumber = this.field_234982_b_.getInt(entityClassification) < maxNumberInWorld;

        callbackInfo.setReturnValue(isBelowMaxNumber);
    }

}
