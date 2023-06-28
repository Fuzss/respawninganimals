package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.handler.AnimalSpawningHandler;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void setPersistenceRequired();

    @Shadow
    public abstract boolean removeWhenFarAway(double distanceToClosestPlayer);

    @Inject(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;removeWhenFarAway(D)Z"), cancellable = true)
    public void checkDespawn(CallbackInfo callback) {
        // be careful here, Forge hooks into the method early (don't capture locals)
        Player nearestPlayer = this.level().getNearestPlayer(this, -1.0);
        double distance = nearestPlayer != null ? nearestPlayer.distanceToSqr(this) : Double.MAX_VALUE;
        if (!this.removeWhenFarAway(distance) && AnimalSpawningHandler.canAnimalDespawn(Mob.class.cast(this), distance)) {
            this.discard();
            callback.cancel();
        }
    }

    @Inject(method = "requiresCustomPersistence", at = @At("HEAD"), cancellable = true)
    public void requiresCustomPersistence(CallbackInfoReturnable<Boolean> callback) {
        if (this.level().getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE)) return;
        if (Mob.class.cast(this) instanceof Animal) {
            if (RespawningAnimals.CONFIG.get(ServerConfig.class).animalBlacklist.contains(this.getType())) {
                Player nearestPlayer = this.level().getNearestPlayer(this, -1.0);
                if (!this.removeWhenFarAway(nearestPlayer != null ? nearestPlayer.distanceToSqr(this) : Double.MAX_VALUE)) {
                    callback.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    public void finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> callback) {
        if (!RespawningAnimals.CONFIG.get(ServerConfig.class).summonedMobPersistence) return;
        // affects all mobs, not just animals
        if (mobSpawnType == MobSpawnType.COMMAND || mobSpawnType == MobSpawnType.SPAWN_EGG || mobSpawnType == MobSpawnType.DISPENSER) {
            this.setPersistenceRequired();
        }
    }
}
