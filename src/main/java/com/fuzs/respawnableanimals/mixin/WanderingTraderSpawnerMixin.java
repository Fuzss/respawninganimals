package com.fuzs.respawnableanimals.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderSpawnerMixin {

    @Redirect(method = "func_242373_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/horse/TraderLlamaEntity;setLeashHolder(Lnet/minecraft/entity/Entity;Z)V"))
    private void setLeashHolder(TraderLlamaEntity traderllamaentity, Entity entityIn, boolean sendAttachNotification) {

        // prevent trader llamas from despawning like other animals, they will still despawn together with the trader though
        traderllamaentity.enablePersistence();
        traderllamaentity.setLeashHolder(entityIn, true);
    }

}
