package traben.entity_texture_features.config.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;

import java.util.Objects;

//inspired by puzzles custom gui code
public class ETFConfigScreenGeneralSettings extends ETFConfigScreen {
    protected ETFConfigScreenGeneralSettings(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".general_settings.title"), parent);

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
                    ETFConfigScreenMain.temporaryETFConfig.elytraThicknessFix = true;
                    ETFConfigScreenMain.temporaryETFConfig.debugLoggingMode = ETFConfig.DebugLogMode.None;
                    ETFConfigScreenMain.temporaryETFConfig.illegalPathSupportMode = ETFConfig.IllegalPathMode.None;
                    ETFConfigScreenMain.temporaryETFConfig.hideConfigButton = false;
                    //ETFConfigScreenMain.temporaryETFConfig.zombiePiglinRightEarEnabled = false;
                    //ETFConfigScreenMain.temporaryETFConfig.temporary_fixIrisPBR = false;
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".elytra_thickness_fix.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.elytraThicknessFix ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.elytraThicknessFix = !ETFConfigScreenMain.temporaryETFConfig.elytraThicknessFix;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".elytra_thickness_fix.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.elytraThicknessFix ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".elytra_thickness_fix.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".debug_logging_mode.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.debugLoggingMode)),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.debugLoggingMode = ETFConfigScreenMain.temporaryETFConfig.debugLoggingMode.next();
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".debug_logging_mode.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.debugLoggingMode)));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".debug_logging_mode.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".allow_illegal_texture_paths.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.illegalPathSupportMode)),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.illegalPathSupportMode = ETFConfigScreenMain.temporaryETFConfig.illegalPathSupportMode.next();
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".allow_illegal_texture_paths.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.illegalPathSupportMode)));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".allow_illegal_texture_paths.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.5), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".hide_button"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.hideConfigButton ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.hideConfigButton = !ETFConfigScreenMain.temporaryETFConfig.hideConfigButton;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".hide_button"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.hideConfigButton ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".hide_button.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.6), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".warden.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableFullBodyWardenTextures ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableFullBodyWardenTextures = !ETFConfigScreenMain.temporaryETFConfig.enableFullBodyWardenTextures;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".warden.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableFullBodyWardenTextures ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warden.tooltip")
        ));
    }




}
