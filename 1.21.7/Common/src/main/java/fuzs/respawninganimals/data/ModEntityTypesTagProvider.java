package fuzs.respawninganimals.data;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypesTagProvider extends AbstractTagProvider<EntityType<?>> {

    public ModEntityTypesTagProvider(DataProviderContext context) {
        super(Registries.ENTITY_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        // maulers are made persistent in Mob::finalizeSpawn which they really shouldn't be, but this way we can handle that
        this.tag(ModRegistry.PERSISTENT_ANIMALS_ENTITY_TYPE_TAG)
                .addOptional("friendsandfoes:mauler",
                        "gamma_beasts:banndu",
                        "gamma_beasts:banndu_a",
                        "gamma_beasts:banndu_c",
                        "gamma_beasts:banndu_spawn",
                        "gamma_beasts:careyatros",
                        "gamma_beasts:careyatros_a",
                        "gamma_beasts:careyatros_c",
                        "gamma_beasts:careyatros_spawn",
                        "gamma_beasts:shecko",
                        "gamma_beasts:shecko_a",
                        "gamma_beasts:shecko_c",
                        "gamma_beasts:shecko_spawn",
                        "gamma_beasts:crasu",
                        "gamma_beasts:crasu_a",
                        "gamma_beasts:crasu_c",
                        "gamma_beasts:crasu_spawn",
                        "gamma_beasts:dogishi",
                        "gamma_beasts:dogishi_a",
                        "gamma_beasts:dogishi_c",
                        "gamma_beasts:dogishi_spawn",
                        "gamma_beasts:dosmo",
                        "gamma_beasts:dosmo_a",
                        "gamma_beasts:dosmo_c",
                        "gamma_beasts:dosmo_spawn",
                        "gamma_beasts:grudels",
                        "gamma_beasts:grudels_a",
                        "gamma_beasts:grudels_c",
                        "gamma_beasts:grudels_spawn",
                        "gamma_beasts:zatlah",
                        "gamma_beasts:zatlah_a",
                        "gamma_beasts:zatlah_c",
                        "gamma_beasts:zatlah_spawn",
                        "gamma_beasts:gusnow",
                        "gamma_beasts:gusnow_a",
                        "gamma_beasts:gusnow_c",
                        "gamma_beasts:gusnow_spawn",
                        "gamma_beasts:molluto",
                        "gamma_beasts:molluto_a",
                        "gamma_beasts:molluto_c",
                        "gamma_beasts:molluto_spawn",
                        "gamma_beasts:muba",
                        "gamma_beasts:muba_a",
                        "gamma_beasts:muba_c",
                        "gamma_beasts:muba_spawn",
                        "gamma_beasts:pangolin",
                        "gamma_beasts:pangolin_a",
                        "gamma_beasts:pangolin_c",
                        "gamma_beasts:pangolin_spawn",
                        "gamma_beasts:radioactive_bear",
                        "gamma_beasts:radioactive_bear_a",
                        "gamma_beasts:radioactive_bear_c",
                        "gamma_beasts:radioactive_bear_spawn",
                        "gamma_beasts:skinno",
                        "gamma_beasts:skinno_a",
                        "gamma_beasts:skinno_c",
                        "gamma_beasts:skinno_spawn",
                        "gamma_beasts:sppinto",
                        "gamma_beasts:sppinto_a",
                        "gamma_beasts:sppinto_c",
                        "gamma_beasts:sppinto_spawn");
    }
}
