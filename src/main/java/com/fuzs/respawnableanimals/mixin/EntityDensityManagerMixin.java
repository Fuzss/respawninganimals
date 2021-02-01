package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import com.fuzs.respawnableanimals.common.element.AnimalsElement;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(WorldEntitySpawner.EntityDensityManager.class)
public abstract class EntityDensityManagerMixin {

    @Redirect(method = "func_234991_a_", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityClassification;getMaxNumberOfCreature()I"))
    public int getMaxNumberOfCreature(EntityClassification entityclassification) {

        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        if (element.isEnabled() && entityclassification == EntityClassification.CREATURE) {

            return element.maxAnimalNumber;
        }

        return entityclassification.getMaxNumberOfCreature();
    }

}
