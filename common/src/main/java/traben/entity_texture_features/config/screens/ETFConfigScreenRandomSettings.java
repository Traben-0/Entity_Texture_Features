package traben.entity_texture_features.config.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;

import java.util.Objects;

//inspired by puzzles custom gui code
public class ETFConfigScreenRandomSettings extends ETFConfigScreen {
    protected ETFConfigScreenRandomSettings(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".random_settings.title"), parent);

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
                    ETFConfigScreenMain.temporaryETFConfig.enableCustomTextures = true;
                    ETFConfigScreenMain.temporaryETFConfig.textureUpdateFrequency_V2 = ETFConfig.UpdateFrequency.Fast;
                    ETFConfigScreenMain.temporaryETFConfig.enableTridents = true;
                    ETFConfigScreenMain.temporaryETFConfig.enableCustomBlockEntities = true;
                    ETFConfigScreenMain.temporaryETFConfig.restrictMoonPhase = true;
                    ETFConfigScreenMain.temporaryETFConfig.restrictDayTime = true;
                    ETFConfigScreenMain.temporaryETFConfig.restrictWeather = true;
                    ETFConfigScreenMain.temporaryETFConfig.restrictBiome = true;
                    ETFConfigScreenMain.temporaryETFConfig.restrictBlock = true;
                    ETFConfigScreenMain.temporaryETFConfig.restrictHeight = true;
                    ETFConfigScreenMain.temporaryETFConfig.disableVanillaDirectoryVariantTextures = false;

                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".enable_custom_textures.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableCustomTextures ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableCustomTextures = !ETFConfigScreenMain.temporaryETFConfig.enableCustomTextures;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".enable_custom_textures.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableCustomTextures ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".enable_custom_textures.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".texture_update_frequency.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.textureUpdateFrequency_V2)),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.textureUpdateFrequency_V2 = ETFConfigScreenMain.temporaryETFConfig.textureUpdateFrequency_V2.next();
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".texture_update_frequency.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.textureUpdateFrequency_V2)));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".texture_update_frequency.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".enable_tridents.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableTridents ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableTridents = !ETFConfigScreenMain.temporaryETFConfig.enableTridents;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".enable_tridents.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableTridents ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".enable_tridents.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.5), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".custom_block_entity.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableCustomBlockEntities ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableCustomBlockEntities = !ETFConfigScreenMain.temporaryETFConfig.enableCustomBlockEntities;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".custom_block_entity.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableCustomBlockEntities ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".custom_block_entity.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.6), (int) (this.width * 0.6), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".restrict_update_properties"),
                (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenRandomRestrictSettings(this)),
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".restrict_update_properties.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.7), (int) (this.width * 0.6), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".disable_default_directory.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.disableVanillaDirectoryVariantTextures ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.disableVanillaDirectoryVariantTextures = !ETFConfigScreenMain.temporaryETFConfig.disableVanillaDirectoryVariantTextures;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".disable_default_directory.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.disableVanillaDirectoryVariantTextures ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".disable_default_directory.tooltip")
        ));


    }


}
