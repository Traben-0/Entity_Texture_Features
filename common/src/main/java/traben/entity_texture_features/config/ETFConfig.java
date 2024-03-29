package traben.entity_texture_features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static traben.entity_texture_features.ETFClientCommon.CONFIG_DIR;
import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@SuppressWarnings("CanBeFinal")
public class ETFConfig {

    @NotNull
    public static ETFConfig getInstance() {
        if(instance == null){
            instance = new ETFConfig();
        }
        return instance;
    }

    public static void setInstance(ETFConfig newConfigInstance) {
        instance = newConfigInstance;
    }

    public static void loadConfig() {
        try {
            File config = new File(ETFClientCommon.CONFIG_DIR, "entity_texture_features.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (config.exists()) {
                try {
                    FileReader fileReader = new FileReader(config);
                    instance = gson.fromJson(fileReader, ETFConfig.class);
                    fileReader.close();
                    saveConfig();
                } catch (IOException e) {
                    ETFUtils2.logMessage("Config could not be loaded, using defaults", false);
                    instance = new ETFConfig();
                    saveConfig();
                    ETFClientCommon.configHadLoadError = true;
                }
            } else {
                instance = new ETFConfig();
                saveConfig();
            }
            if (instance == null) {
                ETFUtils2.logMessage("Config was null, using defaults", false);
                instance = new ETFConfig();
                saveConfig();
                ETFClientCommon.configHadLoadError = true;
            }
        } catch (Exception e) {
            ETFUtils2.logError("Config was corrupt or broken, using defaults", false);
            instance = new ETFConfig();
            saveConfig();
            ETFClientCommon.configHadLoadError = true;
        }
    }

    public static void saveConfig() {
        if(instance == null) ETFUtils2.logError("Config file could not be saved: null", false);
        File config = new File(CONFIG_DIR, "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(instance));
            fileWriter.close();
        } catch (IOException e) {
            ETFUtils2.logError("Config file could not be saved: "+e.getMessage(), false);
        }
    }

    //config object
    private static ETFConfig instance = null;

    public IllegalPathMode illegalPathSupportMode = IllegalPathMode.None;

    public boolean enableCustomTextures = true;
    public boolean enableCustomBlockEntities = true;
    public UpdateFrequency textureUpdateFrequency_V2 = UpdateFrequency.Fast;
    //    public boolean restrictUpdateProperties = true;
    public boolean restrictBiome = true;
    public boolean restrictHeight = true;
    public boolean restrictBlock = true;
    public boolean restrictWeather = true;
    public boolean restrictDayTime = true;
    public boolean restrictMoonPhase = true;
    public boolean enableEmissiveTextures = true;
    public boolean enableEnchantedTextures = true;
    public boolean enableEmissiveBlockEntities = true;

    public ETFManager.EmissiveRenderModes emissiveRenderMode = ETFManager.EmissiveRenderModes.DULL;


    public boolean alwaysCheckVanillaEmissiveSuffix = true;

    public boolean enableArmorAndTrims = true;

    public boolean skinFeaturesEnabled = true;
    public boolean skinFeaturesEnableTransparency = true;
    public boolean skinFeaturesEnableFullTransparency = false;

    public boolean tryETFTransparencyForAllSkins = false;
    //public boolean skinFeaturesPrintETFReadySkin = false;
    public boolean enableEnemyTeamPlayersSkinFeatures = true;
    public boolean enableBlinking = true;
    public int blinkFrequency = 150;
    public int blinkLength = 1;

    public double advanced_IncreaseCacheSizeModifier = 1.0;

    public DebugLogMode debugLoggingMode = DebugLogMode.None;
    public boolean logTextureDataInitialization = false;


    public Set<String> ignoredConfigs2 = new HashSet<>();

    public boolean hideConfigButton = false;

    public boolean disableVanillaDirectoryVariantTextures = false;

    public boolean use3DSkinLayerPatch = true;

    public boolean enableFullBodyWardenTextures = true;

    //string name stuff more in-depth than other enum for backwards compatibility

    public static ETFConfig copyFrom(ETFConfig source) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.fromJson(gson.toJson(source), ETFConfig.class);
    }

    @SuppressWarnings({"unused", "EnhancedSwitchMigration"})
    public enum UpdateFrequency {
        Never(-1),
        Slow(80),
        Average(20),
        Fast(5),
        Instant(1);

        final private int delay;

        UpdateFrequency(int delay) {
            this.delay = delay;
        }

        public int getDelay() {
            return delay;
        }

        @Override
        public String toString() {
            return ETFVersionDifferenceHandler.getTextFromTranslation(getKey()).getString();
        }

        private String getKey() {
            //non enhanced switch for back compatibility
            //noinspection EnhancedSwitchMigration
            switch (delay) {
                case -1:
                    return "config." + MOD_ID + ".update_frequency.never";
                case 80:
                    return "config." + MOD_ID + ".update_frequency.slow";
                case 20:
                    return "config." + MOD_ID + ".update_frequency.average";
                case 5:
                    return "config." + MOD_ID + ".update_frequency.fast";
                case 1:
                    return "config." + MOD_ID + ".update_frequency.instant";
                default:
                    return "config." + MOD_ID + ".error";
            }
        }

        public UpdateFrequency next() {
            //not enhanced for 1.16 version compat
            switch (this) {
                case Never:
                    return Slow;
                case Slow:
                    return Average;
                case Fast:
                    return Instant;
                case Instant:
                    return Never;
                default:
                    return Fast;
            }
        }
    }

    @SuppressWarnings({"unused", "EnhancedSwitchMigration"})
    public enum DebugLogMode {
        None("config." + MOD_ID + ".Debug_log_mode.none"),
        Log("config." + MOD_ID + ".Debug_log_mode.log"),
        Chat("config." + MOD_ID + ".Debug_log_mode.chat");

        private final String key;

        DebugLogMode(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return ETFVersionDifferenceHandler.getTextFromTranslation(key).getString();
        }

        public DebugLogMode next() {
            //not enhanced for 1.16 version compat
            switch (this) {
                case None:
                    return Log;
                case Log:
                    return Chat;
                default:
                    return None;
            }
        }
    }

    @SuppressWarnings({"unused", "EnhancedSwitchMigration"})
    public enum IllegalPathMode {
        None("options.off"),
        Entity("config." + MOD_ID + ".illegal_path_mode.entity"),
        All("config." + MOD_ID + ".illegal_path_mode.all");

        private final String key;

        IllegalPathMode(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return ETFVersionDifferenceHandler.getTextFromTranslation(key).getString();
        }

        public IllegalPathMode next() {
            //not enhanced for 1.16 version compat
            switch (this) {
                case None:
                    return Entity;
                case Entity:
                    return All;
                default:
                    return None;
            }
        }
    }
}
