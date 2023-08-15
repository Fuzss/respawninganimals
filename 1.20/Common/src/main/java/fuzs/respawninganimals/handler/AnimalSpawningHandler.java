package fuzs.respawninganimals.handler;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AnimalSpawningHandler {

    public static EventResult onAnimalTame(Animal animal, Player player) {
        // enable persistence for animals that have been tamed (cats, ocelots, wolves, and all horse types including llamas)
        animal.setPersistenceRequired();
        return EventResult.PASS;
    }

    public static EventResult onLivingTick(LivingEntity entity) {
        // make animals in love persistent
        if (entity instanceof Animal animal && animal.isInLove()) animal.setPersistenceRequired();
        return EventResult.PASS;
    }

    public static EventResult onStartRiding(Level level, Entity rider, Entity vehicle) {
        // make mobs the player has ridden persistent
        if (vehicle instanceof Mob mob) mob.setPersistenceRequired();
        return EventResult.PASS;
    }

    public static EventResult onCheckMobDespawn(Mob mob, ServerLevel level) {
        if (level.getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE) || mob.getType().is(ModRegistry.PERSISTENT_ANIMALS_ENTITY_TYPE_TAG)) return EventResult.PASS;
        // TODO replace this with check for mobs from world gen data that can actually respawn
        return mob.getType().getCategory() == MobCategory.CREATURE && !mob.removeWhenFarAway(Double.MAX_VALUE) ? checkDespawn(mob) : EventResult.PASS;
    }

    public static EventResult checkDespawn(Mob mob) {
        Entity entity = mob.level().getNearestPlayer(mob, -1.0);
        if (entity != null) {
            double distanceToSqr = entity.distanceToSqr(mob);
            int despawnDistance = mob.getType().getCategory().getDespawnDistance();
            if (distanceToSqr > despawnDistance * despawnDistance) {
                return EventResult.ALLOW;
            }
            int noDespawnDistance = mob.getType().getCategory().getNoDespawnDistance();
            if (mob.getNoActionTime() > 600 && mob.getRandom().nextInt(800) == 0 && distanceToSqr > noDespawnDistance * noDespawnDistance) {
                return EventResult.ALLOW;
            } else {
                return EventResult.DENY;
            }
        }
        return EventResult.PASS;
    }

    public static EventResult onEntitySpawn(Entity entity, ServerLevel level, @Nullable MobSpawnType spawnType) {
        // make skeleton horse spawned as trap persistent by default
        // only really needed for single one spawned with lightning bolt, the ones from activating the trap are persistent by default for some reason
        if (entity instanceof SkeletonHorse skeletonHorse && skeletonHorse.isTrap()) {
            skeletonHorse.setPersistenceRequired();
        }
        // make trader llamas spawned together with a wandering trader persistent
        if (entity instanceof TraderLlama traderLlama && spawnType == MobSpawnType.EVENT) {
            traderLlama.setPersistenceRequired();
        }
        // make freshly born baby animals persistent
        if (entity instanceof AgeableMob ageableMob && ageableMob.getAge() < 0) {
            ageableMob.setPersistenceRequired();
        }

        // maybe better handle with tag, also tadpole
        if (entity instanceof Bee bee) bee.setPersistenceRequired();
        if (entity instanceof Tadpole tadpole) tadpole.setPersistenceRequired();

        if (spawnType == MobSpawnType.CHUNK_GENERATION && entity instanceof Mob mob && shouldHandleMobDespawning(mob, level, false)) {
            return EventResult.INTERRUPT;
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

    public static boolean shouldHandleMobDespawning(Mob mob, Level level, boolean testSpawnType) {
        if (level.getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE) || mob.getType().is(ModRegistry.PERSISTENT_ANIMALS_ENTITY_TYPE_TAG)) return false;
        MobSpawnType mobSpawnType = CommonAbstractions.INSTANCE.getMobSpawnType(mob);
        if (testSpawnType && mobSpawnType != MobSpawnType.NATURAL && mobSpawnType != MobSpawnType.CHUNK_GENERATION) return false;
        return mob.getType().getCategory() == MobCategory.CREATURE && !mob.removeWhenFarAway(Double.MAX_VALUE);
    }
}
