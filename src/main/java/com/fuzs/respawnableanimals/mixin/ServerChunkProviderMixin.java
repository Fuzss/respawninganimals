package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import com.fuzs.respawnableanimals.common.element.AnimalsElement;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(ServerChunkProvider.class)
public abstract class ServerChunkProviderMixin extends AbstractChunkProvider {

    @Shadow
    @Final
    public ServerWorld world;

    @Redirect(method = {"lambda$tickChunks$5", "func_223434_a"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityClassification;getAnimal()Z"))
    public boolean getAnimal(EntityClassification entityclassification) {

        // remove 400 tick delay for spawning animals
        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        if (element.isEnabled() && entityclassification == EntityClassification.CREATURE && !this.world.getGameRules().getBoolean(AnimalsElement.PERSISTENT_ANIMALS)) {

            return false;
        }

        return entityclassification.getAnimal();
    }

    @Redirect(method = {"lambda$tickChunks$5", "func_223434_a"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityClassification;getMaxNumberOfCreature()I"))
    public int getMaxNumberOfCreature(EntityClassification entityclassification) {

        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        if (element.isEnabled() && entityclassification == EntityClassification.CREATURE) {

            return element.maxAnimalNumber;
        }

        return entityclassification.getMaxNumberOfCreature();
    }

}
