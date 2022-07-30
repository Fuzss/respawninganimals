package fuzs.respawninganimals.config;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.annotation.Config;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Set;

public class ServerConfig implements ConfigCore {
    @Config(name = "animal_blacklist", description = {"Blacklist animals which will never despawn.", EntryCollectionBuilder.CONFIG_DESCRIPTION})
    List<String> animalBlacklistRaw = Lists.newArrayList("whisperwoods:hirschgeist");
    @Config(name = "animal_mob_cap", description = "Constant for determining when to stop spawning animals in a world. Normally set to 10, monster constant is 70 for comparison. 18 is chosen to mimic spawning mechanics of the beta era.")
    public int maxAnimalNumber = 18;
    @Config(name = "summoned_mob_persistence", description = "Make all mobs (not just animals) automatically persistent when spawned using the \"/summon\" command, a spawn egg or a dispenser.")
    public boolean summonedMobPersistence = false;

    public Set<EntityType<?>> animalBlacklist;

    @Override
    public void afterConfigReload() {
        this.animalBlacklist = EntryCollectionBuilder.of(Registry.ENTITY_TYPE_REGISTRY).buildSet(this.animalBlacklistRaw);
    }
}
