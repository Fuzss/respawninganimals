package fuzs.respawninganimals.handler;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class AnimalSpawningHandler {

    public void onBabyEntitySpawn(Mob parentA, Mob parentB, @Nullable AgeableMob child) {
        // make freshly born baby animals persistent
        if (child != null) child.setPersistenceRequired();
        // environmental makes pigs spawn multiple piglets
        if (CoreServices.ENVIRONMENT.isModLoaded("environmental") && parentA instanceof Pig) {
            parentA.level.getEntitiesOfClass(Pig.class, parentA.getBoundingBox(), AgeableMob::isBaby).forEach(Mob::setPersistenceRequired);
        }
    }

    public void onAnimalTame(Animal animal, Player tamer) {
        // enable persistence for animals that have been tamed (cats, ocelots, wolves, and all horse types including llamas)
        animal.setPersistenceRequired();
    }

    public void onEntityJoinWorld(Entity entity, Level level) {
        // make skeleton horse spawned as trap persistent by default
        // only really needed for single one spawned with lightning bolt, the ones from activating the trap are persistent by default for some reason
        if (entity instanceof SkeletonHorse skeletonHorse &&skeletonHorse.isTrap()) {
            skeletonHorse.setPersistenceRequired();
        }
    }

    public boolean onCheckSpawn(Mob mob, LevelAccessor level, double x, double y, double z, @Nullable BaseSpawner spawner, MobSpawnType spawnReason) {
        if (!(level instanceof ServerLevelAccessor levelAccessor)) return true;
        ServerLevel serverWorld = levelAccessor.getLevel();
        if (spawnReason == MobSpawnType.CHUNK_GENERATION || spawnReason == MobSpawnType.NATURAL) {
            if (mob instanceof Animal && mob.getType().getCategory() == MobCategory.CREATURE) {
                // prevent animals from being spawned on world creation, but exclude blacklisted animals
                if (!serverWorld.getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE) && !RespawningAnimals.CONFIG.get(ServerConfig.class).animalBlacklist.contains(mob.getType())) {
                    if (spawnReason == MobSpawnType.CHUNK_GENERATION) {
                        return false;
                    } else {
                        // prevent animals from being spawned when too far away from the closest player
                        double distanceToClosestPlayer = this.getPlayerDistance(serverWorld, x, y, z);
                        return this.canSpawn(serverWorld, mob, distanceToClosestPlayer);
                    }
                }
            }
        }
        return true;
    }

    private double getPlayerDistance(ServerLevel serverLevel, double x, double y, double z) {
        return serverLevel.getNearestPlayer(x, y, z, -1.0, false).distanceToSqr(x, y, z);
    }

    private boolean canSpawn(ServerLevel serverWorld, Mob entity, double distanceToClosestPlayer) {
        if (distanceToClosestPlayer > entity.getType().getCategory().getDespawnDistance() * entity.getType().getCategory().getDespawnDistance() && canAnimalDespawn(entity, distanceToClosestPlayer)) {
            return false;
        } else {
            return entity.checkSpawnRules(serverWorld, MobSpawnType.NATURAL) && entity.checkSpawnObstruction(serverWorld);
        }
    }

    public static boolean canAnimalDespawn(Mob mob, double distanceToClosestPlayer) {
        // replace canDespawn call, as injecting into the base method directly is not sufficient as it is overridden by many sub classes
        // special behavior such as blacklist is handled in preventDespawn injector
        if (!mob.level.getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE)) {
            return mob instanceof Animal && mob.getType().getCategory() == MobCategory.CREATURE && (!(mob instanceof TamableAnimal) || !((TamableAnimal) mob).isTame()) || mob.removeWhenFarAway(distanceToClosestPlayer);
        }
        return mob.removeWhenFarAway(distanceToClosestPlayer);
    }
}
