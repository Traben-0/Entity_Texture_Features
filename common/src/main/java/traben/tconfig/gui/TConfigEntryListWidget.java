package traben.tconfig.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.jetbrains.annotations.Nullable;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigEntryListWidget extends EntryListWidget<TConfigEntryListWidget.TConfigEntryForList> {
    public TConfigEntryListWidget(final int width, final int height, final int y, final int x, final int itemHeight,
                                  TConfigEntry... entries) {
        super(MinecraftClient.getInstance(), width, height, y, itemHeight);
        for (TConfigEntry option : entries) {
            if (option == null || option.getWidget(0, 0, 0, 0) == null) continue;
            addEntry(option);
        }
//        setRenderBackground(false);

        setX(x);

    }

    @Override
    public int getRowWidth() {
        return Math.min(width - 14, super.getRowWidth());
    }

    @Override
    protected int getScrollbarX() {
        return getX() == 0 ? super.getScrollbarX() : getX() + getRowWidth() + 4;
    }



    @Override
    protected void appendClickableNarrations(final NarrationMessageBuilder builder) {
    }

    @Override
    protected boolean isSelectButton(final int button) {
        return true;
    }


    @Override
    public void setSelected(@Nullable final TConfigEntryListWidget.TConfigEntryForList entry) {

    }

    protected boolean fullWidthBackgroundEvenIfSmaller = false;

    public void setWidgetBackgroundToFullWidth() {
        this.fullWidthBackgroundEvenIfSmaller = true;
    }

    @Override
    protected void drawMenuListBackground(final DrawContext context) {
        if(fullWidthBackgroundEvenIfSmaller){
            int x = getX();
            int width = getWidth();
            setX(0);
            assert MinecraftClient.getInstance().currentScreen != null;
            setWidth(MinecraftClient.getInstance().currentScreen.width);
            super.drawMenuListBackground(context);
            setX(x);
            setWidth(width);
        } else {
            super.drawMenuListBackground(context);
        }
    }

    @Override
    protected void drawHeaderAndFooterSeparators(final DrawContext context) {
        if (fullWidthBackgroundEvenIfSmaller) {
            int x = getX();
            int width = getWidth();
            setX(0);
            assert MinecraftClient.getInstance().currentScreen != null;
            setWidth(MinecraftClient.getInstance().currentScreen.width);
            super.drawHeaderAndFooterSeparators(context);
            setX(x);
            setWidth(width);
        } else {
            super.drawHeaderAndFooterSeparators(context);
        }
    }

    public abstract static class TConfigEntryForList extends Entry<TConfigEntryForList> {


        protected @Nullable ClickableWidget lastWidgetRendered = null;

        @Override
        public void render(final DrawContext context, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {
            lastWidgetRendered = getWidget(x, y, entryWidth, entryHeight);
            if (lastWidgetRendered != null) lastWidgetRendered.render(context, mouseX, mouseY, tickDelta);

        }

        public abstract ClickableWidget getWidget(int x, int y, int width, int height);


        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            if (lastWidgetRendered == null || !lastWidgetRendered.isMouseOver(mouseX, mouseY)) return false;
            return lastWidgetRendered.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
            if (lastWidgetRendered == null || !lastWidgetRendered.isMouseOver(mouseX, mouseY)) return false;
            return lastWidgetRendered.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public void setFocused(final boolean focused) {
            if (lastWidgetRendered == null) return;
            lastWidgetRendered.setFocused(focused);
        }

        @Override
        public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
            if (lastWidgetRendered == null || !lastWidgetRendered.isMouseOver(mouseX, mouseY)) return false;
            return lastWidgetRendered.mouseReleased(mouseX, mouseY, button);
        }
    }
}
