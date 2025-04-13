package fuzs.respawninganimals.mixin;

import com.mojang.serialization.DynamicLike;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(GameRules.class)
abstract class GameRulesMixin {
    @Shadow
    @Final
    private Map<GameRules.Key<?>, GameRules.Value<?>> rules;

    @Inject(
            method = "<init>(Lnet/minecraft/world/flag/FeatureFlagSet;Lcom/mojang/serialization/DynamicLike;)V",
            at = @At("TAIL")
    )
    public void init(FeatureFlagSet enabledFeatures, DynamicLike<?> dynamic, CallbackInfo callback) {
        // if the game rule is not present (this is a world which has been loaded without the mod before) set value
        // to true instead of default false to prevent unwanted behavior such as animals vanishing from farms
        Optional<String> result = dynamic.get(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE.getId()).asString().result();
        if (result.isEmpty()) {
            ((GameRules.BooleanValue) this.rules.get(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE)).set(true, null);
        }
    }
}