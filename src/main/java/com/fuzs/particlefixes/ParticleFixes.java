package com.fuzs.particlefixes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ParticleFixes.MODID,
        name = ParticleFixes.NAME,
        version = ParticleFixes.VERSION,
        acceptedMinecraftVersions = ParticleFixes.RANGE,
        acceptableRemoteVersions = ParticleFixes.REMOTE,
        certificateFingerprint = ParticleFixes.FINGERPRINT
)
public class ParticleFixes {
    public static final String MODID = "particlefixes";
    public static final String NAME = "Particle Fixes";
    public static final String VERSION = "@VERSION@";
    public static final String RANGE = "[1.12.2]";
    public static final String REMOTE = "*";
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static final Logger LOGGER = LogManager.getLogger(ParticleFixes.NAME);

    @EventHandler
    public void fingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
