package fuzs.respawninganimals.mixin;

import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin extends Block {

    public TurtleEggBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @ModifyVariable(method = "randomTick", at = @At("LOAD"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Turtle;moveTo(DDDFF)V")))
    public Turtle randomTick$modify$load(Turtle turtle) {
        // enable persistence for baby turtles
        turtle.setPersistenceRequired();
        return turtle;
    }
}
