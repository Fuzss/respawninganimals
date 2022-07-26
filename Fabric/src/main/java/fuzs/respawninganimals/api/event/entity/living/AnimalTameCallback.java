package fuzs.respawninganimals.api.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface AnimalTameCallback {
    Event<AnimalTameCallback> EVENT = EventFactory.createArrayBacked(AnimalTameCallback.class, listeners -> (Animal animal, Player tamer) -> {
        for (AnimalTameCallback event : listeners) {
            if (!event.onAnimalTame(animal, tamer)) return false;
        }
        return true;
    });

    /**
     * called when a player has tamed an animal, allows for cancelling taming by returning <code>false</code>
     *
     * @param animal    the animal that has been tamed
     * @param tamer     the player taming the animal
     * @return          is taming allowed to happen
     */
    boolean onAnimalTame(Animal animal, Player tamer);
}
