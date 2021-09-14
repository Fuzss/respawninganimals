package fuzs.respawnableanimals.mixin;

import fuzs.respawnableanimals.RespawnableAnimals;
import fuzs.respawnableanimals.element.RespawnableAnimalsElement;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(WorldEntitySpawner.EntityDensityManager.class)
public abstract class EntityDensityManagerMixin {

    @Redirect(method = "canSpawnForCategory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityClassification;getMaxInstancesPerChunk()I"))
    public int getMaxNumberOfCreature(EntityClassification entityclassification) {

        if (entityclassification == EntityClassification.CREATURE) {

            return ((RespawnableAnimalsElement) RespawnableAnimals.RESPAWNABLE_ANIMALS).maxAnimalNumber;
        }

        return entityclassification.getMaxInstancesPerChunk();
    }

}
