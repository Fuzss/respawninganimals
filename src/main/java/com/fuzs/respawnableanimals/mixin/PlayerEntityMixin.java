package com.fuzs.respawnableanimals.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {

        super(type, worldIn);
    }

    @Override
    public boolean startRiding(@Nonnull Entity entityIn, boolean force) {

        boolean flag = super.startRiding(entityIn, force);
        if (flag && entityIn instanceof AnimalEntity) {

            // make all mobs ridden by player persistent
            ((AnimalEntity) entityIn).enablePersistence();
        }

        return flag;
    }

}