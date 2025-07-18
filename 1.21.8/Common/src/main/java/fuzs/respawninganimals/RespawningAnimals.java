package fuzs.respawninganimals;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.entity.EntityRidingEvents;
import fuzs.puzzleslib.api.event.v1.entity.EntityTickEvents;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.AnimalTameCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.CheckMobDespawnCallback;
import fuzs.puzzleslib.api.event.v1.level.GatherPotentialSpawnsCallback;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.respawninganimals.handler.AnimalPersistenceHandler;
import fuzs.respawninganimals.handler.AnimalSpawningHandler;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RespawningAnimals implements ModConstructor {
    public static final String MOD_ID = "respawninganimals";
    public static final String MOD_NAME = "Respawning Animals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        EntityTickEvents.END.register(AnimalPersistenceHandler::onEndEntityTick);
        AnimalTameCallback.EVENT.register(AnimalPersistenceHandler::onAnimalTame);
        EntityRidingEvents.START.register(AnimalPersistenceHandler::onStartRiding);
        CheckMobDespawnCallback.EVENT.register(AnimalSpawningHandler::onCheckMobDespawn);
        ServerEntityLevelEvents.LOAD.register(AnimalSpawningHandler::onEntityLoad);
        ServerLifecycleEvents.STARTED.register(AnimalSpawningHandler::onServerStarted);
        GatherPotentialSpawnsCallback.EVENT.register(AnimalSpawningHandler::onGatherPotentialSpawns);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
