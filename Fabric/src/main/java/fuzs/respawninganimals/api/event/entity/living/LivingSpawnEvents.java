package fuzs.respawninganimals.api.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

/**
 * callbacks for when entities are spawning or despawning (currently incomplete)
 */
public class LivingSpawnEvents {
    public static final Event<CheckSpawn> CHECK_SPAWN = EventFactory.createArrayBacked(CheckSpawn.class, listeners -> (Mob mob, LevelAccessor levelAccessor, double posX, double posY, double posZ, MobSpawnType spawnType) -> {
        for (CheckSpawn event : listeners) {
            if (!event.onCheckSpawn(mob, levelAccessor, posX, posY, posZ, spawnType)) {
                return false;
            }
        }
        return true;
    });

    @FunctionalInterface
    public interface CheckSpawn {

        /**
         * called when an entity type is trying to be spawned, find out where this call is coming from by looking at <code>spawnType</code>
         * the implementation is a bit different from Forge, more cases are covered
         *
         * on Forge this event also includes spawns from monster spawners and provides additional context,
         * here we probably implement that better as a separate event in the future
         *
         * @param mob               entity type trying to be spawned
         * @param levelAccessor     the level
         * @param posX              spawn position x
         * @param posY              spawn position y
         * @param posZ              spawn position z
         * @param spawnType         type of mob spawn (source of spawning attempt)
         * @return                  is the entity allowed to be spawned
         */
        boolean onCheckSpawn(Mob mob, LevelAccessor levelAccessor, double posX, double posY, double posZ, MobSpawnType spawnType);
    }
}
