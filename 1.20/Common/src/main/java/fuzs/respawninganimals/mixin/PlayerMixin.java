package fuzs.respawninganimals.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean startRiding(Entity vehicle, boolean force) {
        boolean flag = super.startRiding(vehicle, force);
        // make all mobs ridden by player persistent
        if (flag && vehicle instanceof Animal animal) animal.setPersistenceRequired();
        return flag;
    }
}
