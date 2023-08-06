package traben.entity_texture_features.config.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;
import traben.entity_texture_features.utils.ETFUtils2;

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
        this.addDrawableChild(getETFButton((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                (button) -> {
                    //temporaryETFConfig = new ETFConfig();
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnabled = true;
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableFullTransparency = false;
                    ETFConfigScreenMain.temporaryETFConfig.skinFeaturesEnableTransparency = true;
                    ETFConfigScreenMain.temporaryETFConfig.enableEnemyTeamPlayersSkinFeatures = true;
                    ETFConfigScreenMain.temporaryETFConfig.tryETFTransparencyForAllSkins = false;
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
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
        this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.6), (int) (this.width * 0.45), 20,
                Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".skin_features_try_transparency_for_all.title"
                ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.tryETFTransparencyForAllSkins ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    ETFConfigScreenMain.temporaryETFConfig.tryETFTransparencyForAllSkins = !ETFConfigScreenMain.temporaryETFConfig.tryETFTransparencyForAllSkins;
                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".skin_features_try_transparency_for_all.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.tryETFTransparencyForAllSkins ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".skin_features_try_transparency_for_all.tooltip")
        ));
        if(ETFClientCommon.SKIN_LAYERS_DETECTED) {
            this.addDrawableChild(getETFButton((int) (this.width * 0.025), (int) (this.height * 0.7), (int) (this.width * 0.45), 20,
                    Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config." + ETFClientCommon.MOD_ID + ".skin_layers_patch.title"
                    ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.use3DSkinLayerPatch ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                    (button) -> {
                        ETFConfigScreenMain.temporaryETFConfig.use3DSkinLayerPatch = !ETFConfigScreenMain.temporaryETFConfig.use3DSkinLayerPatch;
                        button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                                "config." + ETFClientCommon.MOD_ID + ".skin_layers_patch.title"
                        ).getString() + ": " + (ETFConfigScreenMain.temporaryETFConfig.use3DSkinLayerPatch ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                    },
                    ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".skin_layers_patch.tooltip")
            ));
        }

        //capture conditions separately to generate specific instructions for users to fix tool
        boolean condition1 = ETFClientCommon.ETFConfigData.skinFeaturesEnabled;
        boolean condition2 = !ETFVersionDifferenceHandler.isFabric() || ETFVersionDifferenceHandler.isThisModLoaded("fabric");
        boolean condition3 = MinecraftClient.getInstance().player != null;
        boolean condition4 = ETFPlayerTexture.clientPlayerOriginalSkinImageForTool != null;

        canLaunchTool = condition1 && condition2 && condition3 && condition4;

        StringBuilder reasonText = new StringBuilder();
        if(!canLaunchTool){
            //log reason
            reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_0").getString());
            if(!condition1){
                reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_1").getString());
            }
            if(!condition2 ){
                reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_2").getString());
            }
            if(!condition3){
                reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_3").getString());
            }
            if(!condition4){
                reasonText.append(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_4").getString());
            }
            ETFUtils2.logWarn(reasonText.toString());
        }


        ButtonWidget skinTool = getETFButton((int) (this.width * 0.525), (int) (this.height * 0.5), (int) (this.width * 0.45), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.button." + (
                        canLaunchTool ? "enabled" : "disabled"
                )),
                (button) -> Objects.requireNonNull(client).setScreen(playerSkinEditorScreen),
                Text.of(reasonText.toString())
        );
        skinTool.active = canLaunchTool;

        this.addDrawableChild(skinTool);




    }

    private boolean canLaunchTool=false;


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.title"), (int) (width * 0.75), (int) (height * 0.35), 0xFFFFFF);
        context.drawCenteredTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.button_desc.1"), (int) (width * 0.75), (int) (height * 0.4), 0xCCCCCC);
        context.drawCenteredTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.button_desc.2"), (int) (width * 0.75), (int) (height * 0.45), 0xCCCCCC);
        if(!canLaunchTool)
            context.drawCenteredTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.button_desc.fail2"), (int) (width * 0.75), (int) (height * 0.6), 0xCC5555);
        context.drawCenteredTextWithShadow( textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.info"), (int) (width * 0.5), (int) (height * 0.8), 0xCCCCCC);
    }

}
