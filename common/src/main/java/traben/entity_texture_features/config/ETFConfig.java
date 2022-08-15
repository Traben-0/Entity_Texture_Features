package traben.entity_texture_features.config;

import traben.entity_texture_features.ETFVersionDifferenceHandler;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

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

    public boolean ignoreConfigWarnings = false;

    public boolean enableEnemyTeamPlayersSkinFeatures = true;
    public boolean enableBlinking = true;
    public int blinkFrequency = 150;
    public int blinkLength = 1;
    public boolean enableTridents = true;
    public boolean enableElytra = true;
    public boolean elytraThicknessFix = false;

    public double advanced_IncreaseCacheSizeModifier = 1.0;

    public DebugLogMode debugLoggingMode = DebugLogMode.None;

    public boolean temporary_fixIrisPBR = false;

    //string name stuff more in-depth than other enum for backwards compatibility


    @SuppressWarnings("unused")
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

        private String getKey(){
            //non enhanced switch for back compatibility
            switch (delay){
                case -1: return "config."+MOD_ID+".update_frequency.never";
                case 80: return "config."+MOD_ID+".update_frequency.slow";
                case 20: return "config."+MOD_ID+".update_frequency.average";
                case 5: return "config."+MOD_ID+".update_frequency.fast";
                case 1: return "config."+MOD_ID+".update_frequency.instant";
                default: return "config."+MOD_ID+".error";
            }
        }
    }

    @SuppressWarnings("unused")
    public enum DebugLogMode {
        None("config."+MOD_ID+".Debug_log_mode.none"),
        Log("config."+MOD_ID+".Debug_log_mode.log"),
        Chat("config."+MOD_ID+".Debug_log_mode.chat");

        private final String key;

        DebugLogMode(String key){
            this.key = key;
        }
        @Override
        public String toString() {
            return ETFVersionDifferenceHandler.getTextFromTranslation(key).getString();
        }
    }

}
