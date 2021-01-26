package com.fuzs.respawnableanimals.capability.container;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IAnimalsCapability extends INBTSerializable<CompoundNBT> {

    boolean isEnabled();

    void enable();

    void set(boolean enabled);

}
