package com.fuzs.respawnableanimals.common;

import com.fuzs.respawnableanimals.mixin.accessor.IGoalSelectorAccessor;
import com.fuzs.respawnableanimals.mixin.accessor.IHurtByTargetGoalAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RabbitAIFixer {

    public static void addListener(IEventBus eventBus) {

        RabbitAIFixer rabbitAIFixer = new RabbitAIFixer();
        eventBus.addListener(rabbitAIFixer::onEntityJoinWorld);
        eventBus.addListener(rabbitAIFixer::onLivingSetAttackTarget);

        // increase rabbit health back to normal animals health from 3
        GlobalEntityTypeAttributes.put(EntityType.RABBIT, MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 10.0)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
                .create());

        // reduce follow range down from a ridiculous 48
        GlobalEntityTypeAttributes.put(EntityType.BLAZE, MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23F)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0)
                .create());
    }

    private void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        if (evt.getEntity() instanceof RabbitEntity) {

            RabbitEntity rabbitEntity = ((RabbitEntity) evt.getEntity());
            this.clearOldGoals(rabbitEntity);
            this.registerNewGoals(rabbitEntity);
        } else if (evt.getEntity() instanceof SheepEntity) {

            // don't get eaten by wolves
            SheepEntity sheep = (SheepEntity) evt.getEntity();
            sheep.goalSelector.addGoal(4, new AvoidEntityGoal<>(sheep, WolfEntity.class, 10.0F, 1.25, 1.25));
        } else if (evt.getEntity() instanceof CreeperEntity || evt.getEntity() instanceof SkeletonEntity || evt.getEntity() instanceof SpiderEntity || evt.getEntity() instanceof WitchEntity || evt.getEntity() instanceof ZombieEntity) {

            // flee from exploding creepers
            MonsterEntity monsterEntity = (MonsterEntity) evt.getEntity();
            monsterEntity.goalSelector.addGoal(2, new AvoidEntityGoal<>(monsterEntity, CreeperEntity.class, 4.0F, 1.0, 2.0, this::isAboutToExplode));
        } else if (evt.getEntity() instanceof BlazeEntity) {

            BlazeEntity blazeEntity = (BlazeEntity) evt.getEntity();
            ((IGoalSelectorAccessor) blazeEntity.goalSelector).getGoals().stream()
                    .map(PrioritizedGoal::getGoal)
                    .filter(goal -> goal instanceof HurtByTargetGoal)
                    .findFirst()
                    .map(goal -> ((IHurtByTargetGoalAccessor) goal))
                    .ifPresent(goal -> goal.setEntityCallsForHelp(false));
        }
    }

    private void onLivingSetAttackTarget(final LivingSetAttackTargetEvent evt) {

        if (evt.getEntity() instanceof EndermanEntity) {

            final UUID attackingSpeedBoostId = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
            ModifiableAttributeInstance attribute = ((EndermanEntity) evt.getEntity()).getAttribute(Attributes.MOVEMENT_SPEED);
            if (attribute != null) {

                Optional.ofNullable(attribute.getModifier(attackingSpeedBoostId)).ifPresent(attribute::removeModifier);
            }
        }
    }

    private boolean isAboutToExplode(LivingEntity entity) {

        return entity instanceof CreeperEntity && (((CreeperEntity) entity).getCreeperState() > 0 || ((CreeperEntity) entity).hasIgnited());
    }

    private void clearOldGoals(RabbitEntity rabbitEntity) {

        if (rabbitEntity != null) {

            Set<Goal> goalsToRemove = ((IGoalSelectorAccessor) rabbitEntity.goalSelector).getGoals().stream()
                    .map(PrioritizedGoal::getGoal)
                    .filter(goal -> !(goal instanceof MoveToBlockGoal || goal instanceof MeleeAttackGoal || goal instanceof PanicGoal))
                    .collect(Collectors.toSet());

            goalsToRemove.forEach(rabbitEntity.goalSelector::removeGoal);
        }
    }

    private void registerNewGoals(RabbitEntity rabbitEntity) {

        if (rabbitEntity != null) {

            rabbitEntity.goalSelector.addGoal(0, new SwimGoal(rabbitEntity));
            rabbitEntity.goalSelector.addGoal(2, new BreedGoal(rabbitEntity, 1.1));
            rabbitEntity.goalSelector.addGoal(3, new TemptGoal(rabbitEntity, 1.33, Ingredient.fromItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
            if (rabbitEntity.getRabbitType() != 99) {

                rabbitEntity.goalSelector.addGoal(4, new RabbitAvoidEntityGoal<>(rabbitEntity, WolfEntity.class, 16.0F, 2.2, 2.2));
            }

            rabbitEntity.goalSelector.addGoal(6, new FollowParentGoal(rabbitEntity, 1.2));
            rabbitEntity.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(rabbitEntity, 1.1));
            rabbitEntity.goalSelector.addGoal(8, new LookAtGoal(rabbitEntity, PlayerEntity.class, 6.0F));
            rabbitEntity.goalSelector.addGoal(9, new LookRandomlyGoal(rabbitEntity));
        }
    }

    private static class RabbitAvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {

        private final RabbitEntity rabbit;

        public RabbitAvoidEntityGoal(RabbitEntity rabbit, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {

            super(rabbit, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
            this.rabbit = rabbit;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute() {

            return this.rabbit.getRabbitType() != 99 && super.shouldExecute();
        }

    }

}
