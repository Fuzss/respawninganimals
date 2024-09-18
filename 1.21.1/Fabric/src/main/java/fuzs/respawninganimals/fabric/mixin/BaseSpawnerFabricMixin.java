package fuzs.respawninganimals.fabric.mixin;

import fuzs.puzzleslib.fabric.impl.event.SpawnTypeMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BaseSpawner.class)
abstract class BaseSpawnerFabricMixin {

    @ModifyVariable(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;"
            )
    )
    public Entity serverTick(Entity entity) {
        ((SpawnTypeMob) entity).puzzleslib$setSpawnType(MobSpawnType.SPAWNER);
        return entity;
    }
}
