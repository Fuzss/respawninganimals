package fuzs.respawninganimals.data;

import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import fuzs.respawninganimals.init.ModRegistry;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTranslations() {
        this.add(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE, "Persistent animals");
        this.addGameRuleDescription(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE, "Animals will stay in the world forever and can only be reproduced from breeding.");
        this.add(ModRegistry.ANIMAL_MOB_CAP_GAME_RULE, "Animal mob cap");
        this.addGameRuleDescription(ModRegistry.ANIMAL_MOB_CAP_GAME_RULE, "Constant to help determine when to stop spawning animals in a world.");
    }
}
