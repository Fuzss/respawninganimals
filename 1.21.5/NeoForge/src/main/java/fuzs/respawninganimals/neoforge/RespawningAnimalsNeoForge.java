package fuzs.respawninganimals.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.data.ModEntityTypesTagProvider;
import fuzs.respawninganimals.neoforge.mixin.accessor.MobNeoForgeAccessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@Mod(RespawningAnimals.MOD_ID)
public class RespawningAnimalsNeoForge {

    public RespawningAnimalsNeoForge() {
        ModConstructor.construct(RespawningAnimals.MOD_ID, RespawningAnimals::new);
        registerEventHandlers(NeoForge.EVENT_BUS);
        DataProviderHelper.registerDataProviders(RespawningAnimals.MOD_ID, ModEntityTypesTagProvider::new);
    }

    private static void registerEventHandlers(IEventBus eventBus) {
        eventBus.addListener((final FinalizeSpawnEvent evt) -> {
            // provides a workaround for some mods not calling super on Mob::finalizeSpawn where this field is set by Forge
            // this runs before Forge sets the field, but when it does this is just overridden again
            ((MobNeoForgeAccessor) evt.getEntity()).respawninganimals$setSpawnType(evt.getSpawnType());
        });
    }
}
