package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.RespawnableAnimals;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

@SuppressWarnings("unused")
public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {

        Mixins.addConfiguration("META-INF/" + RespawnableAnimals.MODID + ".mixins.json");
    }

}
