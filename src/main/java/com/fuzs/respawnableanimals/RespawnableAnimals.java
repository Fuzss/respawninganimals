package com.fuzs.respawnableanimals;

import com.fuzs.puzzleslib_ra.PuzzlesLib;
import com.fuzs.puzzleslib_ra.config.ConfigManager;
import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(RespawnableAnimals.MODID)
public class RespawnableAnimals extends PuzzlesLib {

    public static final String MODID = "respawnableanimals";
    public static final String NAME = "Respawnable Animals";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public RespawnableAnimals() {

        super();
        RespawnableAnimalsElements.setup(MODID);
        ConfigManager.get().load();
    }

}
