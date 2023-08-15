package fuzs.respawninganimals;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.respawninganimals.data.ModEntityTypesTagProvider;
import fuzs.respawninganimals.data.ModLanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(RespawningAnimals.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RespawningAnimalsForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(RespawningAnimals.MOD_ID, RespawningAnimals::new);
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        evt.getGenerator().addProvider(true, new ModEntityTypesTagProvider(evt, RespawningAnimals.MOD_ID));
        evt.getGenerator().addProvider(true, new ModLanguageProvider(evt, RespawningAnimals.MOD_ID));
    }
}
