package fuzs.respawninganimals.data.client;

import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.respawninganimals.init.ModRegistry;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.add(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE, "Persistent animals");
        builder.addGameRuleDescription(ModRegistry.PERSISTENT_ANIMALS_GAME_RULE, "Animals will stay in the world forever and can only be reproduced from breeding.");
        builder.add(ModRegistry.ANIMAL_MOB_CAP_GAME_RULE, "Animal mob cap");
        builder.addGameRuleDescription(ModRegistry.ANIMAL_MOB_CAP_GAME_RULE, "Constant to help determine when to stop spawning animals in a world.");
    }
}
