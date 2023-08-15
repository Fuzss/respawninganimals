package fuzs.respawninganimals;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.entity.EntityRidingEvents;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.AnimalTameCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.CheckMobDespawnCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingEvents;
import fuzs.puzzleslib.api.event.v1.level.ServerLevelEvents;
import fuzs.respawninganimals.handler.AnimalPersistenceHandler;
import fuzs.respawninganimals.handler.AnimalSpawningHandler;
import fuzs.respawninganimals.init.ModRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RespawningAnimals implements ModConstructor {
    public static final String MOD_ID = "respawninganimals";
    public static final String MOD_NAME = "Respawning Animals";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        AnimalTameCallback.EVENT.register(AnimalPersistenceHandler::onAnimalTame);
        LivingEvents.TICK.register(AnimalPersistenceHandler::onLivingTick);
        EntityRidingEvents.START.register(AnimalPersistenceHandler::onStartRiding);
        CheckMobDespawnCallback.EVENT.register(AnimalSpawningHandler::onCheckMobDespawn);
        ServerEntityLevelEvents.LOAD_V2.register(AnimalSpawningHandler::onEntityLoad);
        ServerEntityLevelEvents.SPAWN.register(AnimalSpawningHandler::onEntitySpawn);
        ServerLevelEvents.LOAD.register(AnimalSpawningHandler::onLevelLoad);
    }
}
