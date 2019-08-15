package com.fuzs.particlefixes.helper;

import com.fuzs.particlefixes.ParticleFixes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReflectionHelper {

    private static final String ENTITYANIMAL_INLOVE = "field_70881_d";

    public static int getInLove(AnimalEntity instance) {

        Integer i = null;

        try {

            i = ObfuscationReflectionHelper.getPrivateValue(AnimalEntity.class, instance, ENTITYANIMAL_INLOVE);

        } catch (Exception e) {

            ParticleFixes.LOGGER.error("setInLove() failed", e);

        }

        return i != null ? i : 0;

    }

}
