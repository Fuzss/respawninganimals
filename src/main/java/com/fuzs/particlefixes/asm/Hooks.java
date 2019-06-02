package com.fuzs.particlefixes.asm;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;

import java.util.Objects;

@SuppressWarnings("unused")
public final class Hooks {

    public static void spawnServerParticle(WorldServer world, int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {

        EnumParticleTypes particle = Objects.requireNonNull(EnumParticleTypes.getParticleFromId(particleID));

        // this check is necessary as in SPacketParticles there are always as many ints written to the PacketBuffer as there are arguments pre-defined for that particle type
        // problem is though, that there is no check in vanilla if there are actually as many parameters passed as there are required which will lead to an ArrayIndexOutOfBoundsException
        // vanilla would even run into that bug itself when executing EntityLivingBase#updateItemUse on the server when there are no sub-types for an item, this only doesn't happen due to MC-10369 which this mod fixes
        if (parameters.length == particle.getArgumentCount()) {

            world.spawnParticle(particle, xCoord, yCoord, zCoord, 0, xSpeed, ySpeed, zSpeed, 1.0, parameters);

        }

    }

}
