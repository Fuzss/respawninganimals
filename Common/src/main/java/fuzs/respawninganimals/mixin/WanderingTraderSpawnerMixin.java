package fuzs.respawninganimals.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderSpawnerMixin {

    @Redirect(method = "tryToSpawnLlamaFor", at = @At("STORE"))
    private void setLeashHolder(TraderLlama traderLlama, Entity entityIn, boolean sendAttachNotification) {

        // prevent trader llamas from despawning like other animals, they will still despawn together with the trader though
        traderllamaentity.setPersistenceRequired();
        traderllamaentity.setLeashedTo(entityIn, true);
    }

}
