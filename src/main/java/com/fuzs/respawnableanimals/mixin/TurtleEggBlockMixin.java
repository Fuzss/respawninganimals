package com.fuzs.respawnableanimals.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.passive.TurtleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin extends Block {

    public TurtleEggBlockMixin(Properties properties) {

        super(properties);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/TurtleEntity;setGrowingAge(I)V"))
    public void setGrowingAge(TurtleEntity turtleEntity, int age) {

        // enable persistence for baby turtles
        turtleEntity.enablePersistence();
        turtleEntity.setGrowingAge(age);
    }

}
