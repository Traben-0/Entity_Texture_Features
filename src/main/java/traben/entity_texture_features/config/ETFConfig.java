package traben.entity_texture_features.config;

public class ETFConfig {

    public boolean alwaysCheckVanillaEmissiveSuffix = true;
    public boolean doShadersEmissiveFix = false;
    public enchantedPotionEffectsEnum enchantedPotionEffects = enchantedPotionEffectsEnum.NONE;
    public enum enchantedPotionEffectsEnum {
        NONE,
        ENCHANTED,
        GLOWING,
        CREEPER_CHARGE
    }

}
