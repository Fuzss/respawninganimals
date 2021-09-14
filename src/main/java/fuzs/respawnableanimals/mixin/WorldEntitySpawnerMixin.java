package fuzs.respawnableanimals.mixin;

import fuzs.respawnableanimals.RespawnableAnimals;
import fuzs.respawnableanimals.element.RespawnableAnimalsElement;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(WorldEntitySpawner.class)
public abstract class WorldEntitySpawnerMixin {

    @Redirect(method = "spawnForChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityClassification;isPersistent()Z"))
    private static boolean isPersistent(EntityClassification entityclassification, ServerWorld serverWorld) {

        // remove 400 tick delay for spawning animals
        RespawnableAnimalsElement element = (RespawnableAnimalsElement) RespawnableAnimals.RESPAWNABLE_ANIMALS;
        if (entityclassification == EntityClassification.CREATURE && !serverWorld.getGameRules().getBoolean(RespawnableAnimalsElement.PERSISTENT_ANIMALS)) {

            return false;
        }

        return entityclassification.isPersistent();
    }

}