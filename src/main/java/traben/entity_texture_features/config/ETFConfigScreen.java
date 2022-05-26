package traben.entity_texture_features.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETFClient;
import traben.entity_texture_features.client.utils.ETFUtils;

import java.awt.*;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;

// config translation rework done by @Maximum#8760
public class ETFConfigScreen {

    public Screen getConfigScreen(Screen parent, boolean isTransparent) {
        // Return the screen here with the one you created from Cloth Config Builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config." + ETFClient.MOD_ID + ".title"))
                .setSavingRunnable(() -> {
                    saveConfig();
                    resetVisuals();
                });

        ConfigCategory category = builder.getOrCreateCategory(Text.of(""));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        category.setBackground(new Identifier("textures/block/deepslate_tiles.png"));

        boolean shownWarning = false;
        int warningCount = 0;
        //this warning disables skin features with figura present
        if (FabricLoader.getInstance().isModLoaded("figura")) {
            shownWarning = true;
            warningCount++;
            if (!ETFConfigData.ignoreConfigWarnings) {

                category.addEntry(entryBuilder.startTextDescription(new TranslatableText("config." + ETFClient.MOD_ID + ".figura_warn.text"))
                        .setColor(new Color(255, 102, 102).getRGB())
                        .build()); // Builds the option entry for cloth config
            }
        }
        if (FabricLoader.getInstance().isModLoaded("skinlayers") && ETFConfigData.skinFeaturesEnabled) {
            shownWarning = true;
            warningCount++;
            if (!ETFConfigData.ignoreConfigWarnings) {
                ETFUtils.logWarn(new TranslatableText("config." + ETFClient.MOD_ID + ".skinlayers_warn.text").getString(), false);
                category.addEntry(entryBuilder.startTextDescription(new TranslatableText("config." + ETFClient.MOD_ID + ".skinlayers_warn.text"))
                        .setColor(new Color(220, 175, 15).getRGB())
                        .build()); // Builds the option entry for cloth config
            }
        }
        if (FabricLoader.getInstance().isModLoaded("enhancedblockentities")) {
            shownWarning = true;
            warningCount++;
            if (!ETFConfigData.ignoreConfigWarnings) {
                ETFUtils.logWarn(new TranslatableText("config." + ETFClient.MOD_ID + ".ebe_warn.text").getString(), false);
                category.addEntry(entryBuilder.startTextDescription(new TranslatableText("config." + ETFClient.MOD_ID + ".ebe_warn.text"))
                        .setColor(new Color(240, 175, 15).getRGB())
                        .build()); // Builds the option entry for cloth config
            }
        }
        if (shownWarning && ETFConfigData.ignoreConfigWarnings) {
            ETFUtils.logMessage(warningCount + " warnings have been ignored.", false);
        }

        //allow users to bypass warning if they want to
        //(this only appears if enabled or if a warning that disables something is present)
        if (shownWarning || ETFConfigData.ignoreConfigWarnings) {
            category.addEntry(entryBuilder.startBooleanToggle(Text.of(new TranslatableText("config." + ETFClient.MOD_ID + ".ignore_warnings.title").getString() + " -> [" + warningCount + "]"), ETFConfigData.ignoreConfigWarnings)
                    .setDefaultValue(false) // Recommended: Used when user click "Reset"
                    .setTooltip(Text.of(new TranslatableText("config." + ETFClient.MOD_ID + ".ignore_warnings.tooltip").getString() + warningCount + ".")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> ETFConfigData.ignoreConfigWarnings = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
        }

        // random settings
        SubCategoryBuilder randomSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".random_settings.title"));
        randomSettings.add(0, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_custom_textures.title"), ETFConfigData.enableCustomTextures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_custom_textures.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableCustomTextures = newValue) // Recommended: Called when user save the config
                .build());
        randomSettings.add(1, entryBuilder.startEnumSelector(new TranslatableText("config." + ETFClient.MOD_ID + ".texture_update_frequency.title"), ETFConfig.UpdateFrequency.class, ETFConfigData.textureUpdateFrequency_V2)
                .setDefaultValue(ETFConfig.UpdateFrequency.Fast)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".texture_update_frequency.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.textureUpdateFrequency_V2 = newValue)
                .build());
        randomSettings.add(2, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_update_properties.title"), ETFConfigData.restrictUpdateProperties)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_update_properties.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.restrictUpdateProperties = newValue) // Recommended: Called when user save the config
                .build());

        randomSettings.add(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_tridents.title"), ETFConfigData.enableTridents)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_tridents.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableTridents = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        // restriction settings
        SubCategoryBuilder restrictionSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".restriction_settings.title"));
        restrictionSettings.add(0, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_biome.title"), ETFConfigData.restrictBiome).setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_biome.tooltip")).setSaveConsumer(newValue -> ETFConfigData.restrictBiome = newValue).build());
        restrictionSettings.add(1, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_height.title"), ETFConfigData.restrictHeight).setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_height.tooltip")).setSaveConsumer(newValue -> ETFConfigData.restrictHeight = newValue).build());
        restrictionSettings.add(2, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_block.title"), ETFConfigData.restrictBlock).setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_block.tooltip")).setSaveConsumer(newValue -> ETFConfigData.restrictBlock = newValue).build());
        restrictionSettings.add(3, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_weather.title"), ETFConfigData.restrictWeather).setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_weather.tooltip")).setSaveConsumer(newValue -> ETFConfigData.restrictWeather = newValue).build());
        restrictionSettings.add(4, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_day_time.title"), ETFConfigData.restrictDayTime).setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_day_time.tooltip")).setSaveConsumer(newValue -> ETFConfigData.restrictDayTime = newValue).build());
        restrictionSettings.add(5, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_moon_phase.title"), ETFConfigData.restrictMoonPhase).setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_moon_phase.tooltip")).setSaveConsumer(newValue -> ETFConfigData.restrictMoonPhase = newValue).build());
        randomSettings.add(restrictionSettings.build());
        category.addEntry(randomSettings.build());

        // emissive settings
        SubCategoryBuilder emissiveSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".emissive_settings.title"));
        emissiveSettings.add(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_emissive_textures.title"), ETFConfigData.enableEmissiveTextures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_emissive_textures.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableEmissiveTextures = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        emissiveSettings.add(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".full_bright_emissives.title"), ETFConfigData.fullBrightEmissives)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".full_bright_emissives.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.fullBrightEmissives = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        emissiveSettings.add(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".always_check_vanilla_emissive_suffix.title"), ETFConfigData.alwaysCheckVanillaEmissiveSuffix)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".always_check_vanilla_emissive_suffix.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.alwaysCheckVanillaEmissiveSuffix = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config/ Builds the option entry for cloth config

        // special emissive settings
        SubCategoryBuilder specialEmissiveSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".special_emissive_settings.title")).setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".special_emissive_settings.tooltip"));
        specialEmissiveSettings.add(0, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_elytra.title"), ETFConfigData.enableElytra).setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_elytra.tooltip")).setSaveConsumer(newValue -> ETFConfigData.enableElytra = newValue).build());
        specialEmissiveSettings.add(1, entryBuilder.startBooleanToggle(Text.of("Emissive Shields"), ETFConfigData.specialEmissiveShield).setDefaultValue(true)
                .setTooltip(new TranslatableText("enables emissive shield textures")).setSaveConsumer(newValue -> ETFConfigData.specialEmissiveShield = newValue).build());
        emissiveSettings.add(specialEmissiveSettings.build());

        category.addEntry(emissiveSettings.build());

        // blinking mob settings
        SubCategoryBuilder blinkingMobSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".blinking_mob_settings_sub.title"));
        blinkingMobSettings.add(0, entryBuilder.startBooleanToggle(Text.of("config." + ETFClient.MOD_ID + ".blinking_mob_settings.title"), ETFConfigData.enableBlinking)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".blinking_mob_settings.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableBlinking = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        blinkingMobSettings.add(1, entryBuilder.startIntSlider(new TranslatableText("config." + ETFClient.MOD_ID + ".blink_frequency.title"), ETFConfigData.blinkFrequency, 1, 200)
                .setDefaultValue(200) // Recommended: Used when user click "Reset"
                .setMin(1)
                .setMax(1024)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".blink_frequency.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.blinkFrequency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        blinkingMobSettings.add(2, entryBuilder.startIntSlider(new TranslatableText("config." + ETFClient.MOD_ID + ".blink_length.title"), ETFConfigData.blinkLength, 0, 20)
                .setDefaultValue(1) // Recommended: Used when user click "Reset"
                .setMin(0)
                .setMax(20)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".blink_length.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.blinkLength = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        category.addEntry(blinkingMobSettings.build());

        // player skin settings
        SubCategoryBuilder playerSkinSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".player_skin_settings.title"));
        playerSkinSettings.add(0, entryBuilder.startBooleanToggle(Text.of("Player Skin Features"), ETFConfigData.skinFeaturesEnabled)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".player_skin_settings.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnabled = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        playerSkinSettings.add(1, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_enemy_team_players_skin_features.title"), ETFConfigData.enableEnemyTeamPlayersSkinFeatures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_enemy_team_players_skin_features.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableEnemyTeamPlayersSkinFeatures = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        SubCategoryBuilder playerSkinTransparentSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".player_skin_transparent_sub.title")).setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".player_skin_transparent_sub.tooltip"));
        playerSkinTransparentSettings.add(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_features_enable_transparency.title"), ETFConfigData.skinFeaturesEnableTransparency)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_features_enable_transparency.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnableTransparency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        playerSkinTransparentSettings.add(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_features_enable_full_transparency.title"), ETFConfigData.skinFeaturesEnableFullTransparency)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_features_enable_full_transparency.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnableFullTransparency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        playerSkinSettings.add(playerSkinTransparentSettings.build());

        playerSkinSettings.add(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_feature_print.title"), ETFConfigData.skinFeaturesPrintETFReadySkin)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_feature_print.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesPrintETFReadySkin = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        category.addEntry(playerSkinSettings.build());

        category.addEntry(entryBuilder.startEnumSelector(new TranslatableText("config." + ETFClient.MOD_ID + ".enchanted_potion_effects.title"), ETFConfig.EnchantedPotionEffectsEnum.class, ETFConfigData.enchantedPotionEffects)
                .setDefaultValue(ETFConfig.EnchantedPotionEffectsEnum.NONE)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enchanted_potion_effects.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enchantedPotionEffects = newValue)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".elytra_thickness_fix.title"), ETFConfigData.elytraThicknessFix)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".elytra_thickness_fix.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.elytraThicknessFix = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        category.addEntry(entryBuilder.startEnumSelector(new TranslatableText("config." + ETFClient.MOD_ID + ".debug_logging_mode.title"), ETFConfig.DebugLogMode.class, ETFConfigData.debugLoggingMode)
                .setDefaultValue(ETFConfig.DebugLogMode.None) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".debug_logging_mode.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.debugLoggingMode = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".allow_illegal_texture_paths.title"), ETFConfigData.allowIllegalTexturePaths)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".allow_illegal_texture_paths.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.allowIllegalTexturePaths = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config


        //MinecraftClient.getInstance().openScreen(screen);
        return builder.setTransparentBackground(isTransparent).build();
    }

    //this needs to be here due to puzzle mod compatibility, remove this when the full release happens
    public void saveConfig() {
        ETFUtils.saveConfig();
    }

    //same as above
    public void resetVisuals() {
        ETFUtils.resetAllETFEntityData();
    }
}