package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import com.fuzs.respawnableanimals.common.element.AnimalsElement;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(ServerChunkProvider.class)
public abstract class ServerChunkProviderMixin extends AbstractChunkProvider {

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;getGameTime()J"))
    public long getGameTime(WorldInfo worldinfo) {

        // remove 400 tick delay for spawning animals
        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        return element.isEnabled() ? 0 : worldinfo.getGameTime();
    }

}
