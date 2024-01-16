package traben.entity_texture_features.config.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;

import java.util.Objects;

//inspired by puzzles custom gui code
public class ETFConfigScreenDebugSettings extends ETFConfigScreen {
    protected ETFConfigScreenDebugSettings(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.debug_screen.title"), parent);

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
                    ETFConfigScreenMain.temporaryETFConfig.debugLoggingMode = ETFConfig.DebugLogMode.None;
                    ETFConfigScreenMain.temporaryETFConfig.illegalPathSupportMode = ETFConfig.IllegalPathMode.None;
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }));


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
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
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".log_creation"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.logTextureDataInitialization ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.logTextureDataInitialization = !ETFConfigScreenMain.temporaryETFConfig.logTextureDataInitialization;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".log_creation"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.logTextureDataInitialization ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".log_creation.tooltip")
        ));


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.6), (int) (this.width * 0.6), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.debug_screen.mass_log"),
                (button) -> {
                    ETFManager.getInstance().doTheBigBoyPrintoutKronk();
                    button.setMessage(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.debug_screen.mass_log.done"));
                    button.active = false;
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.debug_screen.mass_log.tooltip")
        ));

    }


}
