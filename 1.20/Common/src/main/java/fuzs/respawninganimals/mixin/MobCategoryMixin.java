package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public abstract class MobCategoryMixin {

    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    public void getMaxInstancesPerChunk$inject$head(CallbackInfoReturnable<Integer> callback) {
        if ((Object) this == MobCategory.CREATURE) callback.setReturnValue(RespawningAnimals.CONFIG.get(ServerConfig.class).maxAnimalNumber);
    }
}
