package com.fuzs.puzzleslib_ra.config.deserialize;

import com.fuzs.puzzleslib_ra.PuzzlesLib;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * parser logic for collection builder
 * @param <T> content type of collection to build
 */
public class StringEntryReader<T extends IForgeRegistryEntry<T>> {

    /**
     * registry to work with
     */
    private final IForgeRegistry<T> activeRegistry;

    /**
     * @param registry registry entries the to be created collections contain
     */
    protected StringEntryReader(IForgeRegistry<T> registry) {
        
        this.activeRegistry = registry;
    }

    /**
     * takes a string and finds all matching resource locations, can be multiples since wildcards are supported
     * @param source string to generate resource location from
     * @return list of matches
     */
    protected final List<T> getEntriesFromRegistry(String source) {

        List<T> foundEntries = Lists.newArrayList();
        if (source.contains("*")) {

            // an asterisk is present, so attempt to find entries including a wildcard
            foundEntries.addAll(this.getWildcardEntries(source));
        } else {

            Optional<ResourceLocation> location = Optional.ofNullable(ResourceLocation.tryCreate(source));
            if (location.isPresent()) {

                // when it's present there can't be a wildcard parameter
                Optional<T> entry = this.getEntryFromRegistry(location.get());
                entry.ifPresent(foundEntries::add);
            } else {

                log(source, "Entry not found");
            }
        }

        return foundEntries;
    }

    /**
     * finds the location in the active registry, otherwise the optional is empty
     * @param location location to get entry for
     * @return optional entry if found
     */
    private Optional<T> getEntryFromRegistry(ResourceLocation location) {

        if (this.activeRegistry.containsKey(location)) {

            return Optional.ofNullable(this.activeRegistry.getValue(location));
        } else {

            log(location.toString(), "Entry not found");
        }

        return Optional.empty();
    }

    /**
     * split string into namespace and key to be further processed
     * @param source string to get entries for
     * @return all the entries found
     */
    private List<T> getWildcardEntries(String source) {

        String[] s = source.split(":");
        switch (s.length) {

            case 1:

                // no colon found, so this must be an entry from Minecraft
                return this.getListFromRegistry("minecraft", s[0]);
            case 2:

                return this.getListFromRegistry(s[0], s[1]);
            default:

                log(source, "Invalid resource location format");
                return Lists.newArrayList();
        }
    }

    /**
     * create list with entries from given namespace matching given wildcard path
     * @param namespace namespace to check
     * @param path path string including wildcard
     * @return all entries found
     */
    private List<T> getListFromRegistry(String namespace, String path) {

        String regexPath = path.replace("*", "[a-z0-9/._-]*");
        List<T> entries = this.activeRegistry.getEntries().stream()
                .filter(entry -> entry.getKey().getRegistryName().getNamespace().equals(namespace))
                .filter(entry -> entry.getKey().getRegistryName().getPath().matches(regexPath))
                .map(Map.Entry::getValue).collect(Collectors.toList());

        if (entries.isEmpty()) {

            log(new ResourceLocation(namespace, path).toString(), "Entry not found");
        }

        return entries;
    }

    /**
     * checks if a collection already contains an entry, if that's the case an error is logged
     * @param collection collection to search in
     * @param entry entry to search for
     * @return is the entry contained in the given collection
     */
    protected final boolean isNotPresent(Collection<T> collection, T entry) {

        if (collection.contains(entry)) {

            log(Objects.requireNonNull(entry.getRegistryName()).toString(), "Already present");
            return false;
        }

        return true;
    }

    /**
     * log a warning when there is a problem with a certain entry
     * @param entry problematic entry
     * @param message message to print
     */
    protected static void log(String entry, String message) {

        PuzzlesLib.LOGGER.warn("Unable to parse entry \"{}\": {}", entry, message);
    }
    
}