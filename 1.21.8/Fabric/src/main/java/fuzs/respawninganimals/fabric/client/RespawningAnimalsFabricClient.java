package fuzs.respawninganimals.fabric.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.client.RespawningAnimalsClient;
import net.fabricmc.api.ClientModInitializer;

public class RespawningAnimalsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(RespawningAnimals.MOD_ID, RespawningAnimalsClient::new);
    }
}
