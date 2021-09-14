package fuzs.respawnableanimals.element;

import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.side.ICommonElement;
import fuzs.puzzleslib.util.LoadedLocationList;
import fuzs.puzzleslib.util.PuzzlesUtil;
import fuzs.respawnableanimals.mixin.accessor.BooleanValueAccessor;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
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

    public static final GameRules.RuleKey<GameRules.BooleanValue> PERSISTENT_ANIMALS = GameRules.register("persistentAnimals", GameRules.Category.SPAWNING, BooleanValueAccessor.callCreate(false));
    
    public Set<EntityType<?>> animalBlacklist;
    public int maxAnimalNumber;
    public boolean summonedMobPersistence;

    @Override
    public String[] getDescription() {
        
        return new String[]{"Animals are no longer persistent by default, making them spawn just like monsters."};
    }

    @Override
    protected boolean isPersistent() {

        return true;
    }

    @Override
    public void constructCommon() {

        this.addListener(this::onBabyEntitySpawn, EventPriority.LOW);
        this.addListener(this::onAnimalTame);
        this.addListener(this::onPotentialSpawns);
        this.addListener(this::onEntityJoinWorld);
        this.addListener(this::onCheckSpawn);
    }

    @Override
    public void setupCommonConfig(OptionsBuilder builder) {

        builder.define("Animal Blacklist", PuzzlesUtil.make(new LoadedLocationList(), list -> list.add(new ResourceLocation("whisperwoods","hirschgeist")))).comment("Blacklist animals which will never despawn.", EntryCollectionBuilder.CONFIG_STRING).sync(v -> this.animalBlacklist = new EntryCollectionBuilder<>(ForgeRegistries.ENTITIES).buildEntrySet(v));
        builder.define("Animal Mob Cap", 18).comment("Constant for determining when to stop spawning animals in a world. Normally set to 10, monster constant is 70 for comparison. 18 is chosen to mimic spawning mechanics of the Beta era.").sync(v -> this.maxAnimalNumber = v);
        builder.define("Summoned Mob Persistence", false).comment("Make all mobs (not just animals) automatically persistent when spawned using the \"/summon\" command, a spawn egg or a dispenser.").sync(v -> this.summonedMobPersistence = v);
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
        evt.getList().removeIf(spawner -> this.animalBlacklist.contains(spawner.type));
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
                if (!serverWorld.getGameRules().getBoolean(PERSISTENT_ANIMALS) && !this.animalBlacklist.contains(evt.getEntity().getType())) {

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

    private double getPlayerDistance(ServerWorld serverWorld, double x, double y, double z) {

        PlayerEntity playerentity = serverWorld.getNearestPlayer(x, y, z, -1.0, false);

        // can't be null as the used event wouldn't have been called then
        assert playerentity != null;
        return playerentity.distanceToSqr(x, y, z);
    }

    private boolean canSpawn(ServerWorld serverWorld, MobEntity entity, double distanceToClosestPlayer) {

        if (distanceToClosestPlayer > entity.getType().getCategory().getDespawnDistance() * entity.getType().getCategory().getDespawnDistance() && canAnimalDespawn(entity, distanceToClosestPlayer)) {

            return false;
        } else {

            return entity.checkSpawnRules(serverWorld, SpawnReason.NATURAL) && entity.checkSpawnObstruction(serverWorld);
        }
    }

    public static boolean canAnimalDespawn(MobEntity entity, double distanceToClosestPlayer) {

        // replace canDespawn call, as injecting into the base method directly is not sufficient as it is overridden by many sub classes
        // special behavior such as blacklist is handled in preventDespawn injector
        if (!entity.level.getGameRules().getBoolean(PERSISTENT_ANIMALS)) {

            return entity instanceof AnimalEntity && entity.getType().getCategory() == EntityClassification.CREATURE || entity.removeWhenFarAway(distanceToClosestPlayer);
        }

        return entity.removeWhenFarAway(distanceToClosestPlayer);
    }

}
