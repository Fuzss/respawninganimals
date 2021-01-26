package com.fuzs.puzzleslib_ra.config.json;

import com.fuzs.puzzleslib_ra.PuzzlesLib;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * handles loading and saving of json config files
 */
public class JSONConfigUtil {

    /**
     * gson builder instance
     */
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * load a json file in the main config directory, create a file if absent
     * @param jsonName name of the file to load
     * @param serializer serializer creates a {@link com.google.gson.JsonElement} and then calls {@link #saveToFile}
     * @param deserializer deserializer feeds a {@link java.io.FileReader} to {@link #GSON} and handles the outcome itself
     */
    public static void load(String jsonName, BiConsumer<String, File> serializer, Consumer<FileReader> deserializer) {

        File jsonFile = getFilePath(jsonName);
        load(jsonName, jsonFile, serializer, deserializer);
    }

    /**
     * load a json file in the mods own config directory inside of the main directory, create a file if absent
     * @param jsonName name of the file to load
     * @param modId config directory name
     * @param serializer serializer creates a {@link com.google.gson.JsonElement} and then calls {@link #saveToFile}
     * @param deserializer deserializer feeds a {@link java.io.FileReader} to {@link #GSON} and handles the outcome itself
     */
    public static void load(String jsonName, String modId, BiConsumer<String, File> serializer, Consumer<FileReader> deserializer) {

        File jsonFile = getFolderPath(jsonName, modId);
        load(jsonName, jsonFile, serializer, deserializer);
    }

    /**
     * load a json file, create a file if absent
     * @param jsonName name of the file to load
     * @param jsonFile file to read from, or to write to when absent
     * @param serializer serializer creates a {@link com.google.gson.JsonElement} and then calls {@link #saveToFile}
     * @param deserializer deserializer feeds a {@link java.io.FileReader} to {@link #GSON} and handles the outcome itself
     */
    private static void load(String jsonName, File jsonFile, BiConsumer<String, File> serializer, Consumer<FileReader> deserializer) {

        createIfAbsent(jsonName, jsonFile, serializer);
        loadFromFile(jsonName, jsonFile, deserializer);
    }

    /**
     * create file and corresponding directory if absent
     * @param jsonName file name for error logging
     * @param jsonFile file to create if absent
     * @param serializer serializer creates a {@link com.google.gson.JsonElement} and then calls {@link #saveToFile}
     */
    private static void createIfAbsent(String jsonName, File jsonFile, BiConsumer<String, File> serializer) {

        if (!jsonFile.exists()) {

            jsonFile.getParentFile().mkdir();
            serializer.accept(jsonName, jsonFile);
        }
    }

    /**
     * copy <code>jsonName</code> file from classpath to <code>jsonFile</code>
     * @param jsonName name of source file somewhere in classpath
     * @param jsonFile destination file
     */
    public static void copyToFile(String jsonName, File jsonFile) {

        try (InputStream stream = JSONConfigUtil.class.getResourceAsStream(jsonName)) {

            jsonFile.createNewFile();
            byte[] buffer = new byte[16384];
            FileOutputStream out = new FileOutputStream(jsonFile);
            int lengthRead = stream.read(buffer);
            if (lengthRead <= 0) {

                PuzzlesLib.LOGGER.error("Failed to copy {} in config directory: {}", jsonName, "Empty Buffer");
            }

            for (; lengthRead > 0; lengthRead = stream.read(buffer)) {

                out.write(buffer, 0, lengthRead);
                out.flush();
            }

            out.close();
        } catch (Exception e) {

            PuzzlesLib.LOGGER.error("Failed to copy {} in config directory: {}", jsonName, e);
        }
    }

    /**
     * save <code>jsonElement</code> to <code>jsonFile</code>
     * @param jsonName file name for error logging
     * @param jsonFile file to save to
     * @param jsonElement {@link com.google.gson.JsonElement} to save
     */
    public static void saveToFile(String jsonName, File jsonFile, JsonElement jsonElement) {

        try (FileWriter writer = new FileWriter(jsonFile)) {

            GSON.toJson(jsonElement, writer);
        } catch (Exception e) {

            PuzzlesLib.LOGGER.error("Failed to create {} in config directory: {}", jsonName, e);
        }
    }

    /**
     * creates a {@link java.io.FileReader} for <code>file</code> and gives it to a <code>deserializer</code>
     * @param jsonName file name for error logging
     * @param file file to load
     * @param deserializer deserializer feeds a {@link java.io.FileReader} to {@link #GSON} and handles the outcome itself
     */
    private static void loadFromFile(String jsonName, File file, Consumer<FileReader> deserializer) {

        try (FileReader reader = new FileReader(file)) {

            deserializer.accept(reader);
        } catch (Exception e) {

            PuzzlesLib.LOGGER.error("Failed to read {} in config directory: {}", jsonName, e);
        }
    }

    /**
     * get file in main config directory
     * @param jsonName file to get
     * @return file
     */
    private static File getFilePath(String jsonName) {

        return new File(FMLPaths.CONFIGDIR.get().toFile(), jsonName);
    }

    /**
     * get file in the mod's own config directory
     * @param jsonName file to get
     * @param modId config directory name
     * @return file
     */
    private static File getFolderPath(String jsonName, String modId) {

        return new File(FMLPaths.CONFIGDIR.get().toFile(), modId + File.separator + jsonName);
    }

}
