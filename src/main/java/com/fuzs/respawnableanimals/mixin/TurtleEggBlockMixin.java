package com.fuzs.respawnableanimals.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@SuppressWarnings("unused")
@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin extends Block {

    public TurtleEggBlockMixin(Properties properties) {

        super(properties);
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/TurtleEntity;setGrowingAge(I)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random, CallbackInfo callbackInfo, int i, int j, TurtleEntity turtleentity) {

        // enable persistence for baby turtles
        turtleentity.enablePersistence();
    }

}
