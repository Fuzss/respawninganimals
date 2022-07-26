package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.api.event.entity.living.AnimalTameCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Parrot.class)
public abstract class ParrotMixin extends ShoulderRidingEntity {

    protected ParrotMixin(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Parrot;tame(Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
    public void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callback) {
        if (!AnimalTameCallback.EVENT.invoker().onAnimalTame(this, player)) {
            this.level.broadcastEntityEvent(this, (byte) 6);
            callback.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
