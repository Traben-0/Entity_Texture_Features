package traben.entity_texture_features.config.screens;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

//inspired by puzzles custom gui code
public class ETFConfigScreenWarnings extends ETFConfigScreen {
    final ObjectOpenHashSet<ConfigWarning> warningsFound;

    protected ETFConfigScreenWarnings(Screen parent, ObjectOpenHashSet<ConfigWarning> warningsFound) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warnings.title"), parent);
        this.warningsFound = warningsFound;

    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(getETFButton((int) (this.width * 0.55), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.BACK,
                (button) -> Objects.requireNonNull(client).setScreen(parent)));
        this.addDrawableChild(getETFButton((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".ignore_all"),
                (button) -> {
                    //temporaryETFConfig = new ETFConfig();
                    ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.addAll(List.of(ConfigWarning.values()));
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }));

        double offset = 0.0;

        //render config error first
        if (ETFClientCommon.configHadLoadError) {
            offset = 0.1;
        }

        for (ConfigWarning warning :
                warningsFound) {
            if (warning.showDisableButton) {
                ButtonWidget butt = getETFButton((int) (this.width * 0.75), (int) (this.height * (0.25 + offset)), (int) (this.width * 0.17), 20,
                        Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
                                (ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.contains(warning) ? ScreenTexts.YES : ScreenTexts.NO).getString()),
                        (button) -> {
                            //button.active = false;
                            if (ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.contains(warning)) {
                                ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.remove(warning);
                            } else {
                                ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.add(warning);
                            }
                            button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
                                    (ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.contains(warning) ? ScreenTexts.YES : ScreenTexts.NO).getString()));
                        },
                        ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.ignore_description")
                );
                // butt.active = !ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.contains(warning);
                this.addDrawableChild(butt);
            }

            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }


    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);


        context.drawCenteredTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_instruction"), (int) (width * 0.5), (int) (height * 0.18), 0xFFFFFF);
        //drawCenteredText(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_instruction2"), (int) (width * 0.5), (int) (height * 0.23), 0xFFFFFF);
        double offset = 0.0;

        if (ETFClientCommon.configHadLoadError) {
            context.drawCenteredTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_config_load"), (int) (width * 0.5), (int) (height * 0.28), 11546150);
            offset = 0.1;
        }

        for (ConfigWarning warning :
                warningsFound) {

            context.drawTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.text_translation_key), (int) (this.width * 0.05), (int) (this.height * (0.25 + offset)), 0xFFFFFF);
//            if(warning.hasAction){
            context.drawTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.text2_translation_key), (int) (this.width * 0.05), (int) (this.height * (0.29 + offset)), 0x888888);

            //}
            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }


    }

    public enum ConfigWarning {
        FIGURA(true, "figura", "config." + ETFClientCommon.MOD_ID + ".warn.figura.text.1", "config." + ETFClientCommon.MOD_ID + ".warn.figura.text.2"),
        //SKINLAYERS(false, "skinlayers", "config." + ETFClientCommon.MOD_ID + ".warn.skinlayers.text.1", "config." + ETFClientCommon.MOD_ID + ".warn.skinlayers.text.2"),
        ENHANCED_BLOCK_ENTITIES(false, "enhancedblockentities", "config." + ETFClientCommon.MOD_ID + ".warn.ebe.text.1", "config." + ETFClientCommon.MOD_ID + ".warn.ebe.text.2"),
        QUARK(false, "quark", "config." + ETFClientCommon.MOD_ID + ".warn.quark.text.3", "config." + ETFClientCommon.MOD_ID + ".warn.quark.text.4"),
        IRIS(false, "iris", "config." + ETFClientCommon.MOD_ID + ".warn.iris.text.1", "config." + ETFClientCommon.MOD_ID + ".warn.iris.text.2"),

        IRIS_AND_3D_SKIN_trim_warn(false,()-> ETFVersionDifferenceHandler.isThisModLoaded("iris") && ETFClientCommon.SKIN_LAYERS_DETECTED,
                "config." + ETFClientCommon.MOD_ID + ".warn.iris_3d.text.1", "config." + ETFClientCommon.MOD_ID + ".warn.iris_3d.text.2"),
        NO_CEM(false,()-> !ETFVersionDifferenceHandler.isThisModLoaded("entity_model_features") && !ETFVersionDifferenceHandler.isThisModLoaded("cem"),
                "config." + ETFClientCommon.MOD_ID + ".warn.no_emf.text.1", "config." + ETFClientCommon.MOD_ID + ".warn.no_emf.text.2");

        final public boolean showDisableButton;
        final private Supplier<Boolean> condition;
        final private String text_translation_key;
        final private String text2_translation_key;

        ConfigWarning(boolean showDisableButton, Supplier<Boolean> condition, String text_translation_key, String text2_translation_key) {
            this.showDisableButton = showDisableButton;
            this.condition = condition;
            this.text_translation_key = text_translation_key;
            this.text2_translation_key = text2_translation_key;
        }
        ConfigWarning(boolean showDisableButton, String modName, String text_translation_key, String text2_translation_key) {
            this.showDisableButton = showDisableButton;
            this.condition = ()-> ETFVersionDifferenceHandler.isThisModLoaded(modName);
            this.text_translation_key = text_translation_key;
            this.text2_translation_key = text2_translation_key;
        }

        public boolean isConditionMet() {
            return condition.get();
        }

    }
}
