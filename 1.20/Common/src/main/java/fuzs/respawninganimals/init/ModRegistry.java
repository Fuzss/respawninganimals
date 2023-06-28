package fuzs.respawninganimals.init;

import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import net.minecraft.world.level.GameRules;

public class ModRegistry {
    public static final GameRules.Key<GameRules.BooleanValue> PERSISTENT_ANIMALS_GAME_RULE = GameRulesFactory.INSTANCE.register("persistentAnimals", GameRules.Category.SPAWNING, GameRulesFactory.INSTANCE.createBooleanRule(false));

    public static void touch() {

    }
}
