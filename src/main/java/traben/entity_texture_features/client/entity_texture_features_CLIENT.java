package traben.entity_texture_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.config.ETFConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class entity_texture_features_CLIENT implements ModInitializer, entity_texture_features_METHODS {
    //0 = vanilla only    1+ is zombie1+.png
    public static Map<String, Integer> Texture_TotalTrueRandom = new HashMap<>();
    public static Map<UUID, Integer> UUID_randomTextureSuffix = new HashMap<>();
    public static ArrayList<UUID> UUID_entityAlreadyCalculated = new ArrayList<>();//
    public static Map<UUID, Long> UUID_entityAwaitingDataClearing = new HashMap<>();
    public static Map<String, ArrayList<randomCase>> Texture_OptifineRandomSettingsPerTexture = new HashMap<>();
    public static Map<String, Boolean> Texture_OptifineOrTrueRandom = new HashMap<>();
    public static Map<String, Integer> optifineOldOrVanilla = new HashMap<>();// 0,1,2
    public static Map<String, Boolean> ignoreOnePNG = new HashMap<>();
    public static Map<UUID, Boolean> hasUpdatableRandomCases = new HashMap<>();

    public static String[] emissiveSuffix = null;
    public static Map<String, Identifier> Texture_Emissive = new HashMap<>();
    public static boolean puzzleDetected = false;
    public static ETFConfig ETFConfigData;

    @Override
    public void onInitialize() {
        System.out.println("[Entity Texture Features]: Loaded!");
        loadConfig();
    }

    // config code based on bedrockify & actually unbreaking fabric config code
    // https://github.com/juancarloscp52/BedrockIfy/blob/1.17.x/src/main/java/me/juancarloscp52/bedrockify/Bedrockify.java
    // https://github.com/wutdahack/ActuallyUnbreakingFabric/blob/1.18.1/src/main/java/wutdahack/actuallyunbreaking/ActuallyUnbreaking.java
    public void loadConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.exists()) {
            try {
                FileReader fileReader = new FileReader(config);
                ETFConfigData = gson.fromJson(fileReader, ETFConfig.class);
                fileReader.close();
                saveConfig();
            } catch (IOException e) {
                modMessage("Config could not be loaded, using defaults", false);
            }
        } else {
            ETFConfigData = new ETFConfig();
            saveConfig();
        }
    }

}
