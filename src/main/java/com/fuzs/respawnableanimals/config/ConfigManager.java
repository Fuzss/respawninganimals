package com.fuzs.respawnableanimals.config;

import com.fuzs.respawnableanimals.RespawnableAnimals;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * main config manager for this mod
 */
@SuppressWarnings("unused")
public class ConfigManager {

    /**
     * singleton instance
     */
    private static ConfigManager instance;

    /**
     * config build helpers for each mod separately since they store the forge builders and specs
     */
    private final Map<String, ConfigBuilder> configBuilders = Maps.newHashMap();
    /**
     * all config entries as a set
     */
    private final Map<String, ConfigEntry<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> configEntries = Maps.newHashMap();
    /**
     * listeners to call when a config is somehow loaded
     */
    private final Map<Runnable, ConfigLoadState> configListeners = Maps.newHashMap();

    /**
     * this class is a singleton
     */
    private ConfigManager() {

    }

    /**
     * register configs from non-empty builders and add listener from active mod container to {@link #onModConfig}
     */
    public void load() {

        this.getBuilder().registerConfigs(ModLoadingContext.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfig);
    }

    /**
     * fires on both "loading" and "reloading", "loading" phase is required for initial setup
     * @param evt event provided by Forge
     */
    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        String modid = evt.getConfig().getModId();
        ModConfig.Type type = evt.getConfig().getType();
        if (this.getBuilder(modid).isSpecNotBuilt(type) || this.getBuilder(modid).isSpecNotLoaded(type)) {

            RespawnableAnimals.LOGGER.error("Unable to get values from config during " + (evt instanceof ModConfig.Loading ? "loading" : "reloading") + " phase: " + "Config spec not present");
        } else {

            this.syncType(modid, type);
            this.notifyListeners(ConfigLoadState.getState(evt));
        }
    }

    /**
     * sync all config entries no matter which type
     */
    public void sync(String modid) {

        this.getEntriesForMod(modid).forEach(ConfigEntry::sync);
    }

    /**
     * sync config entries for specific type of config
     * @param type type of config to sync
     */
    private void syncType(String modid, ModConfig.Type type) {

        this.getEntriesForMod(modid).filter(configValue -> configValue.getType() == type).forEach(ConfigEntry::sync);
    }

    /**
     * @param modid mod to get entries for
     * @return stream of entries only for this mod
     */
    private Stream<ConfigEntry<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> getEntriesForMod(String modid) {

        return this.configEntries.values().stream().filter(entry -> entry.getModId().equals(modid));
    }

    /**
     * @param paths individual parts of path for config value
     * @return the config value
     */
    public Object getValueFromPath(String... paths) {

        return this.getValueFromPath(String.join(".", paths));
    }

    /**
     * @param path path for config value
     * @return the config value
     */
    public Object getValueFromPath(String path) {

        Optional<Object> optional = Optional.ofNullable(this.configEntries.get(path)).map(ConfigEntry::getValue);
        if (optional.isPresent()) {

            return optional.get();
        }

        throw new RuntimeException("Unable to get config value for path \"" + path + "\": " + "Path not found");
    }

    /**
     * register config entry on both client and server
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerCommonEntry(S entry, Consumer<T> action) {

        this.registerEntry(ModConfig.Type.COMMON, entry, action, Function.identity());
    }

    /**
     * register config entry on the client
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerClientEntry(S entry, Consumer<T> action) {

        this.registerEntry(ModConfig.Type.CLIENT, entry, action, Function.identity());
    }

    /**
     * register config entry on the server
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerServerEntry(S entry, Consumer<T> action) {

        this.registerEntry(ModConfig.Type.SERVER, entry, action, Function.identity());
    }

    /**
     * register config entry for active type
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerEntry(S entry, Consumer<T> action) {

        this.registerEntry(entry, action, Function.identity());
    }

    /**
     * register config entry for active type
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param transformer transformation to apply when returning value
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param <R> final return type of config entry
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T, R> void registerEntry(S entry, Consumer<R> action, Function<T, R> transformer) {

        ModConfig.Type activeType = this.getBuilder().getActiveType();
        if (activeType == null) {

            RespawnableAnimals.LOGGER.error("Unable to register config entry: " + "Active builder is null");
        } else if (this.getBuilder().isSpecNotBuilt(activeType)) {

            this.registerEntry(activeType, entry, action, transformer);
        } else {

            RespawnableAnimals.LOGGER.error("Unable to register config entry: " + "Config spec already built");
        }
    }

    /**
     * register config entry for given type
     * @param type type of config to register for
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param transformer transformation to apply when returning value
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param <R> final return type of config entry
     */
    private <S extends ForgeConfigSpec.ConfigValue<T>, T, R> void registerEntry(ModConfig.Type type, S entry, Consumer<R> action, Function<T, R> transformer) {

        this.configEntries.put(String.join(".", entry.getPath()), new ConfigEntry<>(type, entry, action, transformer, this.getActiveNamespace()));
    }

    /**
     * add a listener for when the config is loaded and reloaded
     * @param listener listener to add
     */
    public void addListener(Runnable listener) {

        this.addListener(listener, ConfigLoadState.BOTH);
    }

    /**
     * add a listener for when the config is loaded
     * @param listener listener to add
     */
    public void addLoadingListener(Runnable listener) {

        this.addListener(listener, ConfigLoadState.LOADING);
    }

    /**
     * add a listener for when the config is reloaded
     * @param listener listener to add
     */
    public void addReloadingListener(Runnable listener) {

        this.addListener(listener, ConfigLoadState.RELOADING);
    }

    /**
     * add a listener for when the config is somehow loaded
     * @param listener listener to add
     * @param state load states when to call this listener
     */
    private void addListener(Runnable listener, ConfigLoadState state) {

        this.configListeners.merge(listener, state, (state1, state2) -> state1 != state2 ? ConfigLoadState.BOTH : state1);
    }

    /**
     * call listeners for state as the config has somehow been loaded
     * @param state config load state
     */
    private void notifyListeners(ConfigLoadState state) {

        this.configListeners.entrySet().stream().filter(entry -> entry.getValue().matches(state)).map(Map.Entry::getKey).forEach(Runnable::run);
    }

    /**
     * @param type type of config
     * @param modId modid this config belongs to
     * @return config name as if it were generated by Forge itself
     */
    public static String getConfigName(ModConfig.Type type, String modId) {

        return String.format("%s-%s.toml", modId, type.extension());
    }

    /**
     * put config into it's own folder when there are multiples
     * @param type type of config
     * @param modId modid this config belongs to
     * @return name lead by folder
     */
    public static String getConfigNameInFolder(ModConfig.Type type, String modId) {

        return modId + File.separator + getConfigName(type, modId);
    }

    /**
     * @param entries entries to convert to string
     * @param <T> registry element type
     * @return entries as string list
     */
    @SafeVarargs
    public final <T extends IForgeRegistryEntry<T>> List<String> getKeyList(T... entries) {

        return Stream.of(entries)
                .map(IForgeRegistryEntry::getRegistryName)
                .filter(Objects::nonNull)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    /**
     * get active modid so entries can still be associated with the mod
     * @return active modid
     */
    private String getActiveNamespace() {

        return ModLoadingContext.get().getActiveNamespace();
    }

    /**
     * get builder for active mod, create if not present
     * @return builder for active mod
     */
    private ConfigBuilder getBuilder() {

        return this.getBuilder(this.getActiveNamespace());
    }

    /**
     * get builder for a given mod, create if not present
     * @param modid modid to get builder for
     * @return builder for active mod
     */
    private ConfigBuilder getBuilder(String modid) {

        return this.configBuilders.computeIfAbsent(modid, key -> new ConfigBuilder());
    }

    /**
     * @return instance of this
     */
    public static ConfigManager get() {

        if (instance == null) {

            instance = new ConfigManager();
        }

        return instance;
    }

    /**
     * get builder directly
     * @return builder for active mod
     */
    public static ConfigBuilder builder() {

        return get().getBuilder();
    }

    /**
     * internal storage for registered config entries
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    private static class ConfigEntry<S extends ForgeConfigSpec.ConfigValue<T>, T, R> {

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
        ConfigEntry(ModConfig.Type type, S entry, Consumer<R> action, Function<T, R> transformer, String modid) {

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

    /**
     * state for when to trigger listeners
     */
    private enum ConfigLoadState {

        LOADING, RELOADING, BOTH;

        /**
         * check if two states are compatible
         * @param state state to match with
         * @return are states compatible
         */
        boolean matches(ConfigLoadState state) {

            if (state == BOTH || this == BOTH) {

                return true;
            } else if (state == LOADING && this != RELOADING) {

                return true;
            }

            return state == RELOADING && this != LOADING;
        }

        /**
         * get state for an event object
         * @param evt event to get state for
         * @return state for event
         */
        static ConfigLoadState getState(ModConfig.ModConfigEvent evt) {

            return evt instanceof ModConfig.Loading ? LOADING : RELOADING;
        }

    }

}
