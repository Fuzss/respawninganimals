package com.fuzs.respawnableanimals.capability.container;

import net.minecraft.nbt.CompoundNBT;

public class AnimalsCapability implements IAnimalsCapability {

    private int enabled;

    @Override
    public boolean isEnabled() {

        return this.enabled == 1;
    }

    @Override
    public void enable() {

        if (this.enabled == -1) {

            this.enabled = 1;
        }
    }

    @Override
    public void set(boolean enabled) {

        this.enabled = enabled ? 1 : 0;
    }

    @Override
    public CompoundNBT serializeNBT() {

        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(getName(), this.enabled);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

        this.enabled = nbt.getInt(getName());
    }

    public static String getName() {

        return "RespawnableAnimals";
    }

}
