package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.element.RespawnableAnimalsElement;
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

            return RespawningAnimals.CONFIG.get(ServerConfig.class).maxAnimalNumber;
        }

        return entityclassification.getMaxInstancesPerChunk();
    }

}
