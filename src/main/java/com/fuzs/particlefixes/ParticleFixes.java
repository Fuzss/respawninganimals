package com.fuzs.particlefixes;

import com.fuzs.particlefixes.handler.LoveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(ParticleFixes.MODID)
public class ParticleFixes {

    public static final String MODID = "particlefixes";
    public static final String NAME = "Particle Fixes";
    public static final Logger LOGGER = LogManager.getLogger(ParticleFixes.NAME);

    public ParticleFixes() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void clientSetup(final FMLCommonSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new LoveHandler());

    }
}
