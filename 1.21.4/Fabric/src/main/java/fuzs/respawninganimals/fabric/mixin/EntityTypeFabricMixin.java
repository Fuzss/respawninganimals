package fuzs.respawninganimals.fabric.mixin;

import fuzs.puzzleslib.fabric.impl.event.SpawnTypeMob;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

@Mixin(EntityType.class)
abstract class EntityTypeFabricMixin<T extends Entity> {

    @ModifyVariable(
            method = "create(Lnet/minecraft/server/level/ServerLevel;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;ZZ)Lnet/minecraft/world/entity/Entity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;"
            )
    )
    public T create(T entity, ServerLevel serverLevel, @Nullable Consumer<T> consumer, BlockPos pos, EntitySpawnReason spawnType, boolean shouldOffsetY, boolean shouldOffsetYMore) {
        ((SpawnTypeMob) entity).puzzleslib$setSpawnType(spawnType);
        return entity;
    }
}
