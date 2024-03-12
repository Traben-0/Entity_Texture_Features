package traben.entity_texture_features.config;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.tconfig.TConfig;
import traben.tconfig.gui.entries.*;

import static traben.entity_texture_features.ETF.MOD_ID;


@SuppressWarnings("CanBeFinal")
public final class ETFConfig extends TConfig {

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
    public boolean allowUnknownRestrictions = true;
    public boolean enableEmissiveTextures = true;

    public boolean enableEnchantedTextures = true;
    public boolean enableEmissiveBlockEntities = true;

    public EmissiveRenderModes emissiveRenderMode = EmissiveRenderModes.DULL;


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
    public TConfigEntryCategory getGUIOptions() {
        return new TConfigEntryCategory.Empty().add(
                new TConfigEntryCategory("config.entity_features.textures_main").add(
                        new TConfigEntryCategory("config.entity_texture_features.random_settings.title").add(
                                new TConfigEntryBoolean("config.entity_texture_features.enable_custom_textures.title", "config.entity_texture_features.enable_custom_textures.tooltip",
                                        () -> enableCustomTextures, aBoolean -> enableCustomTextures = aBoolean, true),
                                new TConfigEntryEnum<>("config.entity_texture_features.texture_update_frequency.title", "config.entity_texture_features.texture_update_frequency.tooltip",
                                        () -> textureUpdateFrequency_V2, updateFrequency -> textureUpdateFrequency_V2 = updateFrequency,
                                        UpdateFrequency.Fast),
                                new TConfigEntryBoolean("config.entity_texture_features.custom_block_entity.title", "config.entity_texture_features.custom_block_entity.tooltip",
                                        () -> enableCustomBlockEntities, aBoolean -> enableCustomBlockEntities = aBoolean, true),
                                new TConfigEntryCategory("config.entity_texture_features.restrict_update_properties", "config.entity_texture_features.restrict_update_properties.tooltip").add(
                                        new TConfigEntryBoolean("config.entity_texture_features.allow_unknown_restrict.title", "config.entity_texture_features.allow_unknown_restrict.tooltip",
                                                () -> allowUnknownRestrictions, aBoolean -> allowUnknownRestrictions = aBoolean, true),
                                        new TConfigEntryBoolean("config.entity_texture_features.restrict_biome.title", "config.entity_texture_features.restrict_biome.tooltip",
                                                () -> restrictBiome, aBoolean -> restrictBiome = aBoolean, true),
                                        new TConfigEntryBoolean("config.entity_texture_features.restrict_height.title", "config.entity_texture_features.restrict_height.tooltip",
                                                () -> restrictHeight, aBoolean -> restrictHeight = aBoolean, true),
                                        new TConfigEntryBoolean("config.entity_texture_features.restrict_block.title", "config.entity_texture_features.restrict_block.tooltip",
                                                () -> restrictBlock, aBoolean -> restrictBlock = aBoolean, true),
                                        new TConfigEntryBoolean("config.entity_texture_features.restrict_weather.title", "config.entity_texture_features.restrict_weather.tooltip",
                                                () -> restrictWeather, aBoolean -> restrictWeather = aBoolean, true),
                                        new TConfigEntryBoolean("config.entity_texture_features.restrict_day_time.title", "config.entity_texture_features.restrict_day_time.tooltip",
                                                () -> restrictDayTime, aBoolean -> restrictDayTime = aBoolean, true),
                                        new TConfigEntryBoolean("config.entity_texture_features.restrict_moon_phase.title", "config.entity_texture_features.restrict_moon_phase.tooltip",
                                                () -> restrictMoonPhase, aBoolean -> restrictMoonPhase = aBoolean, true)
                                ),
                                new TConfigEntryBoolean("config.entity_texture_features.disable_default_directory.title", "config.entity_texture_features.disable_default_directory.tooltip",
                                        () -> disableVanillaDirectoryVariantTextures, aBoolean -> disableVanillaDirectoryVariantTextures = aBoolean, false)
                        ), new TConfigEntryCategory("config.entity_texture_features.emissive_settings.title").add(
                                new TConfigEntryBoolean("config.entity_texture_features.enable_emissive_textures.title", "config.entity_texture_features.enable_emissive_textures.tooltip",
                                        () -> enableEmissiveTextures, aBoolean -> enableEmissiveTextures = aBoolean, true),
                                new TConfigEntryBoolean("config.entity_texture_features.emissive_block_entity.title", "config.entity_texture_features.emissive_block_entity.tooltip",
                                        () -> enableEmissiveBlockEntities, aBoolean -> enableEmissiveBlockEntities = aBoolean, true),
                                new TConfigEntryEnum<>("config.entity_texture_features.emissive_mode.title", "config.entity_texture_features.emissive_mode.tooltip",
                                        () -> emissiveRenderMode, renderMode -> emissiveRenderMode = renderMode, EmissiveRenderModes.DULL),
                                new TConfigEntryBoolean("config.entity_texture_features.always_check_vanilla_emissive_suffix.title", "config.entity_texture_features.always_check_vanilla_emissive_suffix.tooltip",
                                        () -> alwaysCheckVanillaEmissiveSuffix, aBoolean -> alwaysCheckVanillaEmissiveSuffix = aBoolean, true),
                                new TConfigEntryBoolean("config.entity_texture_features.armor_enable", "config.entity_texture_features.armor_enable.tooltip",
                                        () -> enableArmorAndTrims, aBoolean -> enableArmorAndTrims = aBoolean, true),
                                new TConfigEntryBoolean( "config.entity_texture_features.enchanted_enable", "config.entity_texture_features.enchanted_enable.tooltip",
                                        () -> enableEnchantedTextures, aBoolean -> enableEnchantedTextures = aBoolean, true)
                        ), new TConfigEntryCategory("config.entity_texture_features.player_skin_settings.title").add(
                                new TConfigEntryBoolean("config.entity_texture_features.player_skin_features.title", "config.entity_texture_features.player_skin_features.tooltip",
                                        () -> skinFeaturesEnabled, aBoolean -> skinFeaturesEnabled = aBoolean, true),
                                new TConfigEntryBoolean("config.entity_texture_features.skin_features_enable_transparency.title", "config.entity_texture_features.skin_features_enable_transparency.tooltip",
                                        () -> skinFeaturesEnableTransparency, aBoolean -> skinFeaturesEnableTransparency = aBoolean, true),
                                new TConfigEntryBoolean("config.entity_texture_features.skin_features_enable_full_transparency.title", "config.entity_texture_features.skin_features_enable_full_transparency.tooltip",
                                        () -> skinFeaturesEnableFullTransparency, aBoolean -> skinFeaturesEnableFullTransparency = aBoolean, false),
                                new TConfigEntryBoolean("config.entity_texture_features.skin_features_try_transparency_for_all.title", "config.entity_texture_features.skin_features_try_transparency_for_all.tooltip",
                                        () -> tryETFTransparencyForAllSkins, aBoolean -> tryETFTransparencyForAllSkins = aBoolean, false),
                                new TConfigEntryBoolean("config.entity_texture_features.enable_enemy_team_players_skin_features.title", "config.entity_texture_features.enable_enemy_team_players_skin_features.tooltip",
                                        () -> enableEnemyTeamPlayersSkinFeatures, aBoolean -> enableEnemyTeamPlayersSkinFeatures = aBoolean, true),
                                ETF.SKIN_LAYERS_DETECTED ?
                                        new TConfigEntryBoolean("config.entity_texture_features.skin_layers_patch.title", "config.entity_texture_features.skin_layers_patch.tooltip",
                                                () -> use3DSkinLayerPatch, aBoolean -> use3DSkinLayerPatch = aBoolean, true) : null,
                                getPlayerSkinEditorButton()
                        ), new TConfigEntryCategory("config.entity_texture_features.blinking_mob_settings_sub.title").add(
                                new TConfigEntryBoolean("config.entity_texture_features.blinking_mob_settings.title", "config.entity_texture_features.blinking_mob_settings.tooltip",
                                        () -> enableBlinking, aBoolean -> enableBlinking = aBoolean, true),
                                new TConfigEntryInt("config.entity_texture_features.blink_frequency.title", "config.entity_texture_features.blink_frequency.tooltip",
                                        () -> blinkFrequency, aInt -> blinkFrequency = aInt, 150, 1, 1024),
                                new TConfigEntryInt("config.entity_texture_features.blink_length.title", "config.entity_texture_features.blink_length.tooltip",
                                        () -> blinkLength, aInt -> blinkLength = aInt, 1, 1, 2)

                        ), new TConfigEntryCategory("config.entity_texture_features.debug_screen.title").add(
                                new TConfigEntryEnum<>("config.entity_texture_features.debug_logging_mode.title", "config.entity_texture_features.debug_logging_mode.tooltip",
                                        () -> debugLoggingMode, debugLogMode -> debugLoggingMode = debugLogMode, DebugLogMode.None),
                                new TConfigEntryBoolean("config.entity_texture_features.log_creation", "config.entity_texture_features.log_creation.tooltip",
                                        () -> logTextureDataInitialization, aBoolean -> logTextureDataInitialization = aBoolean, false),
                                new TConfigEntryCustomButton("config.entity_texture_features.debug_screen.mass_log", "config.entity_texture_features.debug_screen.mass_log.tooltip",
                                        (button) -> {
                                            ETFManager.getInstance().doTheBigBoyPrintoutKronk();
                                            button.setMessage(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.debug_screen.mass_log.done"));
                                            button.active = false;
                                        })
                        )
                ), new TConfigEntryCategory("config.entity_features.general_settings.title").add(
                        new TConfigEntryEnum<>("config.entity_texture_features.allow_illegal_texture_paths.title", "config.entity_texture_features.allow_illegal_texture_paths.tooltip",
                                () -> illegalPathSupportMode, illegalPathMode -> illegalPathSupportMode = illegalPathMode, IllegalPathMode.None),
                        new TConfigEntryBoolean("config.entity_texture_features.warden.title", "config.entity_texture_features.warden.tooltip",
                                () -> enableFullBodyWardenTextures, aBoolean -> enableFullBodyWardenTextures = aBoolean, true),
                        new TConfigEntryBoolean("config.entity_features.hide_button", "config.entity_features.hide_button.tooltip",
                                () -> hideConfigButton, aBoolean -> hideConfigButton = aBoolean, false)
                )
        );
    }

    private TConfigEntry getPlayerSkinEditorButton() {
        boolean condition1 = ETF.config().getConfig().skinFeaturesEnabled;
        boolean condition2 = !ETFVersionDifferenceHandler.isFabric() || ETFVersionDifferenceHandler.isThisModLoaded("fabric");
        boolean condition3 = MinecraftClient.getInstance().player != null;
        boolean condition4 = ETFPlayerTexture.clientPlayerOriginalSkinImageForTool != null;
        boolean canLaunchSkinTool = condition1 && condition2 && condition3 && condition4;

        StringBuilder reasonText = new StringBuilder();
        if (!canLaunchSkinTool) {
            //log reason
            reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_0").getString());
            if (!condition1) {
                reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_1").getString());
            }
            if (!condition2) {
                reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_2").getString());
            }
            if (!condition3) {
                reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_3").getString());
            }
            if (!condition4) {
                reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_4").getString());
            }
            //ETFUtils2.logWarn(reasonText.toString());
        }

        return canLaunchSkinTool ?
                new TConfigEntryCustomScreenOpener("config.entity_texture_features.player_skin_editor.button.enabled", reasonText.toString(),
                        () -> new ETFConfigScreenSkinTool(MinecraftClient.getInstance().currentScreen), false) :
                new TConfigEntryCustomScreenOpener("config.entity_texture_features.player_skin_editor.button.disabled", reasonText.toString(),
                        () -> new ETFConfigScreenSkinTool(MinecraftClient.getInstance().currentScreen), false).setEnabled(false);
    }

    @Override
    public Identifier getModIcon() {
        return new Identifier(MOD_ID, "textures/gui/icon.png");
    }

    @SuppressWarnings({"unused"})
    public enum UpdateFrequency {
        Never(-1, "config.entity_texture_features.update_frequency.never"),
        Slow(80, "config.entity_texture_features.update_frequency.slow"),
        Average(20, "config.entity_texture_features.update_frequency.average"),
        Fast(5, "config.entity_texture_features.update_frequency.fast"),
        Instant(1, "config.entity_texture_features.update_frequency.instant");

        final private int delay;
        final private String key;

        UpdateFrequency(int delay, @Translatable String key) {
            this.delay = delay;
            this.key = key;
        }

        public int getDelay() {
            return delay;
        }

        @Override
        public String toString() {
            return ETFVersionDifferenceHandler.getTextFromTranslation(key).getString();
        }


    }

    @SuppressWarnings({"unused"})
    public enum DebugLogMode {
        None("config.entity_texture_features.Debug_log_mode.none"),
        Log("config.entity_texture_features.Debug_log_mode.log"),
        Chat("config.entity_texture_features.Debug_log_mode.chat");

        private final String key;

        DebugLogMode(@Translatable String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return ETFVersionDifferenceHandler.getTextFromTranslation(key).getString();
        }

    }

    @SuppressWarnings({"unused"})
    public enum IllegalPathMode {
        None("options.off"),
        Entity("config.entity_texture_features.illegal_path_mode.entity"),
        All("config.entity_texture_features.illegal_path_mode.all");

        private final String key;

        IllegalPathMode(@Translatable String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return ETFVersionDifferenceHandler.getTextFromTranslation(key).getString();
        }

    }

    public enum EmissiveRenderModes {
        DULL,
        BRIGHT;


        @Override
        public String toString() {
            return switch (this) {
                case DULL -> ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config.entity_texture_features.emissive_mode.dull").getString();
                case BRIGHT -> ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config.entity_texture_features.emissive_mode.bright").getString();
            };
        }

    }
}
