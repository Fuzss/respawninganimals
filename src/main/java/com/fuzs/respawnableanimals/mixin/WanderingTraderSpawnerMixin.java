package com.fuzs.respawnableanimals.mixin;

import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("unused")
@Mixin(WanderingTraderSpawner.class)
public class WanderingTraderSpawnerMixin {

    @Inject(method = "func_242373_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/horse/TraderLlamaEntity;setLeashHolder(Lnet/minecraft/entity/Entity;Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void func_242373_a(ServerWorld p_242373_1_, WanderingTraderEntity p_242373_2_, int p_242373_3_, CallbackInfo callbackInfo, BlockPos blockpos, TraderLlamaEntity traderllamaentity) {

        // prevent trader llamas from despawning like other animals, they will still despawn together with the trader though
        traderllamaentity.enablePersistence();
    }

}
