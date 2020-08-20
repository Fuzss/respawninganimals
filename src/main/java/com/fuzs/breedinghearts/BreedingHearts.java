package com.fuzs.breedinghearts;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@SuppressWarnings("unused")
@Mod(BreedingHearts.MODID)
@Mod.EventBusSubscriber(modid = BreedingHearts.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BreedingHearts {

    public static final String MODID = "breedinghearts";
    public static final String NAME = "Breeding Hearts";
    public static final Logger LOGGER = LogManager.getLogger(BreedingHearts.NAME);

    private static final String ENTITYANIMAL_INLOVE = "field_70881_d";

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {

        if (evt.getEntityLiving() instanceof AnimalEntity && !evt.getEntityLiving().world.isRemote) {

            AnimalEntity animal = (AnimalEntity) evt.getEntityLiving();
            int inLove = getInLove(animal);
            if (inLove > 0 && inLove % 10 == 0) {

                Random rnd = animal.getRNG();
                double posX = animal.posX + (double) (rnd.nextFloat() * animal.getWidth() * 2.0F) - (double) animal.getWidth();
                double posY = animal.posY + 0.5D + (double) (rnd.nextFloat() * animal.getHeight());
                double posZ = animal.posZ + (double) (rnd.nextFloat() * animal.getWidth() * 2.0F) - (double) animal.getWidth();
                double d0 = rnd.nextGaussian() * 0.02D;
                double d1 = rnd.nextGaussian() * 0.02D;
                double d2 = rnd.nextGaussian() * 0.02D;
                ((ServerWorld) animal.world).spawnParticle(ParticleTypes.HEART, posX, posY, posZ, 1, d0, d1, d2, 0D);
            }
        }
    }

    private static int getInLove(AnimalEntity instance) {

        Integer inLove = null;
        try {

            inLove = ObfuscationReflectionHelper.getPrivateValue(AnimalEntity.class, instance, ENTITYANIMAL_INLOVE);
        } catch (Exception e) {

            LOGGER.error("getInLove() failed", e);
        }

        return inLove != null ? inLove : 0;
    }

}
