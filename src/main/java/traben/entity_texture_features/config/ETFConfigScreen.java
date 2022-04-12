package traben.entity_texture_features.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETFUtils;

import java.awt.*;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;
import static traben.entity_texture_features.client.ETF_CLIENT.irisDetected;

public class ETFConfigScreen {
    public Screen getConfigScreen(Screen parent, boolean isTransparent) {
        // Return the screen here with the one you created from Cloth Config Builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("Entity Texture Features by Traben"));
        //Screen ModConfigScreen = builder.build();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory optifineOptions = builder.getOrCreateCategory(Text.of("Optifine Options"));
        optifineOptions.setBackground(new Identifier("textures/block/light_gray_wool.png"));
        if (irisDetected) {
            optifineOptions.addEntry(entryBuilder.startTextDescription(Text.of("Iris Shaders Mod was detected:\n If your emissive textures are flickering with shaders try the\nZ-Fighting fix in Emissive Texture Settings!"))
                    .setColor(new Color(240, 195, 15).getRGB())
                    .build()); // Builds the option entry for cloth config
        }

        optifineOptions.addEntry(entryBuilder.startBooleanToggle(Text.of("Allow non [a-z0-9/._-] characters in texture paths"), ETFConfigData.allowIllegalTexturePaths)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        This setting allows you to overwrite the
                        Vanilla behaviour of prohibiting all
                        non [a-z0-9/._-] characters in texture paths.
                        this means textures with spaces or capitals
                        are allowed to be used.""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.allowIllegalTexturePaths = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        SubCategoryBuilder randoms = entryBuilder.startSubCategory(Text.of("Random / Custom Mobs settings"));
        randoms.add(0, entryBuilder.startBooleanToggle(Text.of("Enable Optifine Random mobs"), ETFConfigData.enableCustomTextures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Enables Randomized entity textures
                        works with resource packs
                        Allows for entity texture customization
                        using the optifine format""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableCustomTextures = newValue) // Recommended: Called when user save the config
                .build());
        randoms.add(1, entryBuilder.startEnumSelector(Text.of("Texture update frequency"), ETFConfig.updateFrequency.class, ETFConfigData.textureUpdateFrequency_V2)
                .setDefaultValue(ETFConfig.updateFrequency.Fast)
                .setTooltip(new TranslatableText("""
                        Sets how often a mobs textures will
                        update for changes like health & age
                        Never = never update
                        Slow = 3-5 seconds
                        Fast = less than a second
                        Instant = instant (possible lag with hundred of mobs)""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.textureUpdateFrequency_V2 = newValue)
                .build());
        randoms.add(2, entryBuilder.startBooleanToggle(Text.of("Restrict some property updates"), ETFConfigData.restrictUpdateProperties)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("""
                        If enabled this will prevent changes in:
                        Biome, Height, Block, Weather, DayTime or MoonPhase
                        from effecting mobs that have already had
                        a random texture applied.
                        E.G a zombie spawned in a desert will keep it's
                        desert skin even if it leaves the desert.
                        This will not effect other properties such as:
                        Health, Name, Team, etc.""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.restrictUpdateProperties = newValue) // Recommended: Called when user save the config
                .build());
        SubCategoryBuilder restrictions = entryBuilder.startSubCategory(Text.of("Restricted Properties"));
        restrictions.add(0, entryBuilder.startBooleanToggle(Text.of("Restrict Biome property updates"), ETFConfigData.restrictBiome).setDefaultValue(true)
                .setTooltip(new TranslatableText("Restrict Biome property updates\nMust enable 'Restrict some property updates' first")).setSaveConsumer(newValue -> ETFConfigData.restrictBiome = newValue).build());
        restrictions.add(1, entryBuilder.startBooleanToggle(Text.of("Restrict Height property updates"), ETFConfigData.restrictHeight).setDefaultValue(true)
                .setTooltip(new TranslatableText("Restrict Height property updates\nMust enable 'Restrict some property updates' first")).setSaveConsumer(newValue -> ETFConfigData.restrictHeight = newValue).build());
        restrictions.add(2, entryBuilder.startBooleanToggle(Text.of("Restrict Block property updates"), ETFConfigData.restrictBlock).setDefaultValue(true)
                .setTooltip(new TranslatableText("Restrict Block property updates\nMust enable 'Restrict some property updates' first")).setSaveConsumer(newValue -> ETFConfigData.restrictBlock = newValue).build());
        restrictions.add(3, entryBuilder.startBooleanToggle(Text.of("Restrict Weather property updates"), ETFConfigData.restrictWeather).setDefaultValue(true)
                .setTooltip(new TranslatableText("Restrict Weather property updates\nMust enable 'Restrict some property updates' first")).setSaveConsumer(newValue -> ETFConfigData.restrictWeather = newValue).build());
        restrictions.add(4, entryBuilder.startBooleanToggle(Text.of("Restrict Day Time property updates"), ETFConfigData.restrictDayTime).setDefaultValue(true)
                .setTooltip(new TranslatableText("Restrict Day Time property updates\nMust enable 'Restrict some property updates' first")).setSaveConsumer(newValue -> ETFConfigData.restrictDayTime = newValue).build());
        restrictions.add(5, entryBuilder.startBooleanToggle(Text.of("Restrict Moon Phase property updates"), ETFConfigData.restrictMoonPhase).setDefaultValue(true)
                .setTooltip(new TranslatableText("Restrict Moon Phase property updates\nMust enable 'Restrict some property updates' first")).setSaveConsumer(newValue -> ETFConfigData.restrictMoonPhase = newValue).build());
        randoms.add(restrictions.build());
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
                        with the models animation.
                        It is recommended to disable this if shaders are disabled""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.doShadersEmissiveFix = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        SubCategoryBuilder specialEmissives = entryBuilder.startSubCategory(Text.of("Special Case Emissives")).setTooltip(Text.of("""
                This option is provided as these cases
                may not fit into the typical users expectation
                of entity, these are mostly tile entities
                hence why they are not supported by the
                Continuity mod's emissive texture feature.
                CIT support is always wanted but can only
                be confirmed for the elytra so far"""));
        specialEmissives.add(0, entryBuilder.startBooleanToggle(Text.of("Enable Emissive Elytras"), ETFConfigData.enableElytra).setDefaultValue(true)
                .setTooltip(new TranslatableText("""
                        Allows Elytra to use emissive textures
                        Elytras only have emissive support as the CIT mod
                        already handles customizing these and is fully
                        compatible with ETF's emissive textures.""")).setSaveConsumer(newValue -> ETFConfigData.enableElytra = newValue).build());
        specialEmissives.add(1, entryBuilder.startBooleanToggle(Text.of("Emissive Shields"), ETFConfigData.specialEmissiveShield).setDefaultValue(true)
                .setTooltip(new TranslatableText("enables emissive shield textures")).setSaveConsumer(newValue -> ETFConfigData.specialEmissiveShield = newValue).build());
        //specialEmissives.add(2, entryBuilder.startBooleanToggle(Text.of("null"), ETFConfigData.restrictBlock).setDefaultValue(true)
        // .setTooltip(new TranslatableText("null")).setSaveConsumer(newValue -> ETFConfigData.restrictBlock = newValue).build());
        emissives.add(3, specialEmissives.build());


        optifineOptions.addEntry(emissives.build());
        SubCategoryBuilder blinking = entryBuilder.startSubCategory(Text.of("Blinking Mob settings"));
        blinking.add(0, entryBuilder.startBooleanToggle(Text.of("Enable blinking mobs"), ETFConfigData.enableBlinking)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Sets whether or not mobs will try to blink
                        The Mob must have a texture with it's eyes
                        closed named "textureName_blink.png".
                        And an optional texture with it's eyes half
                        closed named "textureName_blink2.png".""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableBlinking = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        blinking.add(1, entryBuilder.startIntSlider(Text.of("Blinking frequency"), ETFConfigData.blinkFrequency, 1, 200)
                .setDefaultValue(200) // Recommended: Used when user click "Reset"
                .setMin(1)
                .setMax(1024)
                .setTooltip(new TranslatableText("""
                        sets how often textures will randomly blink
                        if set to 100 mobs will blink randomly after
                        1 second delay and then at a random time from
                        1 to 100 ticks (0-5 seconds)
                        This can be set / overridden per mob
                        by the resource-pack
                        See the mod download page for details""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.blinkFrequency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        blinking.add(2, entryBuilder.startIntSlider(Text.of("Blinking Length"), ETFConfigData.blinkLength, 0, 20)
                .setDefaultValue(1) // Recommended: Used when user click "Reset"
                .setMin(0)
                .setMax(20)
                .setTooltip(new TranslatableText("""
                        sets how long textures will blink for
                        0 = 1 tick, 19 = 20 ticks / 1 second
                        This can be set / overridden per mob
                        by the resource-pack
                        See the mod download page for details""")) // Optional: Shown when the user hover over this option
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
                         - maybe more...""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnabled = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        players.add(1, entryBuilder.startBooleanToggle(Text.of("Enable Player Skin Feature for Enemy Teams"), ETFConfigData.enableEnemyTeamPlayersSkinFeatures)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Allows player skins to be enabled/disabled
                        for players on opposing teams in PVP games
                        otherwise they may be harder to see
                        and may affect balance
                        ///THIS SETTING REQUIRES A RESTART\\\\\\""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enableEnemyTeamPlayersSkinFeatures = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        players.add(2, entryBuilder.startBooleanToggle(Text.of("Player Skin Feature: Transparency"), ETFConfigData.skinFeaturesEnableTransparency)
                .setDefaultValue(true) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                        Allows player skins to be transparent on the base texture
                        the texture can only be an average of
                         60% transparent to prevent abuse
                        - uses transparency in the skin texture itself""")) // Optional: Shown when the user hover over this option
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
                        //REQUIRES RESTART\\\\""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.skinFeaturesEnableFullTransparency = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        optifineOptions.addEntry(players.build());

        optifineOptions.addEntry(entryBuilder.startEnumSelector(Text.of("Custom potion effects"), ETFConfig.enchantedPotionEffectsEnum.class, ETFConfigData.enchantedPotionEffects)
                .setDefaultValue(ETFConfig.enchantedPotionEffectsEnum.NONE)
                .setTooltip(new TranslatableText("""
                        currently only works when the mob first loads
                        Will display enchanted mobs with different
                        effects other than just particles""")) // Optional: Shown when the user hover over this option
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
            ETFUtils.saveConfig();
            ETFUtils.resetVisuals();

        });
        //MinecraftClient.getInstance().openScreen(screen);
        return builder.setTransparentBackground(isTransparent).build();
    }
}