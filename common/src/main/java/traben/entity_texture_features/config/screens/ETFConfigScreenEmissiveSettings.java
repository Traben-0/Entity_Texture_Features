package traben.entity_texture_features.config.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.features.ETFManager;

import java.util.Objects;

//inspired by puzzles custom gui code
public class ETFConfigScreenEmissiveSettings extends ETFConfigScreen {
    protected ETFConfigScreenEmissiveSettings(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".emissive_settings.title"), parent);

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
                    ETFConfigScreenMain.temporaryETFConfig.enableEmissiveTextures = true;
                    // ETFConfigScreenMain.temporaryETFConfig.fullBrightEmissives = false;
                    ETFConfigScreenMain.temporaryETFConfig.emissiveRenderMode = ETFManager.EmissiveRenderModes.DULL;
                    ETFConfigScreenMain.temporaryETFConfig.alwaysCheckVanillaEmissiveSuffix = true;
                    ETFConfigScreenMain.temporaryETFConfig.enableEmissiveBlockEntities = true;
                    Objects.requireNonNull(client).setScreen(new ETFConfigScreenEmissiveSettings(parent));
                    this.close();
                }));

        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.3), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".enable_emissive_textures.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableEmissiveTextures ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableEmissiveTextures = !ETFConfigScreenMain.temporaryETFConfig.enableEmissiveTextures;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".enable_emissive_textures.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableEmissiveTextures ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".enable_emissive_textures.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.4), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".emissive_mode.title"
                ).getString() + ": " + ETFConfigScreenMain.temporaryETFConfig.emissiveRenderMode.toString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.emissiveRenderMode = ETFConfigScreenMain.temporaryETFConfig.emissiveRenderMode.next();
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".emissive_mode.title"
                    ).getString() + ": " + ETFConfigScreenMain.temporaryETFConfig.emissiveRenderMode.toString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".emissive_mode.tooltip2")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.5), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".always_check_vanilla_emissive_suffix.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.alwaysCheckVanillaEmissiveSuffix ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.alwaysCheckVanillaEmissiveSuffix = !ETFConfigScreenMain.temporaryETFConfig.alwaysCheckVanillaEmissiveSuffix;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".always_check_vanilla_emissive_suffix.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.alwaysCheckVanillaEmissiveSuffix ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".always_check_vanilla_emissive_suffix.tooltip")
        ));


        this.addDrawableChild(getETFButton((int) (this.width * 0.525), (int) (this.height * 0.3), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".emissive_block_entity.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableEmissiveBlockEntities ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableEmissiveBlockEntities = !ETFConfigScreenMain.temporaryETFConfig.enableEmissiveBlockEntities;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".emissive_block_entity.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableEmissiveBlockEntities ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".emissive_block_entity.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.525), (int) (this.height * 0.4), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".armor_enable"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableArmorAndTrims ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableArmorAndTrims = !ETFConfigScreenMain.temporaryETFConfig.enableArmorAndTrims;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".armor_enable"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableArmorAndTrims ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".armor_enable.tooltip")
        ));

    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredTextWithShadow(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".special_emissive_settings.title").asOrderedText(), (int) (width * 0.75), (int) (height * 0.25), 0xFFFFFF);

    }

}
