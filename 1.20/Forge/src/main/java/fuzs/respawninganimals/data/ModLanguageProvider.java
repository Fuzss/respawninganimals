package fuzs.respawninganimals.data;

import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTranslations() {
        this.add("gamerule.persistentAnimals", "Persistent animals");
        this.add("gamerule.persistentAnimals.description", "Animals will stay in the world forever and can only be reproduced by breeding.");
    }
}
