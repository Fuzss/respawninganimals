package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.RespawnableAnimals;
import com.mojang.serialization.DynamicLike;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
@Mixin(GameRules.class)
public abstract class GameRulesMixin {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Shadow
    @Final
    private Map<GameRules.RuleKey<?>, GameRules.RuleValue<?>> rules;

    @Inject(method = "<init>(Lcom/mojang/serialization/DynamicLike;)V", at = @At("TAIL"))
    public void init(DynamicLike<?> dynamic, CallbackInfo callbackInfo) {

        Optional<String> result = dynamic.get(RespawnableAnimals.PERSISTENT_ANIMALS.getName()).asString().result();
        if (!result.isPresent()) {

            ((GameRules.BooleanValue) this.rules.get(RespawnableAnimals.PERSISTENT_ANIMALS)).set(true, null);
        }
    }

}
