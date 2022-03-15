package traben.entity_texture_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import traben.entity_texture_features.config.ETFConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;



@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETF_CLIENT implements ModInitializer, ETF_METHODS {
    //0 = vanilla only    1+ is zombie1+.png
    public static HashMap<String, Integer> Texture_TotalTrueRandom = new HashMap<>();
    public static HashMap<UUID, Integer> UUID_randomTextureSuffix = new HashMap<>();
    public static Set<UUID> UUID_entityAlreadyCalculated = new HashSet<>();//
    public static HashMap<UUID, Long> UUID_entityAwaitingDataClearing = new HashMap<>();
    public static HashMap<String, ArrayList<randomCase>> Texture_OptifineRandomSettingsPerTexture = new HashMap<>();
    public static HashMap<String, Boolean> Texture_OptifineOrTrueRandom = new HashMap<>();
    public static HashMap<String, Integer> optifineOldOrVanilla = new HashMap<>();// 0,1,2
    public static HashMap<String, Boolean> ignoreOnePNG = new HashMap<>();
    public static HashMap<UUID, Boolean> hasUpdatableRandomCases = new HashMap<>();


    public static HashMap<UUID, Boolean> UUID_playerHasFeatures = new HashMap<>();
    public static HashMap<UUID, Boolean> UUID_playerSkinDownloadedYet = new HashMap<>();
    public static HashMap<UUID, Boolean> UUID_playerHasEnchant = new HashMap<>();
    public static HashMap<UUID, Boolean> UUID_playerHasEmissive = new HashMap<>();
    public static HashMap<UUID, Identifier> UUID_playerTransparentSkinId = new HashMap<>();
    public static HashMap<String, HttpURLConnection> URL_HTTPtoDisconnect1 = new HashMap<>();
    public static HashMap<String, HttpURLConnection> URL_HTTPtoDisconnect2 = new HashMap<>();
    public static HashMap<UUID, Boolean> UUID_playerHasCoat = new HashMap<>();
    public static HashMap<UUID, Boolean> UUID_playerHasFatCoat = new HashMap<>();
    public static HashMap<UUID, Boolean> UUID_playerHasVillagerNose = new HashMap<>();
    public static HashMap<UUID, Boolean> UUID_playerHasCape = new HashMap<>();
    public static HashMap<UUID, Boolean> UUID_playerHasCustomCape = new HashMap<>();

    public static HashMap<String, Boolean> TEXTURE_HasBlink = new HashMap<>();
    public static HashMap<String, Boolean> TEXTURE_HasBlink2 = new HashMap<>();
    public static HashMap<String, Properties> TEXTURE_BlinkProps = new HashMap<>();

    public static HashMap<String, Boolean> TEXTURE_VillagerIsExistingFeature = new HashMap<>();

    public static String[] emissiveSuffix = null;
    public static HashMap<String, Identifier> Texture_Emissive = new HashMap<>();
    public static boolean irisDetected = false;
    public static ETFConfig ETFConfigData;

    public static HashMap<UUID, String> UUID_TridentName = new HashMap<>();
    public static Set<String> PATH_FailedPropertiesToIgnore = new HashSet<>();

    public static int mooshroomRedCustomShroom = 0;
    public static int mooshroomBrownCustomShroom = 0;

    public final static String SKIN_NAMESPACE = "etf_skin:";


    @Override
    public void onInitialize() {
        //testing
        LogManager.getLogger().info("[Entity Texture Features]: Loading! 1.18.x");
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
