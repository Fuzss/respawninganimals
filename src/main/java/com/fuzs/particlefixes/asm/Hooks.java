package com.fuzs.particlefixes.asm;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Objects;

@SuppressWarnings("unused")
public final class Hooks {

    public static void spawnServerParticle(WorldServer world, int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
            world.spawnParticle(Objects.requireNonNull(EnumParticleTypes.getParticleFromId(particleID)), xCoord, yCoord, zCoord, 1, 0.0D, 0.0D, 0.0D, (xSpeed * ySpeed * zSpeed) / 3.0D, parameters);
    }

}
