package fuzs.respawninganimals.mixin;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.respawninganimals.world.entity.SpawnTypeMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Higher priority, so serialization happens after Puzzles Lib, replacing the entry which will hold more valid information.
 */
@Mixin(value = Mob.class, priority = 1200)
abstract class MobFabricMixin extends LivingEntity implements SpawnTypeMob {
    @Unique
    @Nullable
    private MobSpawnType respawninganimals$spawnType;

    protected MobFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    public void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> callback) {
        this.respawninganimals$spawnType = reason;
    }

    @Override
    @Nullable
    public final MobSpawnType respawninganimals$getSpawnType() {
        return this.respawninganimals$spawnType;
    }

    @Override
    public void respawninganimals$setSpawnType(@Nullable MobSpawnType mobSpawnType) {
        this.respawninganimals$spawnType = mobSpawnType;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo callback) {
        if (this.respawninganimals$spawnType != null) {
            // use Puzzles Lib key, so we can replace the value, while at the same time making Puzzles Lib use the better value on reading the data
            String key = PuzzlesLib.id("spawn_type").toString();
            compound.putString(key, this.respawninganimals$spawnType.name());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo callback) {
        // use Puzzles Lib key instead of custom key since it's more likely this key is already present with the data we need
        String key = PuzzlesLib.id("spawn_type").toString();
        if (compound.contains(key)) {
            try {
                this.respawninganimals$spawnType = MobSpawnType.valueOf(compound.getString(key));
            } catch (Exception exception) {
                compound.remove(key);
            }
        }
    }
}
