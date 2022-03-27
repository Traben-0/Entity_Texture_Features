package traben.entity_texture_features.config;

public class ETFConfig {
    public boolean allowIllegalTexturePaths = true;
    public boolean enableCustomTextures = true;
    public int textureUpdateFrequency = 3;
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
    public enchantedPotionEffectsEnum enchantedPotionEffects = enchantedPotionEffectsEnum.NONE;
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
        NONE,
        ENCHANTED,
        GLOWING,
        CREEPER_CHARGE
    }

}
