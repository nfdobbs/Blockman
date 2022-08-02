package net.dobbs.blockman.util;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private static Config instance = null;
    private static final String CONFIG_PATH = "\\blockman\\config.json";
    private static final String CONFIG_DIR = "\\blockman";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    JsonObject settings = null;

    private Config()
    {
        File file  = new File(FabricLoader.getInstance().getConfigDir() + CONFIG_PATH);
        if(file.exists())
        {
            readConfigFile(file);
        }

        else
        {
            createConfigFile();
        }

    }

    public static Config getInstance()
    {
        if(instance == null)
            instance = new Config();

        return instance;
    }

    private void createConfigFile() {

        //Creating Default Config File
        settings = new JsonObject();

        JsonObject noPurchaseColor = new JsonObject();
        noPurchaseColor.addProperty("red", 255);
        noPurchaseColor.addProperty("green", 0);
        noPurchaseColor.addProperty("blue", 0);
        noPurchaseColor.addProperty("alpha", 255);

        JsonObject canPurchaseColor = new JsonObject();
        canPurchaseColor.addProperty("red", 0);
        canPurchaseColor.addProperty("green", 255);
        canPurchaseColor.addProperty("blue", 0);
        canPurchaseColor.addProperty("alpha", 255);

        settings.add("can_purchase_tiles_color", canPurchaseColor);
        settings.add("no_tiles_available_color", noPurchaseColor);

        //Writing Json to file
        File file = new File(FabricLoader.getInstance().getConfigDir() + CONFIG_DIR);

        if(file.mkdirs())
        {
            System.out.println("Made the blockman config directory");
        }

        try {
            Files.write(Path.of(FabricLoader.getInstance().getConfigDir() + CONFIG_PATH), GSON.toJson(settings).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readConfigFile(File file)
    {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String jsonString = new String(bytes);
        System.out.println(jsonString);

        settings = JsonParser.parseString(jsonString).getAsJsonObject();
    }

    public BlockmanColor getCanPurchaseWallColor()
    {
        BlockmanColor color = new BlockmanColor(0, 255, 0, 168);

        return color;
    }

    public BlockmanColor getNoTilesAvailableWallColor()
    {
        BlockmanColor color = new BlockmanColor(255, 0, 0, 168);

        return color;
    }
}
