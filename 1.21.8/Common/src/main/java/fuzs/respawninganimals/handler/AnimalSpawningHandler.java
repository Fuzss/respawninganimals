package fuzs.respawninganimals.handler;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.util.v1.EntityHelper;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AnimalSpawningHandler {
    private static final Set<EntitySpawnReason> PERSISTENT_SPAWN_TYPES = Set.of(EntitySpawnReason.STRUCTURE,
            EntitySpawnReason.BREEDING,
            EntitySpawnReason.TRIGGERED,
            EntitySpawnReason.BUCKET);

    public static void onServerStarted(MinecraftServer minecraftServer) {
        setCreatureAttributes(minecraftServer.getGameRules());
    }

    public static void setCreatureAttributes(GameRules gameRules) {
        boolean persistentAnimals = gameRules.getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE);
        // this setting removes a 400 tick cooldown between spawn cycles, only creatures have this, all other categories don't
        MobCategory.CREATURE.isPersistent = persistentAnimals;
        // increase to 18 by default to be more similar to beta era spawning mechanics
        MobCategory.CREATURE.max = persistentAnimals ? 10 : gameRules.getInt(ModRegistry.ANIMAL_MOB_CAP_GAME_RULE);
    }

    public static EventResult onCheckMobDespawn(Mob mob, ServerLevel level) {
        if (isAllowedToDespawn(mob, level.getGameRules())) {
            // copied from Mob::checkDespawn, so we can run it manually for the creature mob category
            Player player = mob.level().getNearestPlayer(mob, -1.0);
            if (player != null) {
                double distanceToSqr = player.distanceToSqr(mob);
                int despawnDistance = mob.getType().getCategory().getDespawnDistance();
                if (distanceToSqr > despawnDistance * despawnDistance) {
                    return EventResult.ALLOW;
                }
                int noDespawnDistance = mob.getType().getCategory().getNoDespawnDistance();
                if (mob.getNoActionTime() > 600 && mob.getRandom().nextInt(800) == 0
                        && distanceToSqr > noDespawnDistance * noDespawnDistance) {
                    return EventResult.ALLOW;
                } else {
                    // since this involves random don't let vanilla run again, we covered everything
                    return EventResult.DENY;
                }
            }
        }
        return EventResult.PASS;
    }

    public static boolean isAllowedToDespawn(Mob mob, @Nullable GameRules gameRules) {
        if (isAnimalDespawningAllowed(mob.getType(), gameRules, mob.getType().getCategory())) {
            EntitySpawnReason entitySpawnReason = EntityHelper.getMobSpawnReason(mob);
            return entitySpawnReason != null && !PERSISTENT_SPAWN_TYPES.contains(entitySpawnReason);
        } else {
            return false;
        }
    }

    public static boolean isAnimalDespawningAllowed(EntityType<?> entityType, @Nullable GameRules gameRules, MobCategory mobCategory) {
        if (gameRules != null && gameRules.getBoolean(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE)) return false;
        if (entityType.is(ModRegistry.PERSISTENT_ANIMALS_ENTITY_TYPE_TAG)) return false;
        return mobCategory == MobCategory.CREATURE;
    }

    public static EventResult onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isNewlySpawned) {
        if (isNewlySpawned) {
            @Nullable EntitySpawnReason entitySpawnReason = EntityHelper.getMobSpawnReason(entity);
            // don't spawn mobs during chunk generation which we would remove again anyway since they are certainly too far from the player
            if (entitySpawnReason == EntitySpawnReason.CHUNK_GENERATION) {
                // chunk generation only runs for the creature type, so we can safely fix the type if necessary
                applyCorrectMobCategory(entity.getType());
                if (isAnimalDespawningAllowed(entity.getType(), serverLevel.getGameRules(), MobCategory.CREATURE)) {
                    return EventResult.INTERRUPT;
                }
            }
        }
        // make existing mobs in the world persistent to help with compat for worlds that have been used without the mod before
        setPersistenceForPersistentAnimal(entity);
        return EventResult.PASS;
    }

    private static void setPersistenceForPersistentAnimal(Entity entity) {
        // find all mobs that would count towards the creature mob cap and therefore would hinder the spawn cycle from spawning new animals
        // making them persistent prevents counting towards the mob cap, otherwise this doesn't really have any implications for us since we ignore those spawn types anyway
        // in vanilla if the mod were to be removed, this also has no consequences
        if (entity instanceof Mob mob && mob.getType().getCategory() == MobCategory.CREATURE) {
            // do not check the game rule, in case it is toggled during gameplay
            if (!mob.isPersistenceRequired() && !isAllowedToDespawn(mob, null)) {
                mob.setPersistenceRequired();
            }
        }
    }

    private static void applyCorrectMobCategory(EntityType<?> entityType) {
        // an entity type must have the same mob category set that is used for spawning the entity naturally (via mob spawn type natural or chunk generation)
        // otherwise the entity does not count towards its own spawn cap, which can lead to infinite spawns
        // for creatures this unfortunately usually goes unnoticed since the spawning cycle never runs as there are usually enough vanilla animals in the world to fill up the cap
        if (entityType.getCategory() != MobCategory.CREATURE) {
            ResourceLocation resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            Optional<String> issues = ModLoaderEnvironment.INSTANCE.getModContainer(resourceLocation.getNamespace())
                    .map(modContainer -> modContainer.getContactTypes().get("issues"));
            String modName = ModLoaderEnvironment.INSTANCE.getModContainer(resourceLocation.getNamespace())
                    .map(ModContainer::getDisplayName)
                    .orElse(resourceLocation.getNamespace());
            RespawningAnimals.LOGGER.warn(
                    "Mismatched spawn type for {}! Mob is registered as {}, but spawning as {}. Report this to the author of {}"
                            + (issues.map(s -> " at " + s).orElse("")) + ".",
                    resourceLocation,
                    entityType.getCategory(),
                    MobCategory.CREATURE,
                    modName);
            entityType.category = MobCategory.CREATURE;
        }
    }

    public static void onGatherPotentialSpawns(ServerLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, BlockPos blockPos, List<Weighted<MobSpawnSettings.SpawnerData>> mobs) {
        if (mobCategory == MobCategory.CREATURE) {
            Iterator<Weighted<MobSpawnSettings.SpawnerData>> iterator = mobs.iterator();
            while (iterator.hasNext()) {
                Weighted<MobSpawnSettings.SpawnerData> spawnerData = iterator.next();
                applyCorrectMobCategory(spawnerData.value().type());
                // prevent blacklisted animals from being respawned to prevent them from spawning endlessly since they also do not count towards the mob cap
                if (!isAnimalDespawningAllowed(spawnerData.value().type(),
                        level.getGameRules(),
                        MobCategory.CREATURE)) {
                    iterator.remove();
                }
            }
        }
    }
}
