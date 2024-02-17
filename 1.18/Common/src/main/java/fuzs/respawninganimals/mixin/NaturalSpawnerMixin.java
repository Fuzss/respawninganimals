package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.world.entity.SpawnTypeMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(NaturalSpawner.class)
abstract class NaturalSpawnerMixin {

    @ModifyVariable(
            method = "spawnMobsForChunkGeneration", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;"
    )
    )
    private static Entity spawnMobsForChunkGeneration(Entity entity) {
        ((SpawnTypeMob) entity).respawninganimals$setSpawnType(MobSpawnType.CHUNK_GENERATION);
        return entity;
    }
}
