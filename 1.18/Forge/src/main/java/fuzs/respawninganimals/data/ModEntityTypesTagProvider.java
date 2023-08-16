package fuzs.respawninganimals.data;

import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModEntityTypesTagProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypesTagProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags() {
        this.tag(ModRegistry.PERSISTENT_ANIMALS_ENTITY_TYPE_TAG).addOptional(new ResourceLocation("friendsandfoes:mauler"));
    }
}
