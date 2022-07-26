package fuzs.respawninganimals.mixin;

import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderSpawnerMixin {

    @ModifyVariable(method = "tryToSpawnLlamaFor", at = @At(value = "LOAD"), ordinal = 1)
    private TraderLlama tryToSpawnLlamaFor$modify$store(TraderLlama traderLlama) {
        // prevent trader llamas from despawning like other animals, they will still despawn together with the trader though
        traderLlama.setPersistenceRequired();
        return traderLlama;
    }
}
