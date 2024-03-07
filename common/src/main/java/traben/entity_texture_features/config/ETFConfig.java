package traben.entity_texture_features.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import traben.entity_features.config.EFConfig;
import traben.entity_features.config.EFConfigHandler;
import traben.entity_features.config.gui.builders.*;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFManager;

import java.util.HashSet;
import java.util.Set;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@SuppressWarnings("CanBeFinal")
public class ETFConfig extends EFConfig {



    @NotNull
    public static ETFConfig getConfig() {
        if(INSTANCE == null){
            INSTANCE = new EFConfigHandler<>(ETFConfig::new,MOD_ID);
        }
        return INSTANCE.getConfig();
    }

    public static void setConfig(ETFConfig newConfigInstance) {
        INSTANCE.setConfig(newConfigInstance);
    }

    public static void saveConfig() {
        getConfig();
        INSTANCE.saveToFile();
    }

    public static void loadConfig() {
        INSTANCE.loadFromFile();
    }

    public static ETFConfig copyConfig() {
        getConfig();
        return INSTANCE.copyOfConfig();
    }




    //config object
    private static EFConfigHandler<ETFConfig> INSTANCE = null;

    public IllegalPathMode illegalPathSupportMode = IllegalPathMode.None;

    public boolean enableCustomTextures = true;
    public boolean enableCustomBlockEntities = true;
    public UpdateFrequency textureUpdateFrequency_V2 = UpdateFrequency.Fast;
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
    public boolean enableEnemyTeamPlayersSkinFeatures = true;
    public boolean enableBlinking = true;
    public int blinkFrequency = 150;
    public int blinkLength = 1;

    public double advanced_IncreaseCacheSizeModifier = 1.0;

    public DebugLogMode debugLoggingMode = DebugLogMode.None;
    public boolean logTextureDataInitialization = false;




    public boolean hideConfigButton = false;

    public boolean disableVanillaDirectoryVariantTextures = false;

    public boolean use3DSkinLayerPatch = true;

    public boolean enableFullBodyWardenTextures = true;

