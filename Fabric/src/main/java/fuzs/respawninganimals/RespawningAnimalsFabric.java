package fuzs.respawninganimals;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.respawninganimals.handler.AnimalSpawningHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;

public class RespawningAnimalsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(RespawningAnimals.MOD_ID).accept(new RespawningAnimals());
        registerHandlers();
    }

    private static void registerHandlers() {
        AnimalSpawningHandler animalSpawningHandler = new AnimalSpawningHandler();
        MinecraftForge.EVENT_BUS.addListener((final BabyEntitySpawnEvent evt) -> {
            animalSpawningHandler.onBabyEntitySpawn(evt.getParentA(), evt.getParentB(), evt.getChild());
        });
        MinecraftForge.EVENT_BUS.addListener((final AnimalTameEvent evt) -> {
            animalSpawningHandler.onAnimalTame(evt.getAnimal(), evt.getTamer());
        });
        MinecraftForge.EVENT_BUS.addListener((final EntityJoinLevelEvent evt) -> {
            animalSpawningHandler.onEntityJoinWorld(evt.getEntity(), evt.getLevel());
        });
        MinecraftForge.EVENT_BUS.addListener((final LivingSpawnEvent.CheckSpawn evt) -> {
            boolean result = animalSpawningHandler.onCheckSpawn(evt.getEntity(), evt.getLevel(), evt.getX(), evt.getY(), evt.getZ(), evt.getSpawner(), evt.getSpawnReason());
            if (!result) evt.setResult(Event.Result.DENY);
        });
    }
}
