package com.fuzs.respawnableanimals.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RabbitModel;
import net.minecraft.entity.passive.RabbitEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(RabbitModel.class)
public abstract class RabbitModelMixin<T extends RabbitEntity> extends EntityModel<T> {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;scale(FFF)V"))
    public void scale(MatrixStack matrixStackIn, float x, float y, float z) {

        if (x == 0.56666666F && y == 0.56666666F && z == 0.56666666F) {

            matrixStackIn.translate(0.0F, -1.0625F, 0.0F);
        } else if (x == 0.4F && y == 0.4F && z == 0.4F) {

            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0F, -0.75F, 0.0F);
        } else if (x == 0.6F && y == 0.6F && z == 0.6F) {

            matrixStackIn.translate(0.0F, -1.0F, 0.0F);
        }
    }

}
