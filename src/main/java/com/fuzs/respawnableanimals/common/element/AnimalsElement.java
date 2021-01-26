package com.fuzs.respawnableanimals.common.element;

import com.fuzs.puzzleslib_ra.config.deserialize.EntryCollectionBuilder;
import com.fuzs.puzzleslib_ra.element.AbstractElement;
import com.fuzs.puzzleslib_ra.element.side.ICommonElement;
import com.fuzs.respawnableanimals.mixin.accessor.IBooleanValueAccessor;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class AnimalsElement extends AbstractElement implements ICommonElement {

    public static final GameRules.RuleKey<GameRules.BooleanValue> PERSISTENT_ANIMALS = GameRules.register("persistentAnimals", IBooleanValueAccessor.callCreate(false));
    
    public Set<EntityType<?>> animalBlacklist;
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
    }

    @Override
    public void setupCommonConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Blacklist animals which will never despawn.", EntryCollectionBuilder.CONFIG_STRING).define("Animal Blacklist", Lists.<String>newArrayList()), v -> this.animalBlacklist = v, v -> new EntryCollectionBuilder<>(ForgeRegistries.ENTITIES).buildEntrySet(v, animal -> animal.getClassification() == EntityClassification.CREATURE, "No animal"));
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

            // prevent blacklisted animals from being respawned
            // this is not a good solution but I couldn't think of any other way
            evt.getList().removeIf(spawner -> this.animalBlacklist.contains(spawner.entityType));
        }
    }

}
