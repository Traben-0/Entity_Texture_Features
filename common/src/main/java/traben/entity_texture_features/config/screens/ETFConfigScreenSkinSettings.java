package traben.entity_texture_features.config.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;

import java.util.Objects;

//inspired by puzzles custom gui code
public class ETFConfigScreenSkinSettings extends ETFConfigScreen {
    final ETFConfigScreenSkinTool playerSkinEditorScreen = new ETFConfigScreenSkinTool(this);

    protected ETFConfigScreenSkinSettings(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_settings.title"), parent);

    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(getETFButton((int) (this.width * 0.55), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.BACK,
                (button) -> Objects.requireNonNull(client).setScreen(parent)));
        this.addDrawableChild(getETFButton((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".reset_defaults"),
                (button) -> {
                    //temporaryETFConfig = new ETFConfig();
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnabled = true;
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableFullTransparency = false;
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableTransparency = true;
                    ETFConfigScreenMain.temporaryETFConfig.enableEnemyTeamPlayersSkinFeatures = true;
                    Objects.requireNonNull(client).setScreen(new ETFConfigScreenSkinSettings(parent));
                    this.removed();
                }));

        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.2), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".player_skin_features.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnabled ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnabled = !ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnabled;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".player_skin_features.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnabled ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_features.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.3), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".enable_enemy_team_players_skin_features.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableEnemyTeamPlayersSkinFeatures ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.enableEnemyTeamPlayersSkinFeatures = !ETFConfigScreenMain.temporaryETFConfig.enableEnemyTeamPlayersSkinFeatures;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".enable_enemy_team_players_skin_features.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.enableEnemyTeamPlayersSkinFeatures ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".enable_enemy_team_players_skin_features.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.4), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".skin_features_enable_transparency.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableTransparency ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableTransparency = !ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableTransparency;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".skin_features_enable_transparency.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableTransparency ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".skin_features_enable_transparency.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.5), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".skin_features_enable_full_transparency.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableFullTransparency ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableFullTransparency = !ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableFullTransparency;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".skin_features_enable_full_transparency.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableFullTransparency ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".skin_features_enable_full_transparency.tooltip")
        ));

        boolean canLaunchTool = (
                ETFVersionDifferenceHandler.isFabric() == ETFVersionDifferenceHandler.isThisModLoaded("fabric"))
                && MinecraftClient.getInstance().player != null && ETFPlayerTexture.clientPlayerOriginalSkinImageForTool != null;

        ButtonWidget skinTool = getETFButton((int) (this.width * 0.525), (int) (this.height * 0.4), (int) (this.width * 0.45), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.button"),
                (button) -> Objects.requireNonNull(client).setScreen(playerSkinEditorScreen),
                canLaunchTool ? Text.of("") : ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.fail_tooltip")
        );
        skinTool.active = canLaunchTool;

        this.addDrawableChild(skinTool);


    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.title"), (int) (width * 0.75), (int) (height * 0.35), 0xFFFFFF);

    }

}
