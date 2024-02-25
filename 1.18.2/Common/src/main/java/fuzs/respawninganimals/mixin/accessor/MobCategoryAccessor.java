package fuzs.respawninganimals.mixin.accessor;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobCategory.class)
public interface MobCategoryAccessor {

    @Accessor("max")
    @Mutable
    void respawninganimals$setMax(int max);

    @Accessor("isPersistent")
    @Mutable
    void respawninganimals$setIsPersistent(boolean isPersistent);
}
