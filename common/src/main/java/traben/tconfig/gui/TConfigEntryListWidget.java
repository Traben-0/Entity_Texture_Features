package traben.tconfig.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigEntryListWidget extends EntryListWidget<TConfigEntryListWidget.TConfigEntryForList> {
    public TConfigEntryListWidget(final int width, final int height, final int y, final int x, final int itemHeight,
                                  TConfigEntry... entries) {
        super(MinecraftClient.getInstance(), width, height, y, itemHeight);
        for (TConfigEntry option : entries) {
            if (option == null || option.getWidget(0, 0, 0, 0) == null) continue;
            addEntry(option);
        }
        setRenderBackground(false);
        setX(x);

    }

    @Override
    public int getRowWidth() {
        return Math.min(width - 14, super.getRowWidth());
    }

    @Override
    protected int getScrollbarPositionX() {
        return getX() == 0 ? super.getScrollbarPositionX() : getX() + getRowWidth() + 4;
    }


    @Override
    protected void appendClickableNarrations(final NarrationMessageBuilder builder) {
    }

    @Override
    protected boolean isSelectButton(final int button) {
        return true;
    }






//    @Override
//    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
//        System.out.println("drag1");
//        var hovered = getHoveredEntry();
//        if (hovered != null) {
//            return hovered.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
//        }
//        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
//    }

    public abstract static class TConfigEntryForList extends Entry<TConfigEntryForList> {

        private int lastx = 0;
        private int lasty = 0;
        private int lastwidth = 0;
        private int lastheight = 0;

        @Override
        public void render(final DrawContext context, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {
            Drawable widget = getWidget(x, y, entryWidth, entryHeight);
            if (widget != null) widget.render(context, mouseX, mouseY, tickDelta);
            lastx = x;
            lasty = y;
            lastwidth = entryWidth;
            lastheight = entryHeight;
        }

        public abstract <T extends Element & Drawable & Selectable> T getWidget(int x, int y, int width, int height);


        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            var widget = getWidget(lastx, lasty, lastwidth, lastheight);
            if (widget == null || !widget.isMouseOver(mouseX, mouseY)) return false;
            return widget.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
            var widget = getWidget(lastx, lasty, lastwidth, lastheight);
            if (widget == null || !widget.isMouseOver(mouseX, mouseY)) return false;
            return widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public void setFocused(final boolean focused) {
            var widget = getWidget(lastx, lasty, lastwidth, lastheight);
            if (widget == null) return;
            widget.setFocused(focused);
        }

        @Override
        public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
            var widget = getWidget(lastx, lasty, lastwidth, lastheight);
            if (widget == null || !widget.isMouseOver(mouseX, mouseY)) return false;
            return widget.mouseReleased(mouseX, mouseY, button);
        }
    }
}
