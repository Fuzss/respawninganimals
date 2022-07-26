package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.api.event.entity.living.BabyEntitySpawnCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Animal.class)
public abstract class FabricAnimalMixin extends AgeableMob {

    protected FabricAnimalMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void resetLove();

    @ModifyVariable(method = "spawnChildFromBreeding", at = @At("STORE"))
    public AgeableMob spawnChildFromBreeding$modify$store(@Nullable AgeableMob child, ServerLevel serverLevel, Animal parentB) {
        AgeableMob newChild = BabyEntitySpawnCallback.EVENT.invoker().onBabyEntitySpawn(this, parentB, child);
        if (newChild == null && child != null) {
            // reset the "inLove" state for the animals
            this.setAge(6000);
            parentB.setAge(6000);
            this.resetLove();
            parentB.resetLove();
        }
        return newChild;
    }
}
