package traben.entity_texture_features.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import traben.entity_texture_features.client.entity_texture_features_METHODS;

import java.awt.*;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.ETFConfigData;
import static traben.entity_texture_features.client.entity_texture_features_CLIENT.puzzleDetected;

public class ETFConfigScreen implements entity_texture_features_METHODS {
    public Screen getConfigScreen(Screen parent, boolean isTransparent) {
            // Return the screen here with the one you created from Cloth Config Builder
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("Entity Texture Features by Traben"));
            //Screen ModConfigScreen = builder.build();
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory main = builder.getOrCreateCategory(Text.of("Settings"));
        if(puzzleDetected){
            main.addEntry(entryBuilder.startTextDescription(Text.of("@Motschen's Mod 'Puzzle' was detected:\n please ensure you disable emissive entities in that mod's settings!"))
                    .setColor(new Color(240,195,15).getRGB())
                    .build()); // Builds the option entry for cloth config
        }
            main.addEntry(entryBuilder.startBooleanToggle(Text.of("Always check the 'default' emissive?"), ETFConfigData.alwaysCheckVanillaEmissiveSuffix)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            Most resource packs use the emissive suffix _e
                            however this can be overridden by packs if they
                            want to use a different suffix.
                            If this is true the mod will always check for
                            '_e' suffixes even when set differently by a resource pack""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> ETFConfigData.alwaysCheckVanillaEmissiveSuffix = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            main.addEntry(entryBuilder.startBooleanToggle(Text.of("Emissive texture Z-Fighting / Shader fix"), ETFConfigData.doShadersEmissiveFix)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("""
                            If true this will make emissive textures float
                            slightly above the model.
                            This can fix Z-Fighting present in some shaders.
                            This will not always look right and can desync
                            with the models animation.""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.doShadersEmissiveFix = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        main.addEntry(entryBuilder.startEnumSelector(Text.of("Mob potion effects"),ETFConfig.enchantedPotionEffectsEnum.class, ETFConfigData.enchantedPotionEffects)
            .setDefaultValue(ETFConfig.enchantedPotionEffectsEnum.NONE)
                .setTooltip(new TranslatableText("""
                            currently only works when the mob loads""")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> ETFConfigData.enchantedPotionEffects = newValue)
                    .build());

//            SubCategoryBuilder dashMobs= entryBuilder.startSubCategory(Text.of("Additional dash settings"));
//            dashMobs.add(0, entryBuilder.startBooleanToggle(Text.of("Creepers can dash"), config.creeperCanDash)
//                    .setDefaultValue(true).setSaveConsumer(newValue -> config.creeperCanDash = newValue).build());
//            dashMobs.add(1, entryBuilder.startBooleanToggle(Text.of("Skeletons can dash"), config.skeletonCanDash)
//                    .setDefaultValue(true).setSaveConsumer(newValue -> config.skeletonCanDash = newValue).build());
//            Hostiles.addEntry(dashMobs.build());



            builder.setSavingRunnable(() -> {
                // Serialise the config into the config file. This will be called last after all variables are updated.
                saveConfig();
                resetVisuals();

          });
            //MinecraftClient.getInstance().openScreen(screen);
            return builder.setTransparentBackground(isTransparent).build();
        }
}