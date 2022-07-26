package fuzs.respawninganimals.element;

import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.side.ICommonElement;
import fuzs.puzzleslib.util.LoadedLocationList;
import fuzs.puzzleslib.util.PuzzlesUtil;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.init.ModRegistry;
import fuzs.respawninganimals.mixin.accessor.BooleanValueAccessor;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class RespawnableAnimalsElement extends AbstractElement implements ICommonElement {

    @Override
    public void constructCommon() {
        // low priority for environmental mod compat
        this.addListener(this::onBabyEntitySpawn, EventPriority.LOW);
        this.addListener(this::onAnimalTame);
        this.addListener(this::onPotentialSpawns);
        this.addListener(this::onEntityJoinWorld);
        this.addListener(this::onCheckSpawn);
    }

    private void onBabyEntitySpawn(final BabyEntitySpawnEvent evt) {

        // make freshly born baby animals persistent
        if (evt.getChild() != null) {

            evt.getChild().setPersistenceRequired();
        }

        // environmental makes pigs spawn multiple piglets
        if (ModList.get().isLoaded("environmental")) {

            MobEntity parent = evt.getParentA();
            if (parent instanceof PigEntity && evt.getParentB() instanceof PigEntity) {

                parent.level.getEntitiesOfClass(PigEntity.class, parent.getBoundingBox(), AgeableEntity::isBaby).forEach(MobEntity::setPersistenceRequired);
            }
        }
    }

    private void onAnimalTame(final AnimalTameEvent evt) {

        // enable persistence for animals that have been tamed (cats, ocelots, wolves, and all horse types including llamas)
        evt.getAnimal().setPersistenceRequired();
    }

    private void onPotentialSpawns(final WorldEvent.PotentialSpawns evt) {

        // prevent blacklisted animals from being respawned to prevent them from spawning endlessly as they are also blacklisted from counting towards the mob cap
        // this is not a good solution but I couldn't think of any other way
        evt.getList().removeIf(spawner -> RespawningAnimals.CONFIG.get(ServerConfig.class).animalBlacklist.contains(spawner.type));
    }

    private void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        // make skeleton horse spawned as trap persistent by default
        // only really needed for single one spawned with lightning bolt, the ones from activating the trap are persistent by default for some reason
        if (evt.getEntity() instanceof SkeletonHorseEntity && ((SkeletonHorseEntity) evt.getEntity()).isTrap()) {

            ((SkeletonHorseEntity) evt.getEntity()).setPersistenceRequired();
        }
    }

    private void onCheckSpawn(final LivingSpawnEvent.CheckSpawn evt) {

        if (!(evt.getWorld() instanceof IServerWorld)) {

            return;
        }

        ServerWorld serverWorld = ((IServerWorld) evt.getWorld()).getLevel();
        if (evt.getSpawnReason() == SpawnReason.CHUNK_GENERATION || evt.getSpawnReason() == SpawnReason.NATURAL) {

            if (evt.getEntity() instanceof AnimalEntity && evt.getEntity().getType().getCategory() == EntityClassification.CREATURE) {

                // prevent animals from being spawned on world creation, but exclude blacklisted animals
                if (!serverWorld.getGameRules().getBoolean(PERSISTENT_ANIMALS) && !RespawningAnimals.CONFIG.get(ServerConfig.class).animalBlacklist.contains(evt.getEntity().getType())) {

                    if (evt.getSpawnReason() == SpawnReason.CHUNK_GENERATION) {

                        evt.setResult(Event.Result.DENY);
                    } else {

                        // prevent animals from being spawned when too far away from the closest player
                        double distanceToClosestPlayer = getPlayerDistance(serverWorld, evt.getX(), evt.getY(), evt.getZ());
                        if (!this.canSpawn(serverWorld, (MobEntity) evt.getEntity(), distanceToClosestPlayer)) {

                            evt.setResult(Event.Result.DENY);
                        }
                    }
                }
            }
        }
    }

    private double getPlayerDistance(ServerLevel serverWorld, double x, double y, double z) {

        return serverWorld.getNearestPlayer(x, y, z, -1.0, false).distanceToSqr(x, y, z);
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

            return mob instanceof Animal && mob.getType().getCategory() == EntityClassification.CREATURE && (!(mob instanceof TamableAnimal) || !((TamableAnimal) mob).isTame()) || mob.removeWhenFarAway(distanceToClosestPlayer);
        }

        return mob.removeWhenFarAway(distanceToClosestPlayer);
    }

}
