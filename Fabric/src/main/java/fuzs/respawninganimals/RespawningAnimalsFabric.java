package fuzs.respawninganimals;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.respawninganimals.api.event.entity.living.AnimalTameCallback;
import fuzs.respawninganimals.api.event.entity.living.BabyEntitySpawnCallback;
import fuzs.respawninganimals.api.event.entity.living.LivingSpawnEvents;
import fuzs.respawninganimals.handler.AnimalSpawningHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

public class RespawningAnimalsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(RespawningAnimals.MOD_ID).accept(new RespawningAnimals());
        registerHandlers();
    }

    private static void registerHandlers() {
        AnimalSpawningHandler animalSpawningHandler = new AnimalSpawningHandler();
        BabyEntitySpawnCallback.EVENT.register((parentA, parentB, child) -> {
            animalSpawningHandler.onBabyEntitySpawn(parentA, parentB, child);
            return child;
        });
        AnimalTameCallback.EVENT.register((animal, tamer) -> {
            animalSpawningHandler.onAnimalTame(animal, tamer);
            return true;
        });
        ServerEntityEvents.ENTITY_LOAD.register(animalSpawningHandler::onEntityJoinLevel);
        LivingSpawnEvents.CHECK_SPAWN.register(animalSpawningHandler::onCheckSpawn);
    }
}
