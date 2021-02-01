package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import com.fuzs.respawnableanimals.common.element.AnimalsElement;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(WorldEntitySpawner.class)
public abstract class WorldEntitySpawnerMixin {

    @Redirect(method = "func_234979_a_", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityClassification;getAnimal()Z"))
    private static boolean getAnimal(EntityClassification entityclassification, ServerWorld serverWorld) {

        // remove 400 tick delay for spawning animals
        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        if (element.isEnabled() && entityclassification == EntityClassification.CREATURE && !serverWorld.getGameRules().getBoolean(AnimalsElement.PERSISTENT_ANIMALS)) {

            return false;
        }

        return entityclassification.getAnimal();
    }

}
