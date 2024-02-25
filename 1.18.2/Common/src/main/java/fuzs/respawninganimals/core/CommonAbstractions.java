package fuzs.respawninganimals.core;

import fuzs.respawninganimals.world.entity.SpawnTypeMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

public interface CommonAbstractions {

    static @Nullable MobSpawnType getMobSpawnType(Mob mob) {
        return ((SpawnTypeMob) mob).respawninganimals$getSpawnType();
    }
}
