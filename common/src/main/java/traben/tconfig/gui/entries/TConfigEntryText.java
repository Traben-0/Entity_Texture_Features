package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TConfigEntryText extends TConfigEntry {

    protected final ClickableWidget widget;

    public TConfigEntryText(@Translatable final String text, TextAlignment alignment) {
        super(text, null);
        widget = new ETFTextWidget(getText(),alignment);
//        alignment.align(widget);
    }

    public TConfigEntryText(@Translatable final String text) {
        this(text, TextAlignment.CENTER);
    }

    @SuppressWarnings("unused")
    public static Collection<TConfigEntry> fromLongOrMultilineTranslation(@Translatable String translationKey, int width) {
        return fromLongOrMultilineTranslation(translationKey, width, TextAlignment.CENTER);
    }

    public static List<TConfigEntry> fromLongOrMultilineTranslation(@Translatable String translationKey, int width, TextAlignment alignment) {

        var translated = ETFVersionDifferenceHandler.getTextFromTranslation(translationKey);
        var lines = MinecraftClient.getInstance().textRenderer.getTextHandler().wrapLines(translated, width, Style.EMPTY);
        List<TConfigEntry> list = new ArrayList<>();
        String lastLine = null;
        for (StringVisitable line : lines) {
            if (lastLine != null) {
                list.add(new TwoLines(lastLine, line.getString(), alignment));
                lastLine = null;
            } else {
                lastLine = line.getString();
            }

        }
        if (lastLine != null) {
            //noinspection NoTranslation
            list.add(new TwoLines(lastLine, "", alignment));
        }
        return list;
    }

    @Override
    public ClickableWidget getWidget(final int x, final int y, final int width, final int height) {
//        widget.setDimensionsAndPosition(width, height, x, y);
        widget.x=(x);
        widget.y=(y);
        widget.height = (height);
        widget.setWidth(width);
        return widget;
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

    public enum TextAlignment {
        LEFT, CENTER, RIGHT;

//        private void align(TextWidget widget) {
//            switch (this) {
//                case LEFT:
//                    widget.alignLeft();
//                    break;
//                case CENTER:
//                    widget.alignCenter();
//                    break;
//                case RIGHT:
//                    widget.alignRight();
//                    break;
//            }
//        }
    }

    public static class TwoLines extends TConfigEntryText {

        protected final ETFTextWidget widget2;

        @SuppressWarnings("unused")
        public TwoLines(@Translatable final String text1, @Translatable final String text2) {
            this(text1, text2, TextAlignment.CENTER);
        }

        public TwoLines(@Translatable final String text1, @Translatable final String text2, TextAlignment alignment) {
            super(text1, alignment);
            widget2 = new ETFTextWidget(ETFVersionDifferenceHandler.getTextFromTranslation(text2), alignment);
//            alignment.align(widget2);
            if (!widget2.getMessage().getString().contains("§"))
                widget2.setColor(0xCCCCCC);//off-white for better visual separation
        }

        @Override
        public ClickableWidget getWidget(final int x, final int y, final int width, final int height) {
           // widget.setDimensionsAndPosition(width, height / 2, x, y);
            widget.x=(x);
            widget.y=(y);
            widget.height = (height / 2);
            widget.setWidth(width);
            //widget2.setDimensionsAndPosition(width, height / 2, x, y + height / 2 + 2);
            widget2.x=(x);
            widget2.y=(y + height / 2 + 2);
            widget2.height = (height / 2);
            widget2.setWidth(width);
            return widget;
        }

        @Override
        public void render(final MatrixStack context, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {
            lastWidgetRendered = getWidget(x, y, entryWidth, entryHeight);
            widget.render(context, mouseX, mouseY, tickDelta);
            widget2.render(context, mouseX, mouseY, tickDelta);
        }
    }

    private class ETFTextWidget extends ClickableWidget {
        private final TextAlignment alignment;

        public void setColor(final int color) {
            this.color = color;
        }

        private int color = 0xFFFFFF;

        public ETFTextWidget(Text text, final TextAlignment alignment) {
            super(0, 0, 0, 0, text);
            this.alignment = alignment;
        }

        @Override
        public void appendNarrations(final NarrationMessageBuilder builder) {

        }

        @Override
        public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
            switch (alignment){
                case LEFT:
                    MinecraftClient.getInstance().textRenderer.draw(matrices, this.getMessage(), x, y, color);
                    break;
                case CENTER:
                    MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, this.getMessage(), x + (float) (width - MinecraftClient.getInstance().textRenderer.getWidth(getText())) / 2, y, color);
                    break;
                case RIGHT:
                    MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, this.getMessage(), x + width - MinecraftClient.getInstance().textRenderer.getWidth(getText()), y, color);
                    break;

            }
        }
    }
}
