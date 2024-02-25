package fuzs.respawninganimals.world.entity;

import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

/**
 * Copied from Puzzles Lib since the 1.20.1 version is now only receiving bugfixes. Need this to have an additional
 * setter.
 * <p>
 * With all the extra mixins this provides a workaround for some mods rarely not calling super on Mob::finalizeSpawn where this
 * field is set by Puzzles Lib (like Forge does). This runs before Puzzles Lib (like Forge) sets the field, but when it
 * does this is just overridden again.
 */
public interface SpawnTypeMob {

    @Nullable MobSpawnType respawninganimals$getSpawnType();

    void respawninganimals$setSpawnType(@Nullable MobSpawnType mobSpawnType);
}
