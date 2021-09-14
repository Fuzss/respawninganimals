package fuzs.respawnableanimals.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.passive.TurtleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin extends Block {

    public TurtleEggBlockMixin(Properties properties) {

        super(properties);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/TurtleEntity;setAge(I)V"))
    public void setAge(TurtleEntity turtle, int age) {

        // enable persistence for baby turtles
        turtle.setPersistenceRequired();
        turtle.setAge(age);
    }

}
