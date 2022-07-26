package fuzs.respawninganimals.init;

import fuzs.puzzleslib.core.CoreServices;
import net.minecraft.world.level.GameRules;

public class ModRegistry {
    public static final GameRules.Key<GameRules.BooleanValue> PERSISTENT_ANIMALS_GAME_RULE = CoreServices.GAME_RULES.register("persistentAnimals", GameRules.Category.SPAWNING, CoreServices.GAME_RULES.createBooleanRule(false));

    public static void touch() {

    }
}
