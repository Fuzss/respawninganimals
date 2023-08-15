package fuzs.respawninganimals.data;

import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModEntityTypesTagProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypesTagProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.PERSISTENT_ANIMALS_ENTITY_TYPE_TAG);
    }
}
