package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.tconfig.gui.TConfigEntryListWidget;

import java.util.ArrayList;
import java.util.List;

public abstract class TConfigEntry extends TConfigEntryListWidget.TConfigEntryForList {

    public final static String CHANGED_COLOR = "§a";
    private final Text text;
    private final Text tooltip;

    public TConfigEntry(@Translatable String text, @Translatable String tooltip) {
        this.text = ETFVersionDifferenceHandler.getTextFromTranslation(text);
        this.tooltip = tooltip == null || tooltip.isBlank() ? null : ETFVersionDifferenceHandler.getTextFromTranslation(tooltip);
    }

    @SuppressWarnings("unused")
    public TConfigEntry(@Translatable String text) {
        this(text, null);
    }

    public Text getText() {
        return text;
    }

    public ButtonWidget.TooltipSupplier getTooltip() {
        return getTooltip(tooltip);
    }

    public static List<Text> getAsLines(Text toolTipText) {
        String[] strings = toolTipText.getString().split("\n");
        List<Text> lines = new ArrayList<>();
        for (String str :
                strings) {
            lines.add(Text.of(str.strip()));
        }
        return lines;
    }

    public static ButtonWidget.TooltipSupplier getTooltip(@Translatable String toolTipText) {
        if (toolTipText == null || toolTipText.isBlank()) return ButtonWidget.EMPTY;
        return getTooltip(ETFVersionDifferenceHandler.getTextFromTranslation(toolTipText));
    }
    public static ButtonWidget.TooltipSupplier getTooltip(Text toolTipText) {
        if (toolTipText == null || toolTipText.getString().isEmpty()) return ButtonWidget.EMPTY;
        var lines = getAsLines(toolTipText);
        return (buttonWidget, matrices, mouseX, mouseY) -> {
            if (buttonWidget.isHovered() && !toolTipText.getString().isEmpty()) {
                assert MinecraftClient.getInstance().currentScreen != null;
                MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
            }
        };
    }

    public interface ElementTooltipSupplier {
        void onTooltip(Element button, MatrixStack matrices, int mouseX, int mouseY);

    }
    public static @Nullable ElementTooltipSupplier getTooltipForElement(String toolTipText) {
        if (toolTipText == null || toolTipText.isBlank()) return null;
        var text = ETFVersionDifferenceHandler.getTextFromTranslation(toolTipText);
        var lines = getAsLines(text);
        return (element, matrices, mouseX, mouseY) -> {
            if (element.isMouseOver(mouseX,mouseY) && !text.getString().isEmpty()) {
                assert MinecraftClient.getInstance().currentScreen != null;
                MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
            }
        };
    }

    public TConfigEntry setEnabled(boolean enabled) {
        var widget = getWidget(0, 0, 0, 0);
        widget.active = enabled;
        return this;
    }

    abstract boolean hasChangedFromInitial();

    abstract boolean saveValuesToConfig();

    abstract void setValuesToDefault();

    abstract void resetValuesToInitial();

    public static class Empty extends TConfigEntry {

        @SuppressWarnings("unused")
        public Empty() {
            //noinspection NoTranslation
            super("", null);
        }

        @Override
        public ClickableWidget getWidget(int x, int y, int width, int height) {
            return null;
        }


        @Override
        boolean hasChangedFromInitial() {
            return false;
        }

        @Override
        boolean saveValuesToConfig() {
            return false;
        }

        @Override
        void setValuesToDefault() {

        }

        @Override
        void resetValuesToInitial() {

        }


    }

}
