package fuzs.respawninganimals.core;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

public final class ForgeAbstractions implements CommonAbstractions {

    @Override
    public @Nullable MobSpawnType getMobSpawnType(Mob mob) {
        return mob.getSpawnType();
    }
}
