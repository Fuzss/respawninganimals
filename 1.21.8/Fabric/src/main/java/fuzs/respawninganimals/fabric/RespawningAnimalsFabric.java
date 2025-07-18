package fuzs.respawninganimals.fabric;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.respawninganimals.RespawningAnimals;
import net.fabricmc.api.ModInitializer;

public class RespawningAnimalsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(RespawningAnimals.MOD_ID, RespawningAnimals::new);
    }
}
