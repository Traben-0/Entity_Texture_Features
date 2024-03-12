package traben.tconfig.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.jetbrains.annotations.Nullable;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigEntryListWidget extends EntryListWidget<TConfigEntryListWidget.TConfigEntryForList> {
    public TConfigEntryListWidget(final int width, final int height, final int y, final int itemHeight,
                                  TConfigEntry... entries) {
        super(MinecraftClient.getInstance(), width, height, y, itemHeight);
        for (TConfigEntry option : entries) {
            if (option == null || option.getWidget(0,0,0,0) == null) continue;
            addEntry(option);
        }
        setRenderBackground(false);
    }

    @Override
    protected void appendClickableNarrations(final NarrationMessageBuilder builder) {}

    @Override
    protected boolean isSelectButton(final int button) {
        return true;
    }

    @Override
    public void setSelected(@Nullable final TConfigEntryListWidget.TConfigEntryForList entry) {

    }

    public abstract static class TConfigEntryForList extends Entry<TConfigEntryForList>{

        private boolean wasActuallyHovered = false;
        @Override
        public void render(final DrawContext context, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY,final boolean hovered, final float tickDelta) {
            var widget = getWidget(x, y, entryWidth, entryHeight);
            if (widget != null) {
                widget.render(context, mouseX, mouseY, tickDelta);
                this.wasActuallyHovered = widget.isMouseOver(mouseX, mouseY);
            }
        }

        public abstract <T extends Element & Drawable & Selectable> T getWidget(int x, int y, int width, int height);



        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            Element widget = getWidget(0, 0, 0, 0);
            if (widget == null || !wasActuallyHovered) return false;
            return widget.mouseClicked(mouseX, mouseY, button);
        }
    }
}
