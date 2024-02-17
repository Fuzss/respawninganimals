package fuzs.respawninganimals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.respawninganimals.world.entity.SpawnTypeMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(NaturalSpawner.class)
abstract class NaturalSpawnerFabricMixin {

    @WrapOperation(
            method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/NaturalSpawner;isValidPositionForMob(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;D)Z"
            )
    )
    private static boolean spawnCategoryForPosition(ServerLevel level, Mob mob, double distance, Operation<Boolean> operation) {
        // use this approach since Carpet mod has a bunch of redirects in here :(
        if (operation.call(level, mob, distance)) {
            ((SpawnTypeMob) mob).respawninganimals$setSpawnType(MobSpawnType.NATURAL);
            return true;
        } else {
            return false;
        }
    }

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
