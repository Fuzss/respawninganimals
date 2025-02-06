package fuzs.respawninganimals.neoforge.mixin.accessor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobNeoForgeAccessor {

    @Accessor(value = "spawnType", remap = false)
    void respawninganimals$setSpawnType(@Nullable MobSpawnType spawnType);
}
