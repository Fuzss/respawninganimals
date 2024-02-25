package fuzs.respawninganimals;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.core.DataProviderHelper;
import fuzs.respawninganimals.data.ModEntityTypesTagProvider;
import fuzs.respawninganimals.data.client.ModLanguageProvider;
import fuzs.respawninganimals.mixin.accessor.MobForgeAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(RespawningAnimals.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RespawningAnimalsForge {

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
        MinecraftForge.EVENT_BUS.addListener((final MobSpawnEvent.FinalizeSpawn evt) -> {
            // provides a workaround for some mods not calling super on Mob::finalizeSpawn where this field is set by Forge
            // this runs before Forge sets the field, but when it does this is just overridden again
            ((MobForgeAccessor) evt.getEntity()).respawninganimals$setSpawnType(evt.getSpawnType());
        });
    }
}
