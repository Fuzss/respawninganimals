package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import com.fuzs.respawnableanimals.common.element.AnimalsElement;
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

    private static final int field_234960_b_ = (int) Math.pow(17.0, 2.0);

    @Shadow
    @Final
    private int field_234981_a_;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Shadow
    @Final
    private Object2IntOpenHashMap<EntityClassification> field_234982_b_;

    @Inject(method = "func_234991_a_", at = @At("HEAD"), cancellable = true)
    private void isBelowMaxNumber(EntityClassification entityClassification, CallbackInfoReturnable<Boolean> callbackInfo) {

        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        if (element.isEnabled()) {

            // modify max number of animals
            int maxNumberOfCreature = entityClassification != EntityClassification.CREATURE ? entityClassification.getMaxNumberOfCreature() : element.maxAnimalNumber;
            int maxNumberInWorld = maxNumberOfCreature * this.field_234981_a_ / field_234960_b_;
            boolean isBelowMaxNumber = this.field_234982_b_.getInt(entityClassification) < maxNumberInWorld;

            callbackInfo.setReturnValue(isBelowMaxNumber);
        }
    }

}
