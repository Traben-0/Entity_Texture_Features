package traben.entity_texture_features.config.screens;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.tconfig.TConfig;
import traben.tconfig.gui.TConfigScreen;
import traben.tconfig.gui.entries.TConfigEntry;

import java.util.HashSet;
import java.util.Set;

//inspired by puzzles custom gui code
public class ETFConfigScreenWarnings extends TConfigScreen {
    final ObjectOpenHashSet<ETFConfigWarning> warningsFound;

    public ETFConfigScreenWarnings(Screen parent, ObjectOpenHashSet<ETFConfigWarning> warningsFound) {
        super("config." + ETF.MOD_ID + ".warnings.title", parent, true);
        this.warningsFound = warningsFound;

    }

    public static Set<String> getIgnoredWarnings() {
        return ETF.warningConfigHandler.getConfig().ignoredConfigIds;
    }

    @Override
    public void close() {
        ETF.warningConfigHandler.saveToFile();
        super.close();
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETF.MOD_ID + ".ignore_all"),
                (button) -> {
                    //temporaryETFConfig = new ETFConfig();
                    for (ETFConfigWarning warn :
                            ETFConfigWarnings.getRegisteredWarnings()) {
                        getIgnoredWarnings().add(warn.getID());
                    }
                    this.init();
                    //Objects.requireNonNull(client).setScreen(parent);
                }));

        double offset = 0.0;

        for (ETFConfigWarning warning :
                warningsFound) {
            if (warning.doesShowDisableButton()) {
                ButtonWidget butt = new ButtonWidget((int) (this.width * 0.75), (int) (this.height * (0.25 + offset)),
                        (int) (this.width * 0.17), 20,
                        Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
                                        (getIgnoredWarnings().contains(warning.getID()) ? ScreenTexts.YES : ScreenTexts.NO).getString()),
                                (button) -> {
                                    //button.active = false;
                                    if (getIgnoredWarnings().contains(warning.getID())) {
                                        getIgnoredWarnings().remove(warning.getID());
                                    } else {
                                        getIgnoredWarnings().add(warning.getID());
                                    }
                                    button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
                                            (getIgnoredWarnings().contains(warning.getID()) ? ScreenTexts.YES : ScreenTexts.NO).getString()));
                                }, TConfigEntry.getTooltip(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.ignore_description")));


                this.addDrawableChild(butt);
            }

            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }
    }

    @Override
    public void render(MatrixStack context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        drawCenteredTextWithShadow(context,textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETF.MOD_ID + ".warn_instruction").asOrderedText(), (int) (width * 0.5), (int) (height * 0.18), 0xFFFFFF);
        double offset = 0.0;

        for (ETFConfigWarning warning :
                warningsFound) {
            drawTextWithShadow(context,textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.getTitle()), (int) (this.width * 0.05), (int) (this.height * (0.25 + offset)), 0xFFFFFF);
            drawTextWithShadow(context,textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.getSubTitle()), (int) (this.width * 0.05), (int) (this.height * (0.29 + offset)), 0x888888);
            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }


    }

    @SuppressWarnings("CanBeFinal")
    public static class WarningConfig extends TConfig.NoGUI {
        public Set<String> ignoredConfigIds = new HashSet<>();
    }


}
