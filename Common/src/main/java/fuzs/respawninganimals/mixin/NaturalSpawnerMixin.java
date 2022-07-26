package fuzs.respawninganimals.mixin;

import com.google.common.collect.Lists;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.init.ModRegistry;
import fuzs.respawninganimals.mixin.accessor.SpawnStateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Inject(method = "spawnForChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private static void spawnForChunk(ServerLevel serverLevel, LevelChunk levelChunk, NaturalSpawner.SpawnState spawnState, boolean spawnCreatures, boolean spawnMonsters, boolean creatureCooldown, CallbackInfo callback) {
        // remove 400 tick delay for spawning animals
        if (!creatureCooldown && spawnCreatures) {
            if (!serverLevel.getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE) && ((SpawnStateAccessor) spawnState).callCanSpawnForCategory(MobCategory.CREATURE, levelChunk.getPos())) {
                spawnCategoryForChunk(MobCategory.CREATURE, serverLevel, levelChunk, (entityType, blockPos, chunkAccess) -> ((SpawnStateAccessor) spawnState).callCanSpawn(entityType, blockPos, chunkAccess), (mob, chunkAccess1) -> ((SpawnStateAccessor) spawnState).callAfterSpawn(mob, chunkAccess1));
            }
        }
    }

    @Shadow
    private static void spawnCategoryForChunk(MobCategory mobCategory, ServerLevel serverLevel, LevelChunk levelChunk, NaturalSpawner.SpawnPredicate spawnPredicate, NaturalSpawner.AfterSpawnCallback afterSpawnCallback) {
        throw new IllegalStateException();
    }

    @Inject(method = "getRandomSpawnMobAt", at = @At("TAIL"), cancellable = true)
    private static void getRandomSpawnMobAt$inject$tail(ServerLevel serverLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, RandomSource randomSource, BlockPos blockPos, CallbackInfoReturnable<Optional<MobSpawnSettings.SpawnerData>> callback) {
        callback.getReturnValue().ifPresent(data -> {
            // prevent blacklisted animals from being respawned to prevent them from spawning endlessly as they are also blacklisted from counting towards the mob cap
            // this is not a good solution, but I couldn't think of any other way
            if (RespawningAnimals.CONFIG.get(ServerConfig.class).animalBlacklist.contains(data.type)) {
                Holder<Biome> holder = serverLevel.getBiome(blockPos);
                List<MobSpawnSettings.SpawnerData> spawnerData = Lists.newArrayList(mobsAt(serverLevel, structureManager, chunkGenerator, mobCategory, blockPos, holder).unwrap());
                spawnerData.removeIf(data1 -> RespawningAnimals.CONFIG.get(ServerConfig.class).animalBlacklist.contains(data1.type));
                Optional<MobSpawnSettings.SpawnerData> optionalSpawnerData = WeightedRandomList.create(spawnerData).getRandom(randomSource);
                callback.setReturnValue(optionalSpawnerData);
            }
        });
    }

    @Shadow
    private static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel serverLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, BlockPos blockPos, @Nullable Holder<Biome> holder) {
        throw new IllegalStateException();
    }
}
