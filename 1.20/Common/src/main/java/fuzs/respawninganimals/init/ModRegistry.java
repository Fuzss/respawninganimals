package fuzs.respawninganimals.init;

import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.mixin.accessor.MobCategoryAccessor;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.GameRules;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(RespawningAnimals.MOD_ID);
    public static final TagKey<EntityType<?>> PERSISTENT_ANIMALS_ENTITY_TYPE_TAG = REGISTRY.registerEntityTypeTag("persistent_animals");

    public static final GameRules.Key<GameRules.IntegerValue> CREATURE_MOB_CAP_GAME_RULE = GameRulesFactory.INSTANCE.register("creatureMobCap", GameRules.Category.SPAWNING, GameRulesFactory.INSTANCE.createIntRule(18, 0, 100, (server, booleanValue) -> {
        setCreatureAttributes(server.getGameRules());
    }));
    public static final GameRules.Key<GameRules.BooleanValue> PERSISTENT_ANIMALS_GAME_RULE = GameRulesFactory.INSTANCE.register("persistentAnimals", GameRules.Category.SPAWNING, GameRulesFactory.INSTANCE.createBooleanRule(false, (server, booleanValue) -> {
        setCreatureAttributes(server.getGameRules());
    }));

    public static void touch() {

    }

    @SuppressWarnings("DataFlowIssue")
    public static void setCreatureAttributes(GameRules gameRules) {
        ((MobCategoryAccessor) (Object) MobCategory.CREATURE).respawninganimals$setIsPersistent(gameRules.getBoolean(PERSISTENT_ANIMALS_GAME_RULE));
        ((MobCategoryAccessor) (Object) MobCategory.CREATURE).respawninganimals$setMax(gameRules.getBoolean(PERSISTENT_ANIMALS_GAME_RULE) ? 10 : gameRules.getInt(CREATURE_MOB_CAP_GAME_RULE));
    }
}
