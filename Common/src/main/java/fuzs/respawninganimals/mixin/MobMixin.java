package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.handler.AnimalSpawningHandler;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void setPersistenceRequired();

    @Shadow
    public abstract boolean removeWhenFarAway(double distanceToClosestPlayer);

    @Inject(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;removeWhenFarAway(D)Z"), cancellable = true)
    public void checkDespawn$inject$invoke(CallbackInfo callback) {
        // be careful here, Forge hooks into the method early (don't capture locals)
        Entity entity = this.level.getNearestPlayer(this, -1.0);
        double distanceToClosestPlayer = entity.distanceToSqr(this);
        if (!this.removeWhenFarAway(distanceToClosestPlayer) && AnimalSpawningHandler.canAnimalDespawn((Mob) (Object) this, distanceToClosestPlayer)) {
            this.discard();
            callback.cancel();
        }
    }

    @Inject(method = "requiresCustomPersistence", at = @At("HEAD"), cancellable = true)
    public void requiresCustomPersistence$inject$head(CallbackInfoReturnable<Boolean> callback) {
        if (!this.level.getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE) && (Mob) (Object) this instanceof Animal animal) {
            if (RespawningAnimals.CONFIG.get(ServerConfig.class).animalBlacklist.contains(animal.getType()) && canDespawnBePrevented$custom(animal)) {
                callback.setReturnValue(true);
            }
        }
    }

    @Unique
    private static boolean canDespawnBePrevented$custom(Mob mob) {
        Entity closestPlayer = mob.level.getNearestPlayer(mob, -1.0);
        if (closestPlayer != null) {
            double distanceToPlayer = mob.distanceToSqr(mob);
            return !mob.removeWhenFarAway(distanceToPlayer);
        }
        return true;
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    public void finalizeSpawn$inject$head(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> callback) {
        if (RespawningAnimals.CONFIG.get(ServerConfig.class).summonedMobPersistence) {
            // affects all mobs, not just animals
            if (mobSpawnType == MobSpawnType.COMMAND || mobSpawnType == MobSpawnType.SPAWN_EGG || mobSpawnType == MobSpawnType.DISPENSER) {
                this.setPersistenceRequired();
            }
        }
    }
}
