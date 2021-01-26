package com.fuzs.puzzleslib_ra.config;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * internal storage for registered config entries
 * @param <S> config value of a certain type
 * @param <T> type for value
 */
class ConfigValueEntry<S extends ForgeConfigSpec.ConfigValue<T>, T, R> {

    /**
     * config type of this entry
     */
    final ModConfig.Type type;
    /**
     * config value entry
     */
    final S entry;
    /**
     * action to perform when the entry is updated
     */
    final Consumer<R> action;
    /**
     * transformation to apply when returning value, usually {@link Function#identity}
     */
    final Function<T, R> transformer;
    /**
     * source mod this entry belongs to
     */
    final String modid;

    /**
     * new entry storage
     */
    ConfigValueEntry(ModConfig.Type type, S entry, Consumer<R> action, Function<T, R> transformer, String modid) {

        this.type = type;
        this.entry = entry;
        this.action = action;
        this.transformer = transformer;
        this.modid = modid;
    }

    /**
     * get type for filtering purposes
     * @return type of this
     */
    ModConfig.Type getType() {

        return this.type;
    }

    /**
     * get modid for filtering purposes
     * @return modid associated with this
     */
    String getModId() {

        return this.modid;
    }

    /**
     * @return current value from entry
     */
    R getValue() {

        return this.transformer.apply(this.entry.get());
    }

    /**
     * get value from config value and supply it to consumer
     */
    void sync() {

        this.action.accept(this.getValue());
    }

}
