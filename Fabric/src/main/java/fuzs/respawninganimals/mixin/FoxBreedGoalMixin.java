package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.api.event.entity.living.BabyEntitySpawnCallback;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.world.entity.animal.Fox$FoxBreedGoal")
public abstract class FoxBreedGoalMixin extends BreedGoal {

    public FoxBreedGoalMixin(Animal animal, double d) {
        super(animal, d);
    }

    @ModifyVariable(method = "breed", at = @At("STORE"))
    public AgeableMob breed$modify$store(@Nullable Fox child) {
        AgeableMob newChild = BabyEntitySpawnCallback.EVENT.invoker().onBabyEntitySpawn(this.animal, this.partner, child);
        if (newChild == null && child != null) {
            // reset the "inLove" state for the animals
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
        }
        return newChild;
    }
}
