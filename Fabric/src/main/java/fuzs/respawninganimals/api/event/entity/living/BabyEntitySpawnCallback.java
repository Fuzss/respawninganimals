package fuzs.respawninganimals.api.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BabyEntitySpawnCallback {
    Event<BabyEntitySpawnCallback> EVENT = EventFactory.createArrayBacked(BabyEntitySpawnCallback.class, listeners -> (Mob parentA, Mob parentB, @Nullable AgeableMob child) -> {
        for (BabyEntitySpawnCallback event : listeners) {
            AgeableMob newChild = event.onBabyEntitySpawn(parentA, parentB, child);
            if (child != newChild) {
                return newChild;
            }
        }
        return child;
    });

    /**
     * called when a child is created from breeding two parents, allows for replacing the child
     * can also be set to null to prevent any offspring from being created
     *
     * @param parentA   one parent
     * @param parentB   the other parent
     * @param child     the proposed child
     * @return          the actual child that will be spawned
     */
    @Nullable
    AgeableMob onBabyEntitySpawn(Mob parentA, Mob parentB, @Nullable AgeableMob child);
}
