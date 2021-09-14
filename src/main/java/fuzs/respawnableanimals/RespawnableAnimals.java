package fuzs.respawnableanimals;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.ElementRegistry;
import fuzs.respawnableanimals.element.RespawnableAnimalsElement;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "Convert2MethodRef"})
@Mod(RespawnableAnimals.MODID)
public class RespawnableAnimals {

    public static final String MODID = "respawnableanimals";
    public static final String NAME = "Respawnable Animals";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static final ElementRegistry REGISTRY = PuzzlesLib.create(MODID);

    public static final AbstractElement RESPAWNABLE_ANIMALS = REGISTRY.register("respawnable_animals", () -> new RespawnableAnimalsElement());

    public RespawnableAnimals() {

        PuzzlesLib.setup(true);
        PuzzlesLib.setSideOnly();
    }

}
