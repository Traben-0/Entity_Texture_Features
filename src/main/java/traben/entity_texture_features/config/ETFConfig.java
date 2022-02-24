package traben.entity_texture_features.config;

public class ETFConfig {
    public boolean enableRandomTextures = true;
    public boolean enableEmissiveTextures = true;
    public boolean alwaysCheckVanillaEmissiveSuffix = true;
    public boolean doShadersEmissiveFix = false;
    public enchantedPotionEffectsEnum enchantedPotionEffects = enchantedPotionEffectsEnum.NONE;
    public boolean skinFeaturesEnabled = true;
    public boolean skinFeaturesEnableTransparency = true;
    public boolean skinFeaturesDisabledForEnemyTeamPlayersOnly = true;
    public boolean enableBlinking = true;
    public int blinkFrequency = 150;
    public boolean enableTridents = true;
    public boolean enableElytra = true;
    public enum enchantedPotionEffectsEnum {
        NONE,
        ENCHANTED,
        GLOWING,
        CREEPER_CHARGE
    }

}
