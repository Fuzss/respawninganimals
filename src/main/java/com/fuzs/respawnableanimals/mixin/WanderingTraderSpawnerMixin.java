package com.fuzs.respawnableanimals.mixin;

import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("unused")
@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderSpawnerMixin {

    @Inject(method = "func_221243_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/horse/TraderLlamaEntity;setLeashHolder(Lnet/minecraft/entity/Entity;Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void func_221243_a(WanderingTraderEntity p_221243_1_, int p_221243_2_, CallbackInfo callbackInfo, BlockPos blockpos, TraderLlamaEntity traderllamaentity) {

        // prevent trader llamas from despawning like other animals, they will still despawn together with the trader though
        traderllamaentity.enablePersistence();
    }

}