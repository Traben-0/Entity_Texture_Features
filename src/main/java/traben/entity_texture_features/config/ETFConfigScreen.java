package traben.entity_texture_features.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETF_METHODS;

import java.awt.*;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;
import static traben.entity_texture_features.client.ETF_CLIENT.puzzleDetected;

public class ETFConfigScreen implements ETF_METHODS {
    public Screen getConfigScreen(Screen parent, boolean isTransparent) {
        // Return the screen here with the one you created from Cloth Config Builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("Entity Texture Features by Traben"));
        //Screen ModConfigScreen = builder.build();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory optifineOptions = builder.getOrCreateCategory(Text.of("Optifine Options"));
        optifineOptions.setBackground(new Identifier("textures/block/light_gray_wool.png"));
        if (puzzleDetected) {
            optifineOptions.addEntry(entryBuilder.startTextDescription(Text.of("@Motschen's Mod 'Puzzle' was detected:\n please ensure you disable emissive entities in that mod's settings!"))
                    .setColor(new Color(240, 195, 15).getRGB())
                    .build()); // Builds the option entry for cloth config
        }

        SubCategoryBuilder randoms = entryBuilder.startSubCategory(Text.of("Random / Custom Mobs settings"));
        randoms.add(0, entryBuilder.startBooleanToggle(Text.of("Enable Optifine Random mobs"), ETFConfigData.enableCustomTextures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Enables Randomized entity textures
                        works with resource packs
                        using the optifine format""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableCustomTextures = newValue) // Recommended: Called when user save the config
                .build());
        randoms.add(1, entryBuilder.startIntSlider(Text.of("Texture update frequency"), ETFConfigData.textureUpdateFrequency, 1, 64)
                .setDefaultValue(3) // Recommended: Used when user click "Reset"
                .setMin(1)
                .setMax(64)
                .setTooltip(new TranslatableText("""
                        Sets how often mobs textures will update
                        unprompted for changes like health & age
                        Lower values MAY cause lag
                        1 should be fairly instant
                        64 should be a few seconds delay
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.textureUpdateFrequency = newValue) // Recommended: Called when user save the config
                .build());
        optifineOptions.addEntry(randoms.build());
        SubCategoryBuilder emissives = entryBuilder.startSubCategory(Text.of("Emissive Texture settings"));
        emissives.add(0, entryBuilder.startBooleanToggle(Text.of("Enable Optifine Emissive entity textures"), ETFConfigData.enableEmissiveTextures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Enables Emissive entity textures
                        works with resource packs
                        using the optifine format""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableEmissiveTextures = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        emissives.add(1, entryBuilder.startBooleanToggle(Text.of("Always check the 'default' emissive?"), ETFConfigData.alwaysCheckVanillaEmissiveSuffix)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Most resource packs use the emissive suffix _e
                        however this can be overridden by packs if they
                        want to use a different suffix.
                        If this is true the mod will always check for
                        '_e' suffixes even when set differently by a resource pack""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.alwaysCheckVanillaEmissiveSuffix = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        emissives.add(2, entryBuilder.startBooleanToggle(Text.of("Emissive texture Z-Fighting / Shader patch"), ETFConfigData.doShadersEmissiveFix)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        If true this will make emissive textures float
                        slightly above the model.
                        This can fix Z-Fighting present in some shaders.
                        This will not always look right and can desync
                        with the models animation.""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.doShadersEmissiveFix = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        emissives.add(3, entryBuilder.startBooleanToggle(Text.of("Enable Emissive Elytras"), ETFConfigData.enableElytra)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Allows Elytra to use emissive textures
                        Elytra's only have emissive support as the CIT mod
                        already handles customizing these and is willing
                        to become compatible with this Mod's emissive format.
                        May not be fully compatible yet hence the option.""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableElytra = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        optifineOptions.addEntry(emissives.build());
        SubCategoryBuilder blinking = entryBuilder.startSubCategory(Text.of("Blinking Mob settings"));
        blinking.add(0, entryBuilder.startBooleanToggle(Text.of("Enable blinking mobs"), ETFConfigData.enableBlinking)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Sets whether or not mobs will try to blink
                        The Mob must have a texture with it's eyes
                        closed named "textureName_blink.png".
                        And an optional texture with it's eyes half
                        closed named "textureName_blink2.png".
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableBlinking = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        blinking.add(1, entryBuilder.startIntSlider(Text.of("Blinking frequency"), ETFConfigData.blinkFrequency, 32, 1024)
                .setDefaultValue(200) // Recommended: Used when user click "Reset"
                .setMin(32)
                .setMax(1024)
                .setTooltip(new TranslatableText("""
                        Sets whether or not mobs will try to blink
                        The Mob must have a texture with it's eyes
                        closed named "textureName_blink.png".
                        And an optional texture with it's eyes half
                        closed named "textureName_blink2.png".
                        THIS REQUIRES A RESTART OR RELOAD OF TEXTURES
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.blinkFrequency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        blinking.add(1, entryBuilder.startIntSlider(Text.of("Blinking frequency"), ETFConfigData.blinkFrequency, 41, 200)
                .setDefaultValue(200) // Recommended: Used when user click "Reset"
                .setMin(41)
                .setMax(1024)
                .setTooltip(new TranslatableText("""
                        sets how often textures will blink
                        This can be set / overridden per mob
                        by the resource-pack
                        See the mod download page for details
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.blinkFrequency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        blinking.add(1, entryBuilder.startIntSlider(Text.of("Blinking Length"), ETFConfigData.blinkLength, 0, 20)
                .setDefaultValue(1) // Recommended: Used when user click "Reset"
                .setMin(0)
                .setMax(20)
                .setTooltip(new TranslatableText("""
                        sets how long textures will blink for
                        0 = 1 tick, 20 = 20 ticks / 1 second
                        This can be set / overridden per mob
                        by the resource-pack
                        See the mod download page for details
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.blinkLength = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        optifineOptions.addEntry(blinking.build());
        SubCategoryBuilder players = entryBuilder.startSubCategory(Text.of("Player Skin settings"));
        players.add(0, entryBuilder.startBooleanToggle(Text.of("Player Skin Features"), ETFConfigData.skinFeaturesEnabled)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Allows player skins to use the features added by this mod.
                        Features are set in the players skin file.
                         - Instructions can be found on the mod page
                         - This can be disabled for enemy team players for PVP
                        Features include:
                         - Emissive pixels
                         - Enchanted pixels
                         - Blinking & (closed eyes when sleeping)
                         - maybe more...
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnabled = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        players.add(1, entryBuilder.startBooleanToggle(Text.of("Enable Player Skin Feature for Enemy Teams"), ETFConfigData.enableEnemyTeamPlayersSkinFeatures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Allows player skins to be enabled/disabled
                        for players on opposing teams in PVP games
                        otherwise they may be harder to see
                        and may affect balance
                        ///THIS SETTING REQUIRES A RESTART\\\\\\
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableEnemyTeamPlayersSkinFeatures = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        players.add(2, entryBuilder.startBooleanToggle(Text.of("Player Skin Feature: Transparency"), ETFConfigData.skinFeaturesEnableTransparency)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Allows player skins to be transparent on the base texture
                        WARNING: the texture can only be an average of
                         60% transparent to prevent abuse
                        - uses transparency in the skin texture itself
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnableTransparency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        players.add(3, entryBuilder.startBooleanToggle(Text.of("Player Skin Feature: FULL Transparency"), ETFConfigData.skinFeaturesEnableFullTransparency)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Allows player skins to be FULLY transparent
                        WARNING: this overrides the 60% transparency check
                         for player skins so they can be completely invisible
                         this option only changes what you can see and will
                         not make you invisible to others, it is meant for fun.
                        //REQUIRES RESTART\\\\
                        """)) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnableFullTransparency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        optifineOptions.addEntry(players.build());

        optifineOptions.addEntry(entryBuilder.startEnumSelector(Text.of("Custom potion effects"), ETFConfig.enchantedPotionEffectsEnum.class, ETFConfigData.enchantedPotionEffects)
                .setDefaultValue(ETFConfig.enchantedPotionEffectsEnum.NONE)
                .setTooltip(new TranslatableText("""
                        currently only works when the mob loads""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enchantedPotionEffects = newValue)
                .build());
        optifineOptions.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable Custom Tridents"), ETFConfigData.enableTridents)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Allows tridents to use custom & emissive textures
                        This toggle is here to support possible future CIT mod updates
                        The trident item name will choose the texture file to use
                        The texture 'trident_bobbystrident.png; will be chosen by the names:
                        'Bobby's Trident', ' BoBb%Ys   Trid##en t', & 'bobbystrident'
                        The texture 'trident_bobbystrident_e.png; will be emissive.""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableTridents = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        optifineOptions.addEntry(entryBuilder.startBooleanToggle(Text.of("Elytra size fix"), ETFConfigData.elytraThicknessFix)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        The thickness of Elytra is a bit larger than
                        the 2 pixels it is meant to be.
                        This option will 'fix' the scaling of the Elytra to
                        be more consistent.
                        REQUIRES a restart or reload (F3 + T)""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.elytraThicknessFix = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        builder.setSavingRunnable(() -> {
            // Serialise the config into the config file. This will be called last after all variables are updated.
            saveConfig();
            resetVisuals();

        });
        //MinecraftClient.getInstance().openScreen(screen);
        return builder.setTransparentBackground(isTransparent).build();
    }
}