package traben.entity_features.config.gui;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_features.config.EFConfig;
import traben.entity_features.config.EFConfigHandler;
import traben.entity_features.config.EFConfigWarning;
import traben.entity_features.config.EFConfigWarnings;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.util.HashSet;
import java.util.Set;

//inspired by puzzles custom gui code
public class EFConfigScreenWarnings extends EFScreen {
    final ObjectOpenHashSet<EFConfigWarning> warningsFound;

    public static EFConfigHandler<WarningConfig> warningConfigHandler = new EFConfigHandler<>(WarningConfig::new, "ef_warnings.json");

    public static class WarningConfig extends EFConfig.NoGUI {
        public Set<String> ignoredConfigIds = new HashSet<>();
    }

    public static Set<String> getIgnoredWarnings(){
        return warningConfigHandler.getConfig().ignoredConfigIds;
    }


    public EFConfigScreenWarnings(Screen parent, ObjectOpenHashSet<EFConfigWarning> warningsFound) {
        super("config." + ETFClientCommon.MOD_ID + ".warnings.title", parent, true);
        this.warningsFound = warningsFound;

    }

    @Override
    public void close() {
        warningConfigHandler.saveToFile();
        super.close();
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".ignore_all"),
                (button) -> {
                    //temporaryETFConfig = new ETFConfig();
                    for (EFConfigWarning warn :
                            EFConfigWarnings.getRegisteredWarnings()) {
                        getIgnoredWarnings().add(warn.getID());
                    }
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());

        double offset = 0.0;

        for (EFConfigWarning warning :
                warningsFound) {
            if (warning.doesShowDisableButton()) {
                ButtonWidget butt = ButtonWidget.builder(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
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
                        }).dimensions((int) (this.width * 0.75), (int) (this.height * (0.25 + offset)), (int) (this.width * 0.17), 20)
                                .tooltip(Tooltip.of(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.ignore_description"))).build();


                this.addDrawableChild(butt);
            }

            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warn_instruction"), (int) (width * 0.5), (int) (height * 0.18), 0xFFFFFF);
        double offset = 0.0;

        for (EFConfigWarning warning :
                warningsFound) {
            context.drawTextWithShadow(textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.getTitle()), (int) (this.width * 0.05), (int) (this.height * (0.25 + offset)), 0xFFFFFF);
            context.drawTextWithShadow(textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation(warning.getSubTitle()), (int) (this.width * 0.05), (int) (this.height * (0.29 + offset)), 0x888888);
            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }


    }


}
