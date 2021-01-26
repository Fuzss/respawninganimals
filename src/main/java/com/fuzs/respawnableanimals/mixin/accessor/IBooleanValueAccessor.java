package com.fuzs.respawnableanimals.mixin.accessor;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.BooleanValue.class)
public interface IBooleanValueAccessor {

    @SuppressWarnings("unused")
    @Invoker
    static GameRules.RuleType<GameRules.BooleanValue> callCreate(boolean defaultValue) {

        throw new IllegalStateException();
    }

}
