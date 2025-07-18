package fuzs.respawninganimals.neoforge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.client.RespawningAnimalsClient;
import fuzs.respawninganimals.data.client.ModLanguageProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = RespawningAnimals.MOD_ID, dist = Dist.CLIENT)
public class RespawningAnimalsNeoForgeClient {

    public RespawningAnimalsNeoForgeClient() {
        ClientModConstructor.construct(RespawningAnimals.MOD_ID, RespawningAnimalsClient::new);
        DataProviderHelper.registerDataProviders(RespawningAnimals.MOD_ID, ModLanguageProvider::new);
    }
}
