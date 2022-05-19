package traben.entity_texture_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.logging.ETFLogger;
import traben.entity_texture_features.client.utils.ETFUtils;
import traben.entity_texture_features.config.ETFConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETFClient implements ClientModInitializer {

    ///list all uuids that have ever been seen by ETF, so they can be selected for random data clearing to save memory
    public static final HashMap<UUID, Integer> KNOWN_UUID_LIST = new HashMap<>();

    //total number of random variations if true random is used / no optifine properties file
    // 0 = vanilla only    1+ is zombie1+.png etc
    public static final HashMap<String, Integer> PATH_TOTAL_TRUE_RANDOM = new HashMap<>();

    //stores the suffix number for that UUID
    // only stores the Integer as opposed to full texture path for memory size concerns
    // a few mobs (mostly villagers :/) have separate textures that can be randomized but would not work nicely keyed to the same Map hence 4 of them
    public static final HashMap<UUID, Integer> UUID_RANDOM_TEXTURE_SUFFIX = new HashMap<>();
    public static final HashMap<UUID, Integer> UUID_RANDOM_TEXTURE_SUFFIX_2 = new HashMap<>();
    public static final HashMap<UUID, Integer> UUID_RANDOM_TEXTURE_SUFFIX_3 = new HashMap<>();
    public static final HashMap<UUID, Integer> UUID_RANDOM_TEXTURE_SUFFIX_4 = new HashMap<>();

    //special case hashmap for villagers and glowing eyes used to decide if a texture feature is to be randomized
    public static final HashMap<String, Boolean> PATH_IS_EXISTING_FEATURE = new HashMap<>();

    //marks entity has already been processed before for random texture application
    public static final Set<UUID> UUID_ENTITY_ALREADY_CALCULATED = new HashSet<>();//

    //periodically applied to entities by UUID based on texture update frequency and adds the system time as a long
    //todo reason for time in long may be recently deprecated, check this
    public static final HashMap<UUID, Long> UUID_ENTITY_AWAITING_DATA_CLEARING = new HashMap<>();
    public static final HashMap<UUID, Long> UUID_ENTITY_AWAITING_DATA_CLEARING_2 = new HashMap<>();

    //holds a Set of optifine property cases object (e.g  names.1, biome.1, all of .1) for a specific texture path
    public static final HashMap<String, Set<ETFTexturePropertyCase>> PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE = new HashMap<>();

    //marks whether a texture path uses optifine properties, or is just random
    //true = optifine properties
    public static final HashMap<String, Boolean> PATH_OPTIFINE_OR_JUST_RANDOM = new HashMap<>();

    // stores an int referring to what location an altered texture is
    // textures can be in the optifine random folder, the old optifine mobs folder, the vanilla folder, or the ETF override folder
    // denoted by the numbers in the same order as appearance in variable name 0123
    public static final HashMap<String, Integer> PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123 = new HashMap<>();// 0,1,2

    //marks whether <textureName>1.png exists for any path, if true it simply uses the vanilla texture if texture 1 is selected in optifine properties
    public static final HashMap<String, Boolean> PATH_IGNORE_ONE_PNG = new HashMap<>();

    //marks whether an entity should be ignored for texture updating if nothing in it can update
    //todo may have been recently deprecated, check this
    public static final HashMap<UUID, Boolean> UUID_HAS_UPDATABLE_RANDOM_CASES = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_HAS_UPDATABLE_RANDOM_CASES_2 = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_HAS_UPDATABLE_RANDOM_CASES_3 = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_HAS_UPDATABLE_RANDOM_CASES_4 = new HashMap<>();

    //stores the initial data of a mob upon spawning, so they can be used if that property is not to be updated over time
    // 0 biome
    // 1 height
    // 2 block
    // 3 weather
    // 4 daytime
    // 5 moonphase
    public static final HashMap<UUID, String[]> UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS = new HashMap<>();

    //stores various info about player skins per UUID
    //probably would benefit from using an object to store these but as not all are used in every skin this might be more efficient in runtime
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_FEATURES = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_ENCHANT = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_EMISSIVE = new HashMap<>();
    public static final HashMap<UUID, Identifier> UUID_PLAYER_TRANSPARENT_SKIN_ID = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_COAT = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_FAT_COAT = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_VILLAGER_NOSE = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_CAPE = new HashMap<>();
    public static final HashMap<UUID, Boolean> UUID_PLAYER_HAS_CUSTOM_CAPE = new HashMap<>();

    //rare cases can cause these not to be closed, just in case these will be closed shortly after use time has passed if they have not already been caught and closed by the system
    public static final HashMap<String, HttpURLConnection> URL_HTTP_TO_DISCONNECT_1 = new HashMap<>();
    public static final HashMap<String, HttpURLConnection> URL_HTTP_TO_DISCONNECT_2 = new HashMap<>();

    //related to repeating HTTP checks a couple of times in case of bad connection
    public static final HashMap<UUID, Long> UUID_PLAYER_LAST_SKIN_CHECK = new HashMap<>();
    public static final HashMap<UUID, Integer> UUID_PLAYER_LAST_SKIN_CHECK_COUNT = new HashMap<>();

    //blinking data
    public static final HashMap<UUID, Long> UUID_NEXT_BLINK_TIME = new HashMap<>();
    public static final HashMap<String, Boolean> PATH_HAS_BLINK_TEXTURE = new HashMap<>();
    public static final HashMap<String, Boolean> PATH_HAS_BLINK_TEXTURE_2 = new HashMap<>();
    public static final HashMap<String, Properties> PATH_BLINK_PROPERTIES = new HashMap<>();

    //marks whether the vanilla texture has an override in the randoms folder (e.g.  creeper.png in the optifine folder)
    public static final HashMap<String, Boolean> PATH_HAS_DEFAULT_REPLACEMENT = new HashMap<>();

    //list of suffixes found in the suffix properties as for some reason people add multiple sometimes
    //also because an option in ETF can add "_e" to this list
    public static String[] emissiveSuffixes = null;

    //stores the identifier for an emissive version of the given texture path
    public static final HashMap<String, Identifier> PATH_EMISSIVE_TEXTURE_IDENTIFIER = new HashMap<>();

    //whether the iris mod was detected on load
    public static boolean irisDetected = false;

    //trident entities do not send item name data to clients when thrown, this is to keep that name in memory so custom tridents can at least display until reloading
    public static final HashMap<UUID, String> UUID_TRIDENT_NAME = new HashMap<>();

    //set of properties that had some issue and could not be read, they need to be ignored in the next attempt
    public static final Set<String> PATH_FAILED_PROPERTIES_TO_IGNORE = new HashSet<>();

    //marks whether mooshroom mushroom overrides exist
    public static int mooshroomRedCustomShroom = 0;
    public static int mooshroomBrownCustomShroom = 0;

    public static Boolean lecternHasCustomTexture = null;

    //string finals
    public final static String SKIN_NAMESPACE = "etf_skin:";
    public static final String MOD_ID = "etf";

    //marks this UUID to have relevant data printed for debugging
    public static final Set<UUID> UUID_DEBUG_EXPLANATION_MARKER = new HashSet<>();

    //config object
    public static ETFConfig ETFConfigData;

    //logging object
    public static ETFLogger LOGGER;// = ETFLogger.create();

    @Override
    public void onInitializeClient() {
        //needs to be created after initialization stage
        LOGGER = ETFLogger.create();

        LOGGER.info("Loading! 1.18.x");

        if (FabricLoader.getInstance().getModContainer("iris").isPresent()) {
            //LOGGER.info("Iris mod detected : message will be shown in settings");
            irisDetected = true;
        }

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
                ETFUtils.logMessage("Config could not be loaded, using defaults", false);
            }
        } else {
            ETFConfigData = new ETFConfig();
            ETFUtils.saveConfig();
        }
    }

}
