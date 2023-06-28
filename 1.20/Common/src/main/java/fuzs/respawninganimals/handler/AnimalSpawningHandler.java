package fuzs.respawninganimals.handler;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class AnimalSpawningHandler {

    public static EventResult onBabyEntitySpawn(Mob animal, Mob partner, MutableValue<AgeableMob> child) {
        // make freshly born baby animals persistent
        if (child.get() != null) child.get().setPersistenceRequired();
        // environmental makes pigs spawn multiple piglets
        if (animal instanceof Pig && ModLoaderEnvironment.INSTANCE.isModLoaded("environmental")) {
            animal.level().getEntitiesOfClass(Pig.class, animal.getBoundingBox(), AgeableMob::isBaby).forEach(Mob::setPersistenceRequired);
        }
        return EventResult.PASS;
    }

    public static EventResult onAnimalTame(Animal animal, Player player) {
        // enable persistence for animals that have been tamed (cats, ocelots, wolves, and all horse types including llamas)
        animal.setPersistenceRequired();
        return EventResult.PASS;
    }

    public static EventResult onEntityJoinLevel(Entity entity, ServerLevel level, @Nullable MobSpawnType spawnType) {
        // make skeleton horse spawned as trap persistent by default
        // only really needed for single one spawned with lightning bolt, the ones from activating the trap are persistent by default for some reason
        if (entity instanceof SkeletonHorse skeletonHorse && skeletonHorse.isTrap()) {
            skeletonHorse.setPersistenceRequired();
        }
        if (spawnType == MobSpawnType.CHUNK_GENERATION || spawnType == MobSpawnType.NATURAL) {
            if (entity instanceof Animal animal && entity.getType().getCategory() == MobCategory.CREATURE) {
                // prevent animals from being spawned on world creation, but exclude blacklisted animals
                if (!level.getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE) && !RespawningAnimals.CONFIG.get(ServerConfig.class).animalBlacklist.contains(entity.getType())) {
                    if (spawnType == MobSpawnType.CHUNK_GENERATION) {
                        return EventResult.INTERRUPT;
                    } else {
                        // prevent animals from being spawned when too far away from the closest player
                        double distanceToPlayer = getDistanceToPlayer(level, entity.getX(), entity.getY(), entity.getZ());
                        return canSpawn(level, animal, distanceToPlayer) ? EventResult.PASS : EventResult.INTERRUPT;
                    }
                }
            }
        }
        return EventResult.PASS;
    }

    private static double getDistanceToPlayer(ServerLevel serverLevel, double x, double y, double z) {
        Player nearestPlayer = serverLevel.getNearestPlayer(x, y, z, -1.0, false);
        return nearestPlayer != null ? nearestPlayer.distanceToSqr(x, y, z) : Double.MAX_VALUE;
    }

    private static boolean canSpawn(ServerLevel serverWorld, Mob mob, double distance) {
        if (distance > mob.getType().getCategory().getDespawnDistance() * mob.getType().getCategory().getDespawnDistance() && canAnimalDespawn(mob, distance)) {
            return false;
        } else {
            return mob.checkSpawnRules(serverWorld, MobSpawnType.NATURAL) && mob.checkSpawnObstruction(serverWorld);
        }
    }

    public static boolean canAnimalDespawn(Mob mob, double distanceToClosestPlayer) {
        // replace canDespawn call, as injecting into the base method directly is not sufficient as it is overridden by many sub classes
        // special behavior such as blacklist is handled in preventDespawn injector
        if (!mob.level().getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE)) {
            return mob instanceof Animal && mob.getType().getCategory() == MobCategory.CREATURE && (!(mob instanceof TamableAnimal) || !((TamableAnimal) mob).isTame()) || mob.removeWhenFarAway(distanceToClosestPlayer);
        }
        return mob.removeWhenFarAway(distanceToClosestPlayer);
    }
}
