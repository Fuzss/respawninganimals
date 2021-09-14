package fuzs.respawnableanimals.mixin;

import fuzs.respawnableanimals.RespawnableAnimals;
import fuzs.respawnableanimals.element.RespawnableAnimalsElement;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {

        super(type, worldIn);
    }

    @Shadow
    public abstract void setPersistenceRequired();

    @Redirect(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/MobEntity;removeWhenFarAway(D)Z"))
    public boolean removeWhenFarAway(MobEntity entity, double distanceToClosestPlayer) {

        return RespawnableAnimalsElement.canAnimalDespawn(entity, distanceToClosestPlayer);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "requiresCustomPersistence", at = @At("HEAD"), cancellable = true)
    public void requiresCustomPersistence(CallbackInfoReturnable<Boolean> callbackInfo) {

        MobEntity entity = (MobEntity) (Object) this;
        if (!this.level.getGameRules().getBoolean(RespawnableAnimalsElement.PERSISTENT_ANIMALS) && entity instanceof AnimalEntity) {

            if (((RespawnableAnimalsElement) RespawnableAnimals.RESPAWNABLE_ANIMALS).animalBlacklist.contains(entity.getType()) && canDespawnBePrevented(entity)) {

                callbackInfo.setReturnValue(true);
            }
        }
    }

    @Unique
    private static boolean canDespawnBePrevented(MobEntity entity) {

        Entity closestPlayer = entity.level.getNearestPlayer(entity, -1.0);
        if (closestPlayer != null) {

            double distanceToPlayer = entity.distanceToSqr(entity);
            return !entity.removeWhenFarAway(distanceToPlayer);
        }

        return true;
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    public void finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag, CallbackInfoReturnable<ILivingEntityData> callbackInfo) {

        if (((RespawnableAnimalsElement) RespawnableAnimals.RESPAWNABLE_ANIMALS).summonedMobPersistence) {

            // affects all mobs, not just animals
            if (reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.DISPENSER) {

                this.setPersistenceRequired();
            }
        }
    }

}
