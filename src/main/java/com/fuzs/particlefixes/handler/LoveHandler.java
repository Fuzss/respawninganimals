package com.fuzs.particlefixes.handler;

import com.fuzs.particlefixes.helper.ReflectionHelper;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class LoveHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void spawnHearts(LivingEvent.LivingUpdateEvent evt) {

        if (evt.getEntity() instanceof AnimalEntity && !evt.getEntity().world.isRemote) {

            AnimalEntity entityanimal = (AnimalEntity) evt.getEntity();
            ServerWorld world = (ServerWorld) entityanimal.world;
            int inLove = ReflectionHelper.getInLove(entityanimal);

            if (inLove > 0 && inLove % 10 == 0) {
                Random random = entityanimal.getRNG();
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                world.spawnParticle(ParticleTypes.HEART, entityanimal.posX + (double) (random.nextFloat() * entityanimal.getWidth() * 2.0F) - (double) entityanimal.getWidth(), entityanimal.posY + 0.5D + (double) (random.nextFloat() * entityanimal.getHeight()), entityanimal.posZ + (double) (random.nextFloat() * entityanimal.getWidth() * 2.0F) - (double) entityanimal.getWidth(), 1, d0, d1, d2, 0D);
            }

        }

    }

}
