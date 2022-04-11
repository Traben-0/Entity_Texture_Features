package traben.entity_texture_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import traben.entity_texture_features.config.ETFConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

//class name must preserve original name format for puzzle mod support
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETF_CLIENT implements ClientModInitializer {
    //0 = vanilla only    1+ is zombie1+.png
    public static final HashMap<String, Integer> etf$PATH_TotalTrueRandom = new HashMap<>();
    public static final HashMap<UUID, Integer> etf$UUID_randomTextureSuffix = new HashMap<>();
    public static final HashMap<UUID, Integer> etf$UUID_randomTextureSuffix2 = new HashMap<>();
    public static final HashMap<UUID, Integer> etf$UUID_randomTextureSuffix3 = new HashMap<>();
    public static final HashMap<UUID, Integer> etf$UUID_randomTextureSuffix4 = new HashMap<>();
    public static final Set<UUID> etf$UUID_entityAlreadyCalculated = new HashSet<>();//
    public static final HashMap<UUID, Long> etf$UUID_entityAwaitingDataClearing = new HashMap<>();
    public static final HashMap<UUID, Long> etf$UUID_entityAwaitingDataClearing2 = new HashMap<>();
    public static final HashMap<String, ArrayList<ETFTexturePropertyCase>> etf$PATH_OptifineRandomSettingsPerTexture = new HashMap<>();
    public static final HashMap<String, Boolean> etf$PATH_OptifineOrTrueRandom = new HashMap<>();
    public static final HashMap<String, Integer> etf$PATH_OptifineOldVanillaetf$0123 = new HashMap<>();// 0,1,2
    public static final HashMap<String, Boolean> etf$PATH_ignoreOnePNG = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_hasUpdatableRandomCases = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_hasUpdatableRandomCases2 = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_hasUpdatableRandomCases3 = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_hasUpdatableRandomCases4 = new HashMap<>();

    //must be 6 length
    // 0 biome
    // 1 height
    // 2 block
    // 3 weather
    // 4 daytime
    // 5 moonphase
    public static final HashMap<UUID, String[]> etf$UUID_OriginalNonUpdatePropertyStrings = new HashMap<>();


    public static final HashMap<UUID, Boolean> etf$UUID_playerHasFeatures = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_playerSkinDownloadedYet = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_playerHasEnchant = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_playerHasEmissive = new HashMap<>();
    public static final HashMap<UUID, Identifier> etf$UUID_playerTransparentSkinId = new HashMap<>();
    public static final HashMap<String, HttpURLConnection> etf$URL_HTTPtoDisconnect1 = new HashMap<>();
    public static final HashMap<String, HttpURLConnection> etf$URL_HTTPtoDisconnect2 = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_playerHasCoat = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_playerHasFatCoat = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_playerHasVillagerNose = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_playerHasCape = new HashMap<>();
    public static final HashMap<UUID, Boolean> etf$UUID_playerHasCustomCape = new HashMap<>();

    public static final HashMap<UUID, Long> etf$UUID_playerLastSkinCheck = new HashMap<>();
    public static final HashMap<UUID, Integer> etf$UUID_playerLastSkinCheckCount = new HashMap<>();

    public static final HashMap<UUID, Long> etf$UUID_NextBlinkTime = new HashMap<>();
    public static final HashMap<String, Boolean> etf$PATH_HasBlink = new HashMap<>();
    public static final HashMap<String, Boolean> etf$PATH_HasBlink2 = new HashMap<>();
    public static final HashMap<String, Properties> etf$PATH_BlinkProps = new HashMap<>();
    public static final HashMap<String, Boolean> etf$PATH_HasOptifineDefaultReplacement = new HashMap<>();

    public static final HashMap<String, Boolean> etf$PATH_VillagerIsExistingFeature = new HashMap<>();

    public static String[] etf$emissiveSuffixes = null;
    public static final HashMap<String, Identifier> etf$PATH_EmissiveTextureIdentifier = new HashMap<>();
    public static boolean etf$irisDetected = false;

    //can't change due to puzzle mod compatability
    public static ETFConfig ETFConfigData;

    public static final HashMap<UUID, String> etf$UUID_TridentName = new HashMap<>();
    public static final Set<String> etf$PATH_FailedPropertiesToIgnore = new HashSet<>();

    public static int etf$mooshroomRedCustomShroom = 0;
    public static int etf$mooshroomBrownCustomShroom = 0;

    public final static String etf$SKIN_NAMESPACE = "etf_skin:";


    @Override
    public void onInitializeClient() {
        //testing
        LogManager.getLogger().info("[Entity Texture Features]: Loading! 1.18.x");
        etf$loadConfig();
    }

    // config code based on bedrockify & actually unbreaking fabric config code
    // https://github.com/juancarloscp52/BedrockIfy/blob/1.17.x/src/main/java/me/juancarloscp52/bedrockify/Bedrockify.java
    // https://github.com/wutdahack/ActuallyUnbreakingFabric/blob/1.18.1/src/main/java/wutdahack/actuallyunbreaking/ActuallyUnbreaking.java
    public void etf$loadConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.exists()) {
            try {
                FileReader fileReader = new FileReader(config);
                ETFConfigData = gson.fromJson(fileReader, ETFConfig.class);
                fileReader.close();
                ETFUtils.saveConfig();
            } catch (IOException e) {
                ETFUtils.modMessage("Config could not be loaded, using defaults", false);
            }
        } else {
            ETFConfigData = new ETFConfig();
            ETFUtils.saveConfig();
        }
    }

}
