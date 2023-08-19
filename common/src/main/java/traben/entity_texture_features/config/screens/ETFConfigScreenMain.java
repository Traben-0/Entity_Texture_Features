package traben.entity_texture_features.config.screens;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Objects;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

//inspired by puzzles custom gui code
public class ETFConfigScreenMain extends ETFConfigScreen {

    static ETFConfig temporaryETFConfig = null;
    final ObjectOpenHashSet<ETFConfigScreenWarnings.ConfigWarning> warningsFound = new ObjectOpenHashSet<>();

     ETFConfigScreenWarnings warningsScreen;
    final ETFConfigScreenSkinSettings playerSkinSettingsScreen = new ETFConfigScreenSkinSettings(this);
    final ETFConfigScreenRandomSettings randomSettingsScreen = new ETFConfigScreenRandomSettings(this);
    final ETFConfigScreenEmissiveSettings emissiveSettingsScreen = new ETFConfigScreenEmissiveSettings(this);
    final ETFConfigScreenBlinkSettings blinkSettingsScreen = new ETFConfigScreenBlinkSettings(this);
    final ETFConfigScreenGeneralSettings generalSettingsScreen = new ETFConfigScreenGeneralSettings(this);
    boolean shownWarning = false;
    int warningCount = 0;

    public ETFConfigScreenMain(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".title"), parent);
        temporaryETFConfig = ETFConfig.copyFrom(ETFConfigData);
        findWarnings();
    }

    public ETFConfigScreenMain(Screen parent,ETFConfig newTemp) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".title"), parent);
        temporaryETFConfig = newTemp;
        findWarnings();
    }
    private void findWarnings(){

        if (ETFClientCommon.configHadLoadError) {
            shownWarning = true;
            warningCount++;
        }

        for (ETFConfigScreenWarnings.ConfigWarning warning :
                ETFConfigScreenWarnings.ConfigWarning.values()) {
            if (warning.isConditionMet()) {
                shownWarning = true;
                warningCount++;
                warningsFound.add(warning);
            }
        }
        //warningsFound.addAll(Arrays.asList(ETFConfigScreenWarnings.ConfigWarning.values()));
        warningsScreen = new ETFConfigScreenWarnings(this, warningsFound);
    }

    @Override
    public void close() {
//        if (MinecraftClient.getInstance().player != null) {
//            ETFManager.getInstance().PLAYER_TEXTURE_MAP.removeEntryOnly(MinecraftClient.getInstance().player.getUuid());
//            ETFManager.getInstance().ENTITY_BLINK_TIME.put(MinecraftClient.getInstance().player.getUuid(), 0L);
//        }
        ETFManager.resetInstance();
        super.close();
    }

    @Override
    protected void init() {
        super.init();


        if (shownWarning) {
            this.addDrawableChild(new ButtonWidget((int) (this.width * 0.1), (int) (this.height * 0.1) - 15, (int) (this.width * 0.2), 20,
                    Text.of(""),
                    (button) -> Objects.requireNonNull(client).setScreen(warningsScreen)));
        }

        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("gui.done"),
                (button) -> {
                    ETFConfigData = temporaryETFConfig;
                    ETFUtils2.saveConfig();
                    ETFUtils2.checkModCompatibility();
                    ETFManager.resetInstance();
                    ETFClientCommon.configHadLoadError = false;
                    Objects.requireNonNull(client).setScreen(parent);
                }));
        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                (button) -> {
                   // temporaryETFConfig = new ETFConfig();
                    Objects.requireNonNull(client).setScreen(new ETFConfigScreenMain(parent,new ETFConfig()));
                    this.close();
                }));
        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.CANCEL,
                (button) -> {
                    temporaryETFConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }));


        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.3) + 75, (int) (this.height * 0.5) + 17, 165, 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".blinking_mob_settings_sub.title"),
                (button) -> Objects.requireNonNull(client).setScreen(blinkSettingsScreen)));

        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.3) + 75, (int) (this.height * 0.5) - 10, 165, 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_settings.title"),
                (button) -> Objects.requireNonNull(client).setScreen(playerSkinSettingsScreen)));

        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.3) + 75, (int) (this.height * 0.5) - 64, 165, 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".random_settings.title"),
                (button) -> Objects.requireNonNull(client).setScreen(randomSettingsScreen)));

        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.3) + 75, (int) (this.height * 0.5) - 37, 165, 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".emissive_settings.title"),
                (button) -> Objects.requireNonNull(client).setScreen(emissiveSettingsScreen)));

        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.3) + 75, (int) (this.height * 0.5) + 44, 165, 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".general_settings.title"),
                (button) -> Objects.requireNonNull(client).setScreen(generalSettingsScreen)));

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        renderGUITexture(new Identifier(MOD_ID + ":textures/gui/icon.png"), (this.width * 0.3) - 64, (this.height * 0.5) - 64, (this.width * 0.3) + 64, (this.height * 0.5) + 64);
        if (shownWarning) {
            drawCenteredTextWithShadow(matrices, textRenderer,
                    Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warnings_main").getString() + warningCount).asOrderedText(),
                    (int) (width * 0.2), (int) (height * 0.1) - 9, 11546150);
        }

    }


}
