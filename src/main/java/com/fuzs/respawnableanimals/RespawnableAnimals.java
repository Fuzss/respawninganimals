package com.fuzs.respawnableanimals;

import com.fuzs.respawnableanimals.capability.AnimalsCapabilities;
import com.fuzs.respawnableanimals.capability.container.AnimalsCapability;
import com.fuzs.respawnableanimals.config.ConfigBuildHandler;
import com.fuzs.respawnableanimals.config.ConfigManager;
import com.fuzs.respawnableanimals.mixin.accessor.IBooleanValueAccessor;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(RespawnableAnimals.MODID)
public class RespawnableAnimals {

    public static final String MODID = "respawnableanimals";
    public static final String NAME = "Respawnable Animals";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final GameRules.RuleKey<GameRules.BooleanValue> PERSISTENT_ANIMALS = GameRules.register("persistentAnimals", GameRules.Category.SPAWNING, IBooleanValueAccessor.callCreate(false));

    public RespawnableAnimals() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);

        // config setup
        ConfigBuildHandler.setup(ModConfig.Type.COMMON);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.builder().getCommonSpec());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigManager.get()::onModConfig);
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        new AnimalsCapabilities();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::onBabyEntitySpawn);
        MinecraftForge.EVENT_BUS.addListener(this::onPotentialSpawns);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
    }

    private void onBabyEntitySpawn(final BabyEntitySpawnEvent evt) {

        // make freshly born baby animals persistent
        if (evt.getChild() != null) {

            evt.getChild().enablePersistence();
        }
    }

    private void onPotentialSpawns(final WorldEvent.PotentialSpawns evt) {

        if (evt.getType() == EntityClassification.CREATURE) {

            // prevent blacklisted animals from being respawned
            // this is not a good solution but I couldn't think of any other way
            evt.getList().removeIf(spawner -> ConfigBuildHandler.animalBlacklist.contains(spawner.type));
        }
    }

    private void onWorldLoad(final WorldEvent.Load evt) {

        if (evt.getWorld() instanceof ServerWorld && ((ServerWorld) evt.getWorld()).getGameTime() < 72000L) {

            AnimalsCapabilities.getCapability((ServerWorld) evt.getWorld(), AnimalsCapabilities.ANIMALS)
                    .ifPresent(AnimalsCapability::enable);
        }
    }

}
