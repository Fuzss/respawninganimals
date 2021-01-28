package com.fuzs.respawnableanimals.common.element;

import com.fuzs.puzzleslib_ra.config.deserialize.EntryCollectionBuilder;
import com.fuzs.puzzleslib_ra.element.AbstractElement;
import com.fuzs.puzzleslib_ra.element.side.ICommonElement;
import com.fuzs.respawnableanimals.mixin.accessor.IBooleanValueAccessor;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class AnimalsElement extends AbstractElement implements ICommonElement {

    public static final GameRules.RuleKey<GameRules.BooleanValue> PERSISTENT_ANIMALS = GameRules.register("persistentAnimals", GameRules.Category.SPAWNING, IBooleanValueAccessor.callCreate(false));
    
    public Set<EntityType<?>> animalBlacklist;
    public int maxAnimalNumber;
    public boolean summonedMobPersistence;

    @Override
    public String getDescription() {
        
        return "Animals are no longer persistent by default, making them spawn just like monsters.";
    }

    @Override
    public void setupCommon() {

        this.addListener(this::onBabyEntitySpawn);
        this.addListener(this::onAnimalTame);
        this.addListener(this::onPotentialSpawns);
        this.addListener(this::onEntityJoinWorld);
    }

    @Override
    public void setupCommonConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Blacklist animals which will never despawn.", EntryCollectionBuilder.CONFIG_STRING).define("Animal Blacklist", Lists.<String>newArrayList()), v -> this.animalBlacklist = v, v -> new EntryCollectionBuilder<>(ForgeRegistries.ENTITIES).buildEntrySet(v, animal -> animal.getClassification() == EntityClassification.CREATURE, "No animal"));
        addToConfig(builder.comment("Constant for determining when to stop spawning animals in a world. Normally set to 10, monster constant is 70 for comparison. 18 is chosen to mimic spawning mechanics of the beta era.").define("Animal Mob Cap", 18), v -> this.maxAnimalNumber = v);
        addToConfig(builder.comment("Make all mobs (not just animals) automatically persistent when spawned using the \"/summon\" command, a spawn egg or a dispenser.").define("Summoned Mob Persistence", false), v -> this.summonedMobPersistence = v);
    }

    private void onBabyEntitySpawn(final BabyEntitySpawnEvent evt) {

        // make freshly born baby animals persistent
        if (evt.getChild() != null) {

            evt.getChild().enablePersistence();
        }
    }

    private void onAnimalTame(final AnimalTameEvent evt) {

        // enable persistence for animals that have been tamed (cats, ocelots, wolves, and all horse types including llamas)
        evt.getAnimal().enablePersistence();
    }

    private void onPotentialSpawns(final WorldEvent.PotentialSpawns evt) {

        if (evt.getType() == EntityClassification.CREATURE) {

            // prevent blacklisted animals from being respawned to prevent them from spawning endlessly as they are also blacklisted from counting towards the mob cap
            // this is not a good solution but I couldn't think of any other way
            evt.getList().removeIf(spawner -> this.animalBlacklist.contains(spawner.type));
        }
    }

    private void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        // make skeleton horse spawned as trap persistent, other three horses spawned from triggering the trap are persistent by default
        if (evt.getEntity() instanceof SkeletonHorseEntity && ((SkeletonHorseEntity) evt.getEntity()).isTrap()) {

            ((SkeletonHorseEntity) evt.getEntity()).enablePersistence();
        }

        System.out.println(evt.getEntity());
    }

}
