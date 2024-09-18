package fuzs.respawninganimals.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.respawninganimals.RespawningAnimals;
import fuzs.respawninganimals.data.ModEntityTypesTagProvider;
import fuzs.respawninganimals.data.client.ModLanguageProvider;
import fuzs.respawninganimals.neoforge.mixin.accessor.MobNeoForgeAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

@Mod(RespawningAnimals.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RespawningAnimalsNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(RespawningAnimals.MOD_ID, RespawningAnimals::new);
        registerHandlers();
        DataProviderHelper.registerDataProviders(RespawningAnimals.MOD_ID,
                ModEntityTypesTagProvider::new,
                ModLanguageProvider::new
        );
    }

    private static void registerHandlers() {
        NeoForge.EVENT_BUS.addListener((final MobSpawnEvent.FinalizeSpawn evt) -> {
            // provides a workaround for some mods not calling super on Mob::finalizeSpawn where this field is set by Forge
            // this runs before Forge sets the field, but when it does this is just overridden again
            ((MobNeoForgeAccessor) evt.getEntity()).respawninganimals$setSpawnType(evt.getSpawnType());
        });
    }
}