    @Override
    public EFOptionCategory getGUIOptions() {
        return new EFOptionCategory.Empty().add(
                new EFOptionCategory("textures","tool").add(
                        new EFOptionCategory("random","tool").add(
                                new EFOptionBoolean("enableCustomTextures","tool",
                                        () -> enableCustomTextures, aBoolean -> enableCustomTextures = aBoolean,true),
                                new EFOptionEnum<>("textureUpdateFrequency_V2","tool",
                                        () -> textureUpdateFrequency_V2, updateFrequency -> textureUpdateFrequency_V2 = updateFrequency,
                                        UpdateFrequency.Fast),
                                new EFOptionBoolean("blockentities","tool",
                                        () -> enableCustomBlockEntities, aBoolean -> enableCustomBlockEntities = aBoolean,true),
                                new EFOptionCategory("restrictions","tool").add(
                                        new EFOptionBoolean("restrictBiome","tool",
                                                () -> restrictBiome, aBoolean -> restrictBiome = aBoolean,true),
                                        new EFOptionBoolean("restrictHeight","tool",
                                                () -> restrictHeight, aBoolean -> restrictHeight = aBoolean,true),
                                        new EFOptionBoolean("restrictBlock","tool",
                                                () -> restrictBlock, aBoolean -> restrictBlock = aBoolean,true),
                                        new EFOptionBoolean("restrictWeather","tool",
                                                () -> restrictWeather, aBoolean -> restrictWeather = aBoolean,true),
                                        new EFOptionBoolean("restrictDayTime","tool",
                                                () -> restrictDayTime, aBoolean -> restrictDayTime = aBoolean,true),
                                        new EFOptionBoolean("restrictMoonPhase","tool",
                                                () -> restrictMoonPhase, aBoolean -> restrictMoonPhase = aBoolean,true)
                                ),
                                new EFOptionBoolean("defaultdirectory","tool",
                                        () -> disableVanillaDirectoryVariantTextures, aBoolean -> disableVanillaDirectoryVariantTextures = aBoolean,true)
                        ),new EFOptionCategory("Emissive","tool").add(
                                new EFOptionBoolean("enableEmissiveTextures","tool",
                                        () -> enableEmissiveTextures, aBoolean -> enableEmissiveTextures = aBoolean,true),
                                new EFOptionBoolean("enableEmissiveBlockEntities","tool",
                                        () -> enableEmissiveBlockEntities, aBoolean -> enableEmissiveBlockEntities = aBoolean,true),
                                new EFOptionEnum<>("emissiveRenderMode","tool",
                                        () -> emissiveRenderMode, renderMode -> emissiveRenderMode = renderMode,
                                        ETFManager.EmissiveRenderModes.DULL),
                                new EFOptionBoolean("alwaysCheckVanillaEmissiveSuffix","tool",
                                        () -> alwaysCheckVanillaEmissiveSuffix, aBoolean -> alwaysCheckVanillaEmissiveSuffix = aBoolean,true),
                                new EFOptionBoolean("enableArmorAndTrims","tool",
                                        () -> enableArmorAndTrims, aBoolean -> enableArmorAndTrims = aBoolean,true)
                        ), new EFOptionCategory("Skin Features","tool").add(
                                new EFOptionBoolean("skinFeaturesEnabled","tool",
                                        () -> skinFeaturesEnabled, aBoolean -> skinFeaturesEnabled = aBoolean,true),
                                new EFOptionBoolean("skinFeaturesEnableTransparency","tool",
                                        () -> skinFeaturesEnableTransparency, aBoolean -> skinFeaturesEnableTransparency = aBoolean,true),
                                new EFOptionBoolean("skinFeaturesEnableFullTransparency","tool",
                                        () -> skinFeaturesEnableFullTransparency, aBoolean -> skinFeaturesEnableFullTransparency = aBoolean,true),
                                new EFOptionBoolean("tryETFTransparencyForAllSkins","tool",
                                        () -> tryETFTransparencyForAllSkins, aBoolean -> tryETFTransparencyForAllSkins = aBoolean,true),
                                new EFOptionBoolean("enableEnemyTeamPlayersSkinFeatures","tool",
                                        () -> enableEnemyTeamPlayersSkinFeatures, aBoolean -> enableEnemyTeamPlayersSkinFeatures = aBoolean,true),
                                new EFOptionBoolean("3dSkinLayerPatch","tool",
                                        () -> use3DSkinLayerPatch, aBoolean -> use3DSkinLayerPatch = aBoolean,true),
                                new EFOptionCustomScreenOpener("skin tool","tool",
                                        () -> new ETFConfigScreenSkinTool(MinecraftClient.getInstance().currentScreen),false)
                        ), new EFOptionCategory("Blinking","tool").add(
                                new EFOptionBoolean("enableBlinking","tool",
                                        () -> enableBlinking, aBoolean -> enableBlinking = aBoolean,true),
                                new EFOptionInt("blinkFrequency","tool",
                                        () -> blinkFrequency , aInt -> blinkFrequency = aInt ,150,1,1024,false,false),
                                new EFOptionInt("blinkLength","tool",
                                        () -> blinkLength , aInt -> blinkLength = aInt ,1,1,20,false,false)

                        ), new EFOptionCategory("Debug","tool").add(
                                new EFOptionEnum<>("debugLoggingMode","tool",
                                        () -> debugLoggingMode, debugLogMode -> debugLoggingMode = debugLogMode, DebugLogMode.None),
                                new EFOptionBoolean("logTextureDataInitialization","tool",
                                        () -> logTextureDataInitialization, aBoolean -> logTextureDataInitialization = aBoolean,true),
                                new EFOptionCustomButton("config.entity_texture_features.debug_screen.mass_log","config.entity_texture_features.debug_screen.mass_log.tooltip",
                                        (button) -> {ETFManager.getInstance().doTheBigBoyPrintoutKronk();
                                                    button.setMessage(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.debug_screen.mass_log.done"));
                                                    button.active = false;})
                        )
                ), new EFOptionCategory("General","tool").add(
                        new EFOptionEnum<>("illegalPathSupportMode","tool",
                                () -> illegalPathSupportMode, illegalPathMode -> illegalPathSupportMode = illegalPathMode, IllegalPathMode.None),
                        new EFOptionBoolean("enableFullBodyWardenTextures","tool",
                                () -> enableFullBodyWardenTextures, aBoolean -> enableFullBodyWardenTextures = aBoolean,true),
                        new EFOptionBoolean("hideConfigButton","tool",
                                () -> hideConfigButton, aBoolean -> hideConfigButton = aBoolean,true)
                )
        );
    }

    @Override
    public Identifier getModIcon() {
        return new Identifier(MOD_ID, "textures/gui/icon.png");
    }

    //string name stuff more in-depth than other enum for backwards compatibility






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
