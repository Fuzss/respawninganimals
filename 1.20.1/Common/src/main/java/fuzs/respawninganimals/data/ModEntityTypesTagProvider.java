package fuzs.respawninganimals.data;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class ModEntityTypesTagProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypesTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        // maulers are made persistent in Mob::finalizeSpawn which they really shouldn't be, but this way we can handle that
        this.tag(ModRegistry.PERSISTENT_ANIMALS_ENTITY_TYPE_TAG).addOptional(new ResourceLocation("friendsandfoes:mauler"));
    }
}
