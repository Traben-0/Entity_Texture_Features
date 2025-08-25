package traben.entity_texture_features.config.screens;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.tconfig.TConfig;
import traben.tconfig.gui.TConfigScreen;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

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
    public void onClose() {
        ETF.warningConfigHandler.saveToFile();
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(ETF.getTextFromTranslation("config." + ETF.MOD_ID + ".ignore_all"),
                (button) -> {
                    //temporaryETFConfig = new ETFConfig();
                    for (ETFConfigWarning warn :
                            ETFConfigWarnings.getRegisteredWarnings()) {
                        getIgnoredWarnings().add(warn.getID());
                    }
                    this.rebuildWidgets();
                    //Objects.requireNonNull(client).setScreen(parent);
                }).bounds((int) (this.width * 0.25), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());

        double offset = 0.0;

        for (ETFConfigWarning warning :
                warningsFound) {
            if (warning.doesShowDisableButton()) {
                Button butt = Button.builder(Component.nullToEmpty(ETF.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
                                        (getIgnoredWarnings().contains(warning.getID()) ? CommonComponents.GUI_YES : CommonComponents.GUI_NO).getString()),
                                (button) -> {
                                    //button.active = false;
                                    if (getIgnoredWarnings().contains(warning.getID())) {
                                        getIgnoredWarnings().remove(warning.getID());
                                    } else {
                                        getIgnoredWarnings().add(warning.getID());
                                    }
                                    button.setMessage(Component.nullToEmpty(ETF.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() +
                                            (getIgnoredWarnings().contains(warning.getID()) ? CommonComponents.GUI_YES : CommonComponents.GUI_NO).getString()));
                                }).bounds((int) (this.width * 0.75), (int) (this.height * (0.25 + offset)), (int) (this.width * 0.17), 20)
                        .tooltip(Tooltip.create(ETF.getTextFromTranslation("config.entity_texture_features.ignore_description"))).build();


                this.addRenderableWidget(butt);
            }

            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredString(font, ETF.getTextFromTranslation("config." + ETF.MOD_ID + ".warn_instruction"), (int) (width * 0.5), (int) (height * 0.18), 0xFFFFFF);
        double offset = 0.0;

        for (ETFConfigWarning warning :
                warningsFound) {
            context.drawString(font, ETF.getTextFromTranslation(warning.getTitle()), (int) (this.width * 0.05), (int) (this.height * (0.25 + offset)), 0xFFFFFF);
            context.drawString(font, ETF.getTextFromTranslation(warning.getSubTitle()), (int) (this.width * 0.05), (int) (this.height * (0.29 + offset)), 0x888888);
            offset += 0.1;
            //todo offset method only good for about 6 warnings, return here if adding more than 7 in future
        }


    }

    @SuppressWarnings("CanBeFinal")
    public static class WarningConfig extends TConfig.NoGUI {
        public Set<String> ignoredConfigIds = new HashSet<>();
    }


}
