package com.fuzs.respawnableanimals.mixin.accessor;

import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HurtByTargetGoal.class)
public interface IHurtByTargetGoalAccessor {

    @Accessor
    void setEntityCallsForHelp(boolean entityCallsForHelp);

}
