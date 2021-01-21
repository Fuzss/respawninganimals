package com.fuzs.respawnableanimals.mixin.accessor;

import net.minecraft.entity.passive.OcelotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OcelotEntity.class)
public interface IOcelotEntityAccessor {

    @Invoker
    boolean callIsTrusting();

}
