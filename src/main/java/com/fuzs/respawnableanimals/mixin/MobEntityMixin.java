package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import com.fuzs.respawnableanimals.common.element.AnimalsElement;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
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
    public abstract void enablePersistence();

    @Redirect(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/MobEntity;canDespawn(D)Z"))
    public boolean canAnimalDespawn(MobEntity entity, double distanceToClosestPlayer) {

        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        return AnimalsElement.canAnimalDespawn(element, entity, distanceToClosestPlayer);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "preventDespawn", at = @At("HEAD"), cancellable = true)
    public void preventDespawn(CallbackInfoReturnable<Boolean> callbackInfo) {

        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        MobEntity entity = (MobEntity) (Object) this;
        if (element.isEnabled() && !this.world.getGameRules().getBoolean(AnimalsElement.PERSISTENT_ANIMALS) && entity instanceof AnimalEntity) {

            if (element.animalBlacklist.contains(entity.getType()) && canDespawnBePrevented(entity)) {

                callbackInfo.setReturnValue(true);
            }
        }
    }

    @Unique
    private static boolean canDespawnBePrevented(MobEntity entity) {

        Entity closestPlayer = entity.world.getClosestPlayer(entity, -1.0);
        if (closestPlayer != null) {

            double distanceToPlayer = entity.getDistanceSq(entity);
            return !entity.canDespawn(distanceToPlayer);
        }

        return true;
    }

    @Inject(method = "onInitialSpawn", at = @At("HEAD"))
    public void onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag, CallbackInfoReturnable<ILivingEntityData> callbackInfo) {

        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        if (element.isEnabled() && element.summonedMobPersistence) {

            // affects all mobs, not just animals
            if (reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.DISPENSER) {

                this.enablePersistence();
            }
        }
    }

}
