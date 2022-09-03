package traben.entity_texture_features.config.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//inspired by puzzles custom gui code
public class ETFConfigScreenEmissiveFixSettings extends ETFConfigScreen {
    protected ETFConfigScreenEmissiveFixSettings(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".emissive_fix.title"), parent);

    }


    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(getETFButton((int) (this.width * 0.55), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.BACK,
                (button) -> {
                    Objects.requireNonNull(client).setScreen(parent);
                }));
        this.addDrawableChild(getETFButton((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".reset_defaults"),
                (button) -> {

                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveElytra = true;
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveArmour = true;
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissivePlayers = true;
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveMobs = true;
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveBlockEntity = true;

                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }));

        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.3), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".emissive_fix.elytra"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveElytra ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveElytra = !ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveElytra;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".emissive_fix.elytra"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveElytra ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.4), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".emissive_fix.armour"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveArmour ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveArmour = !ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveArmour;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".emissive_fix.armour"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveArmour ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.5), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".emissive_fix.players"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissivePlayers ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissivePlayers = !ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissivePlayers;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".emissive_fix.players"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissivePlayers ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.6), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".emissive_fix.mobs"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveMobs ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveMobs = !ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveMobs;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".emissive_fix.mobs"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveMobs ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.7), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".emissive_fix.block_entity"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveBlockEntity ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveBlockEntity = !ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveBlockEntity;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".emissive_fix.block_entity"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.removePixelsUnderEmissiveBlockEntity ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                }
        ));
//        this.addDrawableChild(getETFButton((int) (this.width * 0.525), (int) (this.height * 0.3), (int) (this.width * 0.45), 20,
//                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
//                        "config." + ETFClientCommon.MOD_ID + ".enable_elytra.title"
//                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableElytra ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
//                (button) -> {
//                    ETFConfigScreenMain.temporaryETFConfig.enableElytra = !ETFConfigScreenMain.temporaryETFConfig.enableElytra;
//                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
//                            "config." + ETFClientCommon.MOD_ID + ".enable_elytra.title"
//                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableElytra ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
//                },
//                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".enable_elytra.tooltip")
//        ));


    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        String[] strings = ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".remove_pixels.info").getString().split("\n");
        List<Text> lines = new ArrayList<>();

        for (String str :
                strings) {
            lines.add(Text.of(str.strip()));
        }
        int i = 0;
        for (Text txt :
                lines) {
            drawTextWithShadow(matrices, textRenderer, txt, (int) (width * 0.5), (int) (height * 0.18) + i, 0xFFFFFF);
            i += txt.getString().isBlank() ? 7 : 10;
        }
    }

}
