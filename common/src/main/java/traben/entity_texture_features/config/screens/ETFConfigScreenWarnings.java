package traben.entity_texture_features.config.screens;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.util.Arrays;
import java.util.Objects;

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
        this.addButton(getETFButton((int) (this.width * 0.55), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.BACK,
                (button) -> Objects.requireNonNull(client).openScreen(parent)));
        this.addButton(getETFButton((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".ignore_all"),
                (button) -> {
                    //temporaryETFConfig = new ETFConfig();
                    ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.addAll(Arrays.asList(ConfigWarning.values()));

                    Objects.requireNonNull(client).openScreen(new ETFConfigScreenWarnings(parent,warningsFound));
                    this.removed();
                }));

        double offset = 0.0;

        //render config error first
        if (ETFClientCommon.configHadLoadError) {
            offset = 0.1;
        }

        for (ConfigWarning warning :
                warningsFound) {

//            ButtonWidget butt = getETFButton((int) (this.width * 0.2), (int) (this.height * (0.3 + offset)), (int) (this.width * 0.6), 20,
//                    warning.getButton_translation(),
//                    (button) -> {
//                        button.active = false;
//                        ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.add(warning);
//                    },
//                    warning.getTooltip_translation()
//            );
//            butt.active = !ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs.contains(warning);
//            this.addButton(butt);

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
                this.addButton(butt);
            }

            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }


    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_instruction"), (int) (width * 0.5), (int) (height * 0.18), 0xFFFFFF);
        //drawCenteredText(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_instruction2"), (int) (width * 0.5), (int) (height * 0.23), 0xFFFFFF);
        double offset = 0.0;

        if (ETFClientCommon.configHadLoadError) {
            drawCenteredText(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_config_load"), (int) (width * 0.5), (int) (height * 0.28), 11546150);
            offset = 0.1;
        }

        for (ConfigWarning warning :
                warningsFound) {

            drawTextWithShadow(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.text_translation_key), (int) (this.width * 0.05), (int) (this.height * (0.25 + offset)), 0xFFFFFF);
//            if(warning.hasAction){
            drawTextWithShadow(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.text2_translation_key), (int) (this.width * 0.05), (int) (this.height * (0.29 + offset)), 0x888888);

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
        IRIS(false, "iris", "config." + ETFClientCommon.MOD_ID + ".warn.iris.text.1", "config." + ETFClientCommon.MOD_ID + ".warn.iris.text.2");
        //IMPERSONATE(true, "impersonate", "config." + ETFClientCommon.MOD_ID + ".warn.impersonate.text.1", "config." + ETFClientCommon.MOD_ID + ".warn.impersonate.text.2");


        final public boolean showDisableButton;
        final private String mod_id;
        final private String text_translation_key;
        final private String text2_translation_key;

        ConfigWarning(boolean showDisableButton, String mod_id, String text_translation_key, String text2_translation_key) {

            this.showDisableButton = showDisableButton;
            this.mod_id = mod_id;
            this.text_translation_key = text_translation_key;
            this.text2_translation_key = text2_translation_key;
        }

        public String getMod_id() {
            return mod_id;
        }

    }
}
