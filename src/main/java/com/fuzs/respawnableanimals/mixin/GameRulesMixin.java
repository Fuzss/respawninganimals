package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.element.AnimalsElement;
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

        // if the gamerule is not present (this is a world which has been loaded without the mod before) set value
        // to true instead of default false to prevent unwanted behavior such as animals vanishing from farms
        Optional<String> result = dynamic.get(AnimalsElement.PERSISTENT_ANIMALS.getName()).asString().result();
        if (!result.isPresent()) {

            ((GameRules.BooleanValue) this.rules.get(AnimalsElement.PERSISTENT_ANIMALS)).set(true, null);
        }
    }

}
