package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.world.entity.SpawnTypeMob;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SummonCommand.class)
abstract class SummonCommandFabricMixin {

    @ModifyVariable(
            method = "createEntity", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;"
    )
    )
    private static Entity createEntity(Entity entity) {
        ((SpawnTypeMob) entity).respawninganimals$setSpawnType(MobSpawnType.COMMAND);
        return entity;
    }
}
