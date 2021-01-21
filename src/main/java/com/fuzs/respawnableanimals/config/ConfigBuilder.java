package com.fuzs.respawnableanimals.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigBuilder {

    /**
     * after a spec has been built it goes here
     */
    private final EnumMap<ModConfig.Type, ForgeConfigSpec> specs = new EnumMap<>(ModConfig.Type.class);
    /**
     * enum map of builders, initialized with empty builders by default
     */
    private final EnumMap<ModConfig.Type, ForgeConfigSpec.Builder> builders = new EnumMap<>(ModConfig.Type.class);

    /**
     * config type of category currently being built
     */
    private ModConfig.Type activeType;

    /**
     * @return config spec for type common
     */
    public ForgeConfigSpec getCommonSpec() {

        return this.getSpec(ModConfig.Type.COMMON);
    }

    /**
     * @return config spec for type client
     */
    public ForgeConfigSpec getClientSpec() {

        return this.getSpec(ModConfig.Type.CLIENT);
    }

    /**
     * @return config spec for type server
     */
    public ForgeConfigSpec getServerSpec() {

        return this.getSpec(ModConfig.Type.SERVER);
    }

    /**
     * get spec, build from builder if absent
     * @param type type to get spec for
     * @return config spec for type
     */
    @Nullable
    private ForgeConfigSpec getSpec(ModConfig.Type type) {

        // don't create spec out of an empty builder
        if (this.builders.containsKey(type)) {

            return this.specs.computeIfAbsent(type, key -> this.builders.get(key).build());
        }

        return null;
    }

    /**
     * has the spec for this type been built yet (has {@link #getSpec} been called)
     * @param type type to check
     * @return has spec been built
     */
    public boolean isSpecNotBuilt(ModConfig.Type type) {

        return !this.specs.containsKey(type);
    }

    /**
     * @param type type to check
     * @return has spec been loaded by Forge
     */
    public boolean isSpecNotLoaded(ModConfig.Type type) {

        return !this.specs.get(type).isLoaded();
    }

    /**
     * add a spec when building config manually
     * @param spec spec to add
     * @param type type to add
     * @return was adding successful (spec not present yet)
     */
    public boolean addSpec(ForgeConfigSpec spec, ModConfig.Type type) {

        if (this.isSpecNotBuilt(type)) {

            this.specs.put(type, spec);
            return true;
        }

        return false;
    }

    /**
     * @param type type to get
     * @return builder for type
     */
    private ForgeConfigSpec.Builder getBuilder(ModConfig.Type type) {

        return this.builders.computeIfAbsent(type, key -> new ForgeConfigSpec.Builder());
    }

    /**
     * wrap creation of a new category
     * @param category name of new category
     * @param options builder for category
     * @param type config type this category is for
     * @param comments comments to add to category
     */
    public void create(String category, Consumer<ForgeConfigSpec.Builder> options, ModConfig.Type type, String... comments) {

        this.activeType = type;

        ForgeConfigSpec.Builder builder = this.getBuilder(type);
        if (comments.length != 0) {

            builder.comment(comments);
        }

        builder.push(category);
        options.accept(builder);
        builder.pop();

        this.activeType = null;
    }

    /**
     * register all configs from non-empty builders
     * @param context active mod container context
     */
    public void registerConfigs(ModLoadingContext context) {

        for (ModConfig.Type type : ModConfig.Type.values()) {

            ForgeConfigSpec spec = this.getSpec(type);
            if (spec != null) {

                context.registerConfig(type, spec);
            }
        }
    }

    /**
     * @return type of category currently being built
     */
    public ModConfig.Type getActiveType() {

        return this.activeType;
    }

}
