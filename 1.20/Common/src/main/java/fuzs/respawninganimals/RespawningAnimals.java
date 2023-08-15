package fuzs.respawninganimals;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.entity.EntityRidingEvents;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.AnimalTameCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.CheckMobDespawnCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingEvents;
import fuzs.puzzleslib.api.event.v1.level.ServerLevelEvents;
import fuzs.respawninganimals.config.ServerConfig;
import fuzs.respawninganimals.handler.AnimalSpawningHandler;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RespawningAnimals implements ModConstructor {
    public static final String MOD_ID = "respawninganimals";
    public static final String MOD_NAME = "Respawning Animals";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        AnimalTameCallback.EVENT.register(AnimalSpawningHandler::onAnimalTame);
        LivingEvents.TICK.register(AnimalSpawningHandler::onLivingTick);
        EntityRidingEvents.START.register(AnimalSpawningHandler::onStartRiding);
        CheckMobDespawnCallback.EVENT.register(AnimalSpawningHandler::onCheckMobDespawn);
        ServerEntityLevelEvents.SPAWN.register(AnimalSpawningHandler::onEntitySpawn);
        ServerLevelEvents.LOAD.register((server, level) -> {
            if (level.dimension() == Level.OVERWORLD) ModRegistry.setCreatureAttributes(level.getGameRules());
        });
    }
}
