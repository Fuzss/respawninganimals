package fuzs.respawninganimals.handler;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Predicate;

public class AnimalPersistenceHandler {
    private static final List<Predicate<Mob>> TICK_PREDICATES = Lists.newArrayList();

    static {
        // animals that are in love from using their breeding item on them
        TICK_PREDICATES.add((Mob mob) -> {
            return mob instanceof Animal animal && animal.isInLove();
        });
        // mobs with a lead attached to them
        TICK_PREDICATES.add(Mob::isLeashed);
        // mobs that have an owner like horses or wolves
        TICK_PREDICATES.add((Mob mob) -> {
            return mob instanceof OwnableEntity ownable && ownable.getOwnerUUID() != null;
        });
        // mobs that have a saddle equipped like pigs and horses
        TICK_PREDICATES.add((Mob mob) -> {
            return mob instanceof Saddleable saddleable && saddleable.isSaddled();
        });
    }

    public static void onEndEntityTick(Entity entity) {
        if (entity instanceof Mob mob && !mob.isPersistenceRequired() && AnimalSpawningHandler.isAllowedToDespawn(mob, mob.level().getGameRules())) {
            for (Predicate<Mob> mobPredicate : TICK_PREDICATES) {
                if (mobPredicate.test(mob)) {
                    mob.setPersistenceRequired();
                    break;
                }
            }
        }
    }

    public static EventResult onAnimalTame(Animal animal, Player player) {
        // enable persistence for animals that have been tamed (cats, ocelots, wolves, and all horse types including llamas)
        setPersistenceForVolatileAnimal(animal);
        return EventResult.PASS;
    }

    public static EventResult onStartRiding(Level level, Entity rider, Entity vehicle) {
        // make mobs the player has ridden persistent
        if (rider instanceof Player) setPersistenceForVolatileAnimal(vehicle);
        return EventResult.PASS;
    }

    private static void setPersistenceForVolatileAnimal(Entity entity) {
        if (entity instanceof Mob mob && mob.getType().getCategory() == MobCategory.CREATURE) {
            if (!mob.isPersistenceRequired() && AnimalSpawningHandler.isAllowedToDespawn(mob, entity.level().getGameRules())) {
                mob.setPersistenceRequired();
            }
        }
    }
}
