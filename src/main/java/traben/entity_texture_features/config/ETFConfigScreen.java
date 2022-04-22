package traben.entity_texture_features.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETFClient;
import traben.entity_texture_features.client.ETFUtils;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;

public class ETFConfigScreen {
    public Screen createConfigScreen(Screen parent, boolean isTransparent) {
        // Return the screen here with the one you created from Cloth Config Builder
        // ConfigScreen screen = ConfigScreen.create(new TranslatableText("config." + ETFClient.MOD_ID + ".title"), parent);

        // screen.add(new TranslatableText("config." + ETFClient.MOD_ID + ".allow_illegal_texture_paths"), ETFConfigData.allowIllegalTexturePaths, () -> ETFConfigData.allowIllegalTexturePaths, newValue -> {
        //     ETFConfigData.allowIllegalTexturePaths = (boolean)newValue;
        //     saveConfig();
        // });

        // screen.add(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_custom_textures"), ETFConfigData.enableCustomTextures, () -> ETFConfigData.enableCustomTextures, newValue -> {
        //     ETFConfigData.enableCustomTextures = (boolean)newValue;
        //     saveConfig();
        // });

        // screen.add(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_custom_textures"), ETFConfigData.textureUpdateFrequency_V2, () -> ETFConfigData.textureUpdateFrequency_V2, newValue -> {
        //     ETFConfigData.textureUpdateFrequency_V2 = (ETFConfig.updateFrequency)newValue;
        //     saveConfig();
        // });
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config." + ETFClient.MOD_ID + ".title"))
                .setSavingRunnable(() -> {
                    saveConfig();
                    resetVisuals();
                });

        ConfigCategory category  = builder.getOrCreateCategory(Text.of(""));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        category.setBackground(new Identifier("textures/block/light_gray_wool.png"));

        // TODO: may want to re use this
        // if (irisDetected) {
        //     optifineOptions.addEntry(entryBuilder.startTextDescription(Text.of("""
        //                     Iris Shaders Mod was detected:
        //                     - If your emissive textures are not glowing correctly with shaders make sure
        //                       Full Bright emissive rendering is enabled in Emissive Texture Settings"""))
        //             .setColor(new Color(240, 195, 15).getRGB())
        //             .build()); // Builds the option entry for cloth config
        // }

        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".allow_illegal_texture_paths.title"), ETFConfigData.allowIllegalTexturePaths)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".allow_illegal_texture_paths.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.allowIllegalTexturePaths = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        // random settings
        SubCategoryBuilder randomSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".random_settings.title"));
        randomSettings.add(0, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_custom_textures.title"), ETFConfigData.enableCustomTextures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_custom_textures.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableCustomTextures = newValue) // Recommended: Called when user save the config
                .build());
        randomSettings.add(1, entryBuilder.startEnumSelector(new TranslatableText("config." + ETFClient.MOD_ID + ".texture_update_frequency.title"), ETFConfig.updateFrequency.class, ETFConfigData.textureUpdateFrequency_V2)
                .setDefaultValue(ETFConfig.updateFrequency.Fast)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".texture_update_frequency.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.textureUpdateFrequency_V2 = newValue)
                .build());
        randomSettings.add(2, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_update_properties.title"), ETFConfigData.restrictUpdateProperties)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".restrict_update_properties.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.restrictUpdateProperties = newValue) // Recommended: Called when user save the config
                .build());

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
        emissiveSettings.add(0, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_emissive_textures.title"), ETFConfigData.enableEmissiveTextures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_emissive_textures.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableEmissiveTextures = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        // TODO: Re add
        // emissiveSettings.add(1, entryBuilder.startBooleanToggle(Text.of("Use Full Bright emissive rendering"), ETFConfigData.fullBrightEmissives)
        //         .setDefaultValue(true) // Recommended: Used when user click "Reset"
        //         .setTooltip(new TranslatableText("""
        //                 This sets whether emissives use 'Bright' or 'Dull' rendering.
        //                 This is hard to describe just try it ingame.
        //                 Bright rendering makes the emissive appear at max brightness if need be
        //                 and is compatible with shaders and usually supports bloom, etc.
        //                 Dull rendering respects the default renderers affects given to entities
        //                 the direction of each surface will have slight brightness variation
        //                 and the emissives will be a bit less bright overall, as well as unlikely to be shader compatible.
        //                 Block entities will always be Dull unless Iris is installed""")) // Optional: Shown when the user hover over this option
        //         .setSaveConsumer(newValue -> ETFConfigData.fullBrightEmissives = newValue) // Recommended: Called when user save the config
        //         .build()); // Builds the option entry for cloth config
        emissiveSettings.add(1, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".always_check_vanilla_emissive_suffix.title"), ETFConfigData.alwaysCheckVanillaEmissiveSuffix)
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
        emissiveSettings.add(1, specialEmissiveSettings.build());

        category.addEntry(emissiveSettings.build());

        // blinking mob settings
        SubCategoryBuilder blinkingMobSettings = entryBuilder.startSubCategory(new TranslatableText("config." + ETFClient.MOD_ID + ".blinking_mob_settings.title"));
        blinkingMobSettings.add(0, entryBuilder.startBooleanToggle(Text.of("Enable blinking mobs"), ETFConfigData.enableBlinking)
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
        playerSkinSettings.add(2, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_features_enable_transparency.title"), ETFConfigData.skinFeaturesEnableTransparency)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_features_enable_transparency.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnableTransparency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        playerSkinSettings.add(3, entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_features_enable_full_transparency.title"), ETFConfigData.skinFeaturesEnableFullTransparency)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".skin_features_enable_full_transparency.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnableFullTransparency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        category.addEntry(playerSkinSettings.build());

        category.addEntry(entryBuilder.startEnumSelector(new TranslatableText("config." + ETFClient.MOD_ID + ".enchanted_potion_effects.title"), ETFConfig.enchantedPotionEffectsEnum.class, ETFConfigData.enchantedPotionEffects)
                .setDefaultValue(ETFConfig.enchantedPotionEffectsEnum.NONE)
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enchanted_potion_effects.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enchantedPotionEffects = newValue)
                .build());
        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_tridents.title"), ETFConfigData.enableTridents)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".enable_tridents.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableTridents = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config." + ETFClient.MOD_ID + ".elytra_thickness_fix.title"), ETFConfigData.elytraThicknessFix)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("config." + ETFClient.MOD_ID + ".elytra_thickness_fix.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.elytraThicknessFix = newValue) // Recommended: Called when user save the config
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
        ETFUtils.resetVisuals();
    }
}