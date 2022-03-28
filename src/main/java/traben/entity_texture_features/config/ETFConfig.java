package traben.entity_texture_features.config;

public class ETFConfig {
    public boolean allowIllegalTexturePaths = true;
    public boolean enableCustomTextures = true;
    public updateFrequency textureUpdateFrequency_V2 = updateFrequency.Fast;
    public boolean restrictUpdateProperties = true;
    public boolean restrictBiome = true;
    public boolean restrictHeight = true;
    public boolean restrictBlock = true;
    public boolean restrictWeather = true;
    public boolean restrictDayTime = true;
    public boolean restrictMoonPhase = true;
    public boolean enableEmissiveTextures = true;
    public boolean alwaysCheckVanillaEmissiveSuffix = true;
    public boolean doShadersEmissiveFix = false;
    public enchantedPotionEffectsEnum enchantedPotionEffects_V2 = enchantedPotionEffectsEnum.None;
    public boolean skinFeaturesEnabled = true;
    public boolean skinFeaturesEnableTransparency = true;
    public boolean skinFeaturesEnableFullTransparency = false;
    public boolean enableEnemyTeamPlayersSkinFeatures = true;
    public boolean enableBlinking = true;
    public int blinkFrequency = 150;
    public int blinkLength = 1;
    public boolean enableTridents = true;
    public boolean enableElytra = true;
    public boolean elytraThicknessFix = true;

    public enum enchantedPotionEffectsEnum {
        None,
        Enchanted,
        Glowing,
        Creeper_Charge
    }

    public enum updateFrequency {
        Never,
        Slow,
        Fast,
        Instant
    }

}
