package traben.entity_texture_features.config.screens.warnings;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreen;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;

import java.util.Objects;

//inspired by puzzles custom gui code
public class ETFConfigScreenWarnings extends ETFConfigScreen {
    final ObjectOpenHashSet<ETFConfigWarning> warningsFound;

    public ETFConfigScreenWarnings(Screen parent, ObjectOpenHashSet<ETFConfigWarning> warningsFound) {
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
                    for (ETFConfigWarning warn :
                            ETFConfigWarnings.getRegisteredWarnings()) {
                        ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs2.add(warn.getID());
                    }

                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }));

        double offset = 0.0;

        //render config error first
        if (ETFClientCommon.configHadLoadError) {
            offset = 0.1;
        }

        for (ETFConfigWarning warning :
                warningsFound) {
            if (warning.doesShowDisableButton()) {
                ButtonWidget butt = getETFButton((int) (this.width * 0.75), (int) (this.height * (0.25 + offset)), (int) (this.width * 0.17), 20,
                        Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
                                (ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs2.contains(warning.getID()) ? ScreenTexts.YES : ScreenTexts.NO).getString()),
                        (button) -> {
                            //button.active = false;
                            if (ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs2.contains(warning.getID())) {
                                ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs2.remove(warning.getID());
                            } else {
                                ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs2.add(warning.getID());
                            }
                            button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
                                    (ETFConfigScreenMain.temporaryETFConfig.ignoredConfigs2.contains(warning.getID()) ? ScreenTexts.YES : ScreenTexts.NO).getString()));
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


        context.drawCenteredTextWithShadow(textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_instruction"), (int) (width * 0.5), (int) (height * 0.18), 0xFFFFFF);
        //drawCenteredText(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_instruction2"), (int) (width * 0.5), (int) (height * 0.23), 0xFFFFFF);
        double offset = 0.0;

        if (ETFClientCommon.configHadLoadError) {
            context.drawCenteredTextWithShadow(textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_config_load"), (int) (width * 0.5), (int) (height * 0.28), 11546150);
            offset = 0.1;
        }

        for (ETFConfigWarning warning :
                warningsFound) {

            context.drawTextWithShadow(textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.getTitle()), (int) (this.width * 0.05), (int) (this.height * (0.25 + offset)), 0xFFFFFF);
//            if(warning.hasAction){
            context.drawTextWithShadow(textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.getSubTitle()), (int) (this.width * 0.05), (int) (this.height * (0.29 + offset)), 0x888888);

            //}
            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }


    }


}
