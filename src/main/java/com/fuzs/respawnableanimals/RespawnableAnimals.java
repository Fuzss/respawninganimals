package com.fuzs.respawnableanimals;

import com.fuzs.respawnableanimals.config.ConfigBuildHandler;
import com.fuzs.respawnableanimals.config.ConfigManager;
import com.fuzs.respawnableanimals.mixin.accessor.IAnimalEntityAccessor;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@SuppressWarnings("unused")
@Mod(RespawnableAnimals.MODID)
public class RespawnableAnimals {

    public static final String MODID = "respawnableanimals";
    public static final String NAME = "Respawnable Animals";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public RespawnableAnimals() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);

        // config setup
        ConfigBuildHandler.setup(ModConfig.Type.COMMON);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.builder().getCommonSpec());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigManager.get()::onModConfig);
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

//        MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::onBabyEntitySpawn);
        MinecraftForge.EVENT_BUS.addListener(this::onPotentialSpawns);
    }

    private void onLivingUpdate(final LivingEvent.LivingUpdateEvent evt) {

        // call correct method for sending spawned particles to the client
        if (evt.getEntityLiving() instanceof AnimalEntity && !evt.getEntityLiving().world.isRemote) {

            AnimalEntity animal = (AnimalEntity) evt.getEntityLiving();
            int inLove = ((IAnimalEntityAccessor) animal).getInLove();
            if (inLove > 0 && inLove % 10 == 0) {

                Random random = animal.getRNG();
                double posX = random.nextGaussian() * 0.02;
                double posY = random.nextGaussian() * 0.02;
                double posZ = random.nextGaussian() * 0.02;
                ((ServerWorld) animal.world).spawnParticle(ParticleTypes.HEART, animal.getPosXRandom(1.0),
                        animal.getPosYRandom() + 0.5, animal.getPosZRandom(1.0), 1, posX, posY, posZ, 0.0);
            }
        }
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

}
