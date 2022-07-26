package fuzs.respawninganimals.mixin;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {

    protected AnimalMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setInLove(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"))
    public void setInLove(@Nullable Player player, CallbackInfo callback) {
        // enable persistence for animals that have been bred
        this.setPersistenceRequired();
    }

    @Inject(method = "setInLoveTime(I)V", at = @At("HEAD"))
    public void setInLoveTime$inject$head(int ticks, CallbackInfo callback) {
        // enable persistence for animals that have been bred
        if (ticks > 0) this.setPersistenceRequired();
    }
}
