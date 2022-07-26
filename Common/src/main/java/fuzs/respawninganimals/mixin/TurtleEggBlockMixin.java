package fuzs.respawninganimals.mixin;

import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin extends Block {

    public TurtleEggBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @ModifyVariable(method = "randomTick", at = @At("STORE"))
    public Turtle randomTick$modify$store(Turtle turtle) {
        // enable persistence for baby turtles
        turtle.setPersistenceRequired();
        return turtle;
    }
}
