package com.fuzs.respawnableanimals.capability;

import com.fuzs.puzzleslib_ra.capability.CapabilityController;
import com.fuzs.puzzleslib_ra.capability.CapabilityDispatcher;
import com.fuzs.respawnableanimals.RespawnableAnimals;
import com.fuzs.respawnableanimals.capability.container.IAnimalsCapability;
import com.fuzs.respawnableanimals.capability.container.AnimalsCapability;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class AnimalsCapabilities extends CapabilityController {

    @CapabilityInject(IAnimalsCapability.class)
    public static final Capability<AnimalsCapability> ANIMALS = null;

    @Override
    protected void register() {

        register(IAnimalsCapability.class, AnimalsCapability::new);
    }

    @Override
    protected void onAttachWorldCapabilities(AttachCapabilitiesEvent<World> evt) {

        evt.addCapability(getRegistryKey(RespawnableAnimals.MODID, AnimalsCapability.getName()), new CapabilityDispatcher<>(new AnimalsCapability(), AnimalsCapabilities.ANIMALS));
    }

}
