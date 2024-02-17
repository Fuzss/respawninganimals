package fuzs.respawninganimals.init;

import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.handler.AnimalSpawningHandler;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameRules;

public class ModRegistry {
    static final RegistryManager TAGS = RegistryManager.instant(RespawningAnimals.MOD_ID);
    public static final TagKey<EntityType<?>> PERSISTENT_ANIMALS_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag("persistent_animals");

    public static final GameRules.Key<GameRules.IntegerValue> ANIMAL_MOB_CAP_GAME_RULE = GameRulesFactory.INSTANCE.register("animalMobCap", GameRules.Category.SPAWNING, GameRulesFactory.INSTANCE.createIntRule(15, 0, 100, (server, booleanValue) -> {
        AnimalSpawningHandler.setCreatureAttributes(server.getGameRules());
    }));
    public static final GameRules.Key<GameRules.BooleanValue> PERSISTENT_ANIMALS_GAME_RULE = GameRulesFactory.INSTANCE.register("persistentAnimals", GameRules.Category.SPAWNING, GameRulesFactory.INSTANCE.createBooleanRule(false, (server, booleanValue) -> {
        AnimalSpawningHandler.setCreatureAttributes(server.getGameRules());
    }));

    public static void touch() {

    }
}
