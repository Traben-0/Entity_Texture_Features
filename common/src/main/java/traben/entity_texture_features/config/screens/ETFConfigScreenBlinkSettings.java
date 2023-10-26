package traben.entity_texture_features.config.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//inspired by puzzles custom gui code
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
public class ETFConfigScreenBlinkSettings extends ETFConfigScreen {
    protected ETFConfigScreenBlinkSettings(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.blinking_mob_settings_sub.title"), parent);

    }


    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(getETFButton((int) (this.width * 0.55), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.BACK,
                (button) -> Objects.requireNonNull(client).setScreen(parent)));
        this.addDrawableChild(getETFButton((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                (button) -> {
                    //temporaryETFConfig = new ETFConfig();
                    ETFConfigScreenMain.temporaryETFConfig.enableBlinking = true;
                    ETFConfigScreenMain.temporaryETFConfig.blinkFrequency = 150;
                    ETFConfigScreenMain.temporaryETFConfig.blinkLength = 1;

                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }));

        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.2), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".blinking_mob_settings.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableBlinking ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableBlinking = !ETFConfigScreenMain.temporaryETFConfig.enableBlinking;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".blinking_mob_settings.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableBlinking ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blinking_mob_settings.tooltip")
        ));
        int sliderWidth = (int) (this.width * 0.025);
        int sliderHeight = 20;
        if (sliderWidth > 400)
            sliderHeight = 40;
        if (sliderWidth > 800)
            sliderHeight = 80;
        if (sliderWidth > 1600)
            sliderHeight = 16;
        this.addDrawableChild(new SliderWidget((int) (this.width * 0.025), (int) (this.height * 0.3), (int) (this.width * 0.45), sliderHeight,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blink_frequency.title").getString()
                        + ": " + (ETFConfigScreenMain.temporaryETFConfig.blinkFrequency)),
                ETFConfigScreenMain.temporaryETFConfig.blinkFrequency / 1024f
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blink_frequency.title").getString()
                        + ": " + (ETFConfigScreenMain.temporaryETFConfig.blinkFrequency)));
            }

            @Override
            protected void applyValue() {
                //                                                             value * (maximum-min) + min
                ETFConfigScreenMain.temporaryETFConfig.blinkFrequency = (int) (this.value * 1023 + 1);
            }
        });

        this.addDrawableChild(new SliderWidget((int) (this.width * 0.025), (int) (this.height * 0.4), (int) (this.width * 0.45), sliderHeight,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blink_length.title").getString()
                        + ": " + (ETFConfigScreenMain.temporaryETFConfig.blinkLength)),
                ETFConfigScreenMain.temporaryETFConfig.blinkLength / 20f
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blink_length.title").getString()
                        + ": " + (ETFConfigScreenMain.temporaryETFConfig.blinkLength)));
            }

            @Override
            protected void applyValue() {
                //                                                             value * (maximum-min) + min
                ETFConfigScreenMain.temporaryETFConfig.blinkLength = (int) (this.value * 20);
            }

        });


    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String[] strings = ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blink_frequency.tooltip").getString().split("\n");
        List<Text> lines = new ArrayList<>();
        lines.add(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blink_frequency.title"));
        lines.add(Text.of(""));
        for (String str :
                strings) {
            lines.add(Text.of(str.strip()));
        }
        int i = 0;
        for (Text txt :
                lines) {
            context.drawTextWithShadow(textRenderer, txt, (int) (width * 0.5), (int) (height * 0.18) + i, 0xFFFFFF);
            i += txt.getString().isBlank() ? 5 : 10;
        }

        String[] strings2 = ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blink_length.tooltip").getString().split("\n");
        List<Text> lines2 = new ArrayList<>();
        lines2.add(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blink_length.title"));
        lines2.add(Text.of(""));
        for (String str :
                strings2) {
            lines2.add(Text.of(str.strip()));
        }
        int i2 = 0;
        for (Text txt :
                lines2) {
            context.drawTextWithShadow(textRenderer, txt, (int) (width * 0.5), (int) (height * 0.58) + i2, 0xFFFFFF);
            i2 += txt.getString().isBlank() ? 5 : 10;
        }
    }

}
