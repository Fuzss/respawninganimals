package fuzs.respawninganimals.handler;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.respawninganimals.init.ModRegistry;
import fuzs.respawninganimals.mixin.accessor.MobCategoryAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AnimalSpawningHandler {
    private static final Set<MobSpawnType> VOLATILE_SPAWN_TYPES = Set.of(MobSpawnType.NATURAL, MobSpawnType.CHUNK_GENERATION, MobSpawnType.COMMAND, MobSpawnType.SPAWN_EGG, MobSpawnType.DISPENSER);

    public static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        if (level.dimension() == Level.OVERWORLD) setCreatureAttributes(level.getGameRules());
    }

    public static EventResult onCheckMobDespawn(Mob mob, ServerLevel level) {
        return shouldHandleMobDespawning(mob, level) ? checkDespawn(mob) : EventResult.PASS;
    }

    private static EventResult checkDespawn(Mob mob) {
        // copied from Mob::checkDespawn, so we can run it manually for the creature mob category
        Player player = mob.level().getNearestPlayer(mob, -1.0);
        if (player != null) {
            double distanceToSqr = player.distanceToSqr(mob);
            int despawnDistance = mob.getType().getCategory().getDespawnDistance();
            if (distanceToSqr > despawnDistance * despawnDistance) {
                return EventResult.ALLOW;
            }
            int noDespawnDistance = mob.getType().getCategory().getNoDespawnDistance();
            if (mob.getNoActionTime() > 600 && mob.getRandom().nextInt(800) == 0 && distanceToSqr > noDespawnDistance * noDespawnDistance) {
                return EventResult.ALLOW;
            } else {
                // since this involves random don't let vanilla run again, we covered everything
                return EventResult.DENY;
            }
        }
        return EventResult.PASS;
    }

    public static EventResult onEntityLoad(Entity entity, ServerLevel level) {
        trySetMobPersistence(entity, level);
        return EventResult.PASS;
    }

    public static EventResult onEntitySpawn(Entity entity, ServerLevel level, @Nullable MobSpawnType spawnType) {
        // don't spawn mobs during chunk generation which we would remove again anyway since they are certainly too far from the player
        if (entity instanceof Mob mob && shouldAffectMob(mob, level) && spawnType == MobSpawnType.CHUNK_GENERATION) {
            return EventResult.INTERRUPT;
        }
        trySetMobPersistence(entity, level);
        return EventResult.PASS;
    }

    private static void trySetMobPersistence(Entity entity, ServerLevel level) {
        // find all mobs that would count towards the creature mob cap and therefore would hinder the spawn cycle from spawning new animals
        // making them persistent prevents counting towards the mob cap, otherwise this doesn't really have any implications for us since we ignore those spawn types anyway
        // in vanilla if the mod were to be removed this also has no consequences
        if (entity instanceof Mob mob && mob.getType().getCategory() == MobCategory.CREATURE) {
            MobSpawnType spawnType = CommonAbstractions.INSTANCE.getMobSpawnType(mob);
            if (spawnType == null || !VOLATILE_SPAWN_TYPES.contains(spawnType)) {
                mob.setPersistenceRequired();
            }
        }
    }

    public static boolean shouldHandleMobDespawning(Mob mob, Level level) {
        if (!shouldAffectMob(mob, level)) return false;
        MobSpawnType spawnType = CommonAbstractions.INSTANCE.getMobSpawnType(mob);
        return spawnType != null && VOLATILE_SPAWN_TYPES.contains(spawnType);
    }

    public static boolean shouldAffectMob(Mob mob, Level level) {
        if (level.getGameRules().getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE) || mob.getType().is(ModRegistry.PERSISTENT_ANIMALS_ENTITY_TYPE_TAG)) return false;
        // make sure to test if mob can actually never despawn (based on distance at least)
        return mob.getType().getCategory() == MobCategory.CREATURE && !mob.removeWhenFarAway(Double.MAX_VALUE);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void setCreatureAttributes(GameRules gameRules) {
        boolean persistentAnimals = gameRules.getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE);
        // this setting removes a 400 tick cooldown between spawn cycles, only creatures have this, all other categories don't
        ((MobCategoryAccessor) (Object) MobCategory.CREATURE).respawninganimals$setIsPersistent(persistentAnimals);
        // increase to 18 by default to be more similar to beta era spawning mechanics
        ((MobCategoryAccessor) (Object) MobCategory.CREATURE).respawninganimals$setMax(persistentAnimals ? 10 : gameRules.getInt(ModRegistry.ANIMAL_MOB_CAP_GAME_RULE));
    }
}
