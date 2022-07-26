package fuzs.respawninganimals.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NaturalSpawner.SpawnState.class)
public interface SpawnStateAccessor {

    @Invoker
    boolean callCanSpawn(EntityType<?> entityType, BlockPos blockPos, ChunkAccess chunkAccess);

    @Invoker
    void callAfterSpawn(Mob mob, ChunkAccess chunkAccess);

    @Invoker
    boolean callCanSpawnForCategory(MobCategory mobCategory, ChunkPos chunkPos);
}
