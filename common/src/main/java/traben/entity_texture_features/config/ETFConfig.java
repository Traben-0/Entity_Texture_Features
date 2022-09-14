package traben.entity_texture_features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreenWarnings;

import java.util.HashSet;
import java.util.Set;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@SuppressWarnings("CanBeFinal")
public class ETFConfig {


    public boolean allowIllegalTexturePaths = false;
    public boolean enableCustomTextures = true;
    public boolean enableCustomBlockEntities = true;
    public UpdateFrequency textureUpdateFrequency_V2 = UpdateFrequency.Fast;
    public boolean restrictUpdateProperties = true;
    public boolean restrictBiome = true;
    public boolean restrictHeight = true;
    public boolean restrictBlock = true;
    public boolean restrictWeather = true;
    public boolean restrictDayTime = true;
    public boolean restrictMoonPhase = true;
    public boolean enableEmissiveTextures = true;
    public boolean enableEmissiveBlockEntities = true;

    public boolean fullBrightEmissives = false;

    public boolean specialEmissiveShield = true;
    public boolean alwaysCheckVanillaEmissiveSuffix = true;

    public boolean skinFeaturesEnabled = true;
    public boolean skinFeaturesEnableTransparency = true;
    public boolean skinFeaturesEnableFullTransparency = false;
    public boolean skinFeaturesPrintETFReadySkin = false;
    public boolean enableEnemyTeamPlayersSkinFeatures = true;
    public boolean enableBlinking = true;
    public int blinkFrequency = 150;
    public int blinkLength = 1;
    public boolean enableTridents = true;
    public boolean enableElytra = true;
    public boolean elytraThicknessFix = false;

    public double advanced_IncreaseCacheSizeModifier = 1.0;

    public DebugLogMode debugLoggingMode = DebugLogMode.None;


    public boolean removePixelsUnderEmissiveElytra = true;
    public boolean removePixelsUnderEmissiveArmour = true;
    public boolean removePixelsUnderEmissivePlayers = true;
    public boolean removePixelsUnderEmissiveMobs = true;
    public boolean removePixelsUnderEmissiveBlockEntity = true;


    public Set<ETFConfigScreenWarnings.ConfigWarning> ignoredConfigs = new HashSet<>();

    public boolean hideConfigButton = false;

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
            //noinspection EnhancedSwitchMigration
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

}
