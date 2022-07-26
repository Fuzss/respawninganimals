package fuzs.respawninganimals.mixin;

import fuzs.respawninganimals.api.event.entity.living.AnimalTameCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.class)
public abstract class CatMixin extends TamableAnimal {

    protected CatMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Cat;tame(Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
    public void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callback) {
        if (!AnimalTameCallback.EVENT.invoker().onAnimalTame(this, player)) {
            this.level.broadcastEntityEvent(this, (byte) 6);
            // vanilla returns InteractionResult#CONSUME here which is not correct for the server
            callback.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
