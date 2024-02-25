package fuzs.respawninganimals.mixin.accessor;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityType.class)
public interface EntityTypeAccessor {

    @Accessor("category")
    @Mutable
    void respawninganimals$setCategory(MobCategory category);
}
