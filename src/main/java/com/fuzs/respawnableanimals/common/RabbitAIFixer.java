package com.fuzs.respawnableanimals.common;

import com.fuzs.respawnableanimals.mixin.accessor.IGoalSelectorAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Set;
import java.util.stream.Collectors;

public class RabbitAIFixer {

    public static void addListener(IEventBus eventBus) {

        RabbitAIFixer rabbitAIFixer = new RabbitAIFixer();
        eventBus.addListener(rabbitAIFixer::onEntitySize);
        eventBus.addListener(rabbitAIFixer::onEntityJoinWorld);

        // increase rabbit health back to 10
        GlobalEntityTypeAttributes.put(EntityType.RABBIT, MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 10.0)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
                .create());
    }

    private void onEntitySize(final EntityEvent.Size evt) {

        if (evt.getEntity() instanceof RabbitEntity) {

            evt.setNewSize(EntitySize.flexible(0.6F, 0.7F));
        }
    }

    private void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        if (evt.getEntity() instanceof RabbitEntity) {

            RabbitEntity rabbitEntity = ((RabbitEntity) evt.getEntity());
            this.clearOldGoals(rabbitEntity);
            this.registerNewGoals(rabbitEntity);
        } else if (evt.getEntity() instanceof SheepEntity) {

            SheepEntity sheep = (SheepEntity) evt.getEntity();
            sheep.goalSelector.addGoal(4, new AvoidEntityGoal<>(sheep, WolfEntity.class, 10.0F, 1.25, 1.25));
        }
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
