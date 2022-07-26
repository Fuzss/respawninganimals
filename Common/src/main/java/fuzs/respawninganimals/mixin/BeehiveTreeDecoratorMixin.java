package fuzs.respawninganimals.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(BeehiveTreeDecorator.class)
public abstract class BeehiveTreeDecoratorMixin extends TreeDecorator {

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/BeehiveTileEntity;addOccupantWithPresetTicks(Lnet/minecraft/entity/Entity;ZI)V"))
    public void addOccupantWithPresetTicks(BeehiveTileEntity tileEntity, Entity entity, boolean manyTicksInHive, int minOccupationTicks) {

        if (entity instanceof BeeEntity) {

            ((BeeEntity) entity).setPersistenceRequired();
        }

        tileEntity.addOccupantWithPresetTicks(entity, manyTicksInHive, minOccupationTicks);
    }

}
