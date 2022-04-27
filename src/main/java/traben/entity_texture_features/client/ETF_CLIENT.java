package traben.entity_texture_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.ETFConfigScreen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;


@Mod("etf")
public class ETF_CLIENT implements ETF_METHODS {
    public static final Path CONFIG_DIR = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());

    //0 = vanilla only    1+ is zombie1+.png
    public static final HashMap<String, Integer> ETF_PATH_TotalTrueRandom = new HashMap<>();
    public static final HashMap<UUID, Integer> ETF_UUID_randomTextureSuffix = new HashMap<>();
    public static final HashMap<UUID, Integer> ETF_UUID_randomTextureSuffix2 = new HashMap<>();
    public static final HashMap<UUID, Integer> ETF_UUID_randomTextureSuffix3 = new HashMap<>();
    public static final HashMap<UUID, Integer> ETF_UUID_randomTextureSuffix4 = new HashMap<>();
    public static final Set<UUID> ETF_UUID_entityAlreadyCalculated = new HashSet<>();//
    public static final HashMap<UUID, Long> ETF_UUID_entityAwaitingDataClearing = new HashMap<>();
    public static final HashMap<UUID, Long> ETF_UUID_entityAwaitingDataClearing2 = new HashMap<>();
    public static final HashMap<String, ArrayList<randomCase>> ETF_PATH_OptifineRandomSettingsPerTexture = new HashMap<>();
    public static final HashMap<String, Boolean> ETF_PATH_OptifineOrTrueRandom = new HashMap<>();
    public static final HashMap<String, Integer> ETF_PATH_OptifineOldVanillaETF_0123 = new HashMap<>();// 0,1,2
    public static final HashMap<String, Boolean> ETF_PATH_ignoreOnePNG = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_hasUpdatableRandomCases = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_hasUpdatableRandomCases2 = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_hasUpdatableRandomCases3 = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_hasUpdatableRandomCases4 = new HashMap<>();

    //must be 6 length
    // 0 biome
    // 1 height
    // 2 block
    // 3 weather
    // 4 daytime
    // 5 moonphase
    public static final HashMap<UUID, String[]> ETF_UUID_OriginalNonUpdatePropertyStrings = new HashMap<>();


    public static final HashMap<UUID, Boolean> ETF_UUID_playerHasFeatures = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_playerSkinDownloadedYet = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_playerHasEnchant = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_playerHasEmissive = new HashMap<>();
    public static final HashMap<UUID, Identifier> ETF_UUID_playerTransparentSkinId = new HashMap<>();
    public static final HashMap<String, HttpURLConnection> ETF_URL_HTTPtoDisconnect1 = new HashMap<>();
    public static final HashMap<String, HttpURLConnection> ETF_URL_HTTPtoDisconnect2 = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_playerHasCoat = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_playerHasFatCoat = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_playerHasVillagerNose = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_playerHasCape = new HashMap<>();
    public static final HashMap<UUID, Boolean> ETF_UUID_playerHasCustomCape = new HashMap<>();

    public static final HashMap<UUID, Long> ETF_UUID_playerLastSkinCheck = new HashMap<>();
    public static final HashMap<UUID, Integer> ETF_UUID_playerLastSkinCheckCount = new HashMap<>();

    public static final HashMap<UUID, Long> ETF_UUID_NextBlinkTime = new HashMap<>();
    public static final HashMap<String, Boolean> ETF_PATH_HasBlink = new HashMap<>();
    public static final HashMap<String, Boolean> ETF_PATH_HasBlink2 = new HashMap<>();
    public static final HashMap<String, Properties> ETF_PATH_BlinkProps = new HashMap<>();
    public static final HashMap<String, Boolean> ETF_PATH_HasOptifineDefaultReplacement = new HashMap<>();

    public static final HashMap<String, Boolean> ETF_PATH_VillagerIsExistingFeature = new HashMap<>();

    public static String[] ETF_emissiveSuffixes = null;
    public static final HashMap<String, Identifier> ETF_PATH_EmissiveTextureIdentifier = new HashMap<>();
    public static boolean ETF_irisDetected = false;
    public static ETFConfig ETFConfigData;

    public static final HashMap<UUID, String> ETF_UUID_TridentName = new HashMap<>();
    public static final Set<String> ETF_PATH_FailedPropertiesToIgnore = new HashSet<>();

    public static int ETF_mooshroomRedCustomShroom = 0;
    public static int ETF_mooshroomBrownCustomShroom = 0;

    public final static String ETF_SKIN_NAMESPACE = "etf_skin:";

    public ETF_CLIENT() {
        if(FMLEnvironment.dist == Dist.CLIENT) {
            LogManager.getLogger().info("[Entity Texture Features]: Loading! 1.18.x");
            ETF_loadConfig();
            // Register the configuration GUI factory
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigGuiHandler.ConfigGuiFactory.class,
                    () -> new ConfigGuiHandler.ConfigGuiFactory((minecraftClient, screen) -> new ETFConfigScreen().getConfigScreen(screen, false)));
            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        } else {
            LogManager.getLogger().info("[Entity Texture Features]: Attempting to load a clientside only mod on the server, refusing.");
            throw new UnsupportedOperationException("Attempting to load a clientside only mod on the server, refusing");
        }
    }

    // config code based on bedrockify & actually unbreaking fabric config code
    // https://github.com/juancarloscp52/BedrockIfy/blob/1.17.x/src/main/java/me/juancarloscp52/bedrockify/Bedrockify.java
    // https://github.com/wutdahack/ActuallyUnbreakingFabric/blob/1.18.1/src/main/java/wutdahack/actuallyunbreaking/ActuallyUnbreaking.java
    public void ETF_loadConfig() {
        File config = new File(CONFIG_DIR.toFile(), "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.exists()) {
            try {
                FileReader fileReader = new FileReader(config);
                ETFConfigData = gson.fromJson(fileReader, ETFConfig.class);
                fileReader.close();
                ETF_saveConfig();
            } catch (IOException e) {
                ETF_modMessage("Config could not be loaded, using defaults", false);
            }
        } else {
            ETFConfigData = new ETFConfig();
            ETF_saveConfig();
        }
    }

}
