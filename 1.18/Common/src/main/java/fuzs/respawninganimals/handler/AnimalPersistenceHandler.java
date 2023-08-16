package fuzs.respawninganimals.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AnimalPersistenceHandler {

    public static EventResult onAnimalTame(Animal animal, Player player) {
        // enable persistence for animals that have been tamed (cats, ocelots, wolves, and all horse types including llamas)
        if (AnimalSpawningHandler.shouldHandleMobDespawning(animal, animal.level)) {
            animal.setPersistenceRequired();
        }
        return EventResult.PASS;
    }

    public static EventResult onLivingTick(LivingEntity entity) {
        // make animals in love persistent
        if (entity instanceof Animal animal && animal.isInLove()) {
            if (AnimalSpawningHandler.shouldHandleMobDespawning(animal, animal.level)) {
                animal.setPersistenceRequired();
            }
        }
        return EventResult.PASS;
    }

    public static EventResult onStartRiding(Level level, Entity rider, Entity vehicle) {
        // make mobs the player has ridden persistent
        if (vehicle instanceof Mob mob && AnimalSpawningHandler.shouldHandleMobDespawning(mob, level)) {
            mob.setPersistenceRequired();
        }
        return EventResult.PASS;
    }
}
