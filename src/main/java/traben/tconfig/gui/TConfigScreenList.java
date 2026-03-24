package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;
import traben.tconfig.gui.entries.TConfigEntry;

import java.util.ArrayList;
import java.util.Arrays;

public class TConfigScreenList extends TConfigScreen {

    private final TConfigEntry[] options;
    private final Align align;
    protected boolean fullWidthBackgroundEvenIfSmaller = false;
    private Renderable renderFeature = null;
    private TConfigEntryListWidget list;
    private final EditBox search;


    public TConfigScreenList(@Translatable final String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges, Align align) {
        super(title, parent, true);
        this.options = options;
        this.parent = parent;
        this.resetDefaultValuesRunnable = resetValuesToDefault;
        this.undoChangesRunnable = undoChanges;
        this.align = align;
        if (options.length > 12) {
            search = new EditBox(Minecraft.getInstance().font, 4, 0, 120, 20, Component.literal(""));
            search.setResponder(this::updateSearch);
            search.setHint(ETF.getTextFromTranslation("config.entity_features.search"));
        } else {
            search = null;
        }
    }

    @SuppressWarnings("unused")
    public TConfigScreenList(@Translatable final String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges) {
        this(title, parent, options, resetValuesToDefault, undoChanges, Align.CENTER);
    }

    public void setRenderFeature(final Renderable renderFeature) {
        this.renderFeature = renderFeature;
    }

    @Override
    public void
        //#if MC >= 26.1
        //$$ extractRenderState
        //#else
        render
        //#endif
    (final GuiGraphics context, final int mouseX, final int mouseY, final float delta) {
        super.
                //#if MC >= 26.1
                //$$ extractRenderState
                //#else
                render
                //#endif
                        (context, mouseX, mouseY, delta);
        if (renderFeature != null) {
            renderFeature.render(context, mouseX, mouseY);
        }
    }

    public void setWidgetBackgroundToFullWidth() {
        this.fullWidthBackgroundEvenIfSmaller = true;
    }

    @Override
    protected void init() {
        super.init();

        if (search != null) {
            int y = (int) (this.height * 0.15) - 24;
            int width = (int) (this.width * 0.2);
            search.setY(y);
            search.setWidth(width);
            this.addRenderableWidget(search);
        }

        initList(options);
    }

    private void updateSearch(String searchText) {
        assert search != null;

        this.removeWidget(list);

        var list = new ArrayList<TConfigEntry>();
        if (searchText.isBlank()) {
            initList(options);
        } else {
            Arrays.stream(options)
                    .filter((it)-> it.getText().getString().contains(searchText))
                    .forEach(list::add);
            initList(list.toArray(new TConfigEntry[0]));
        }
    }

    private void initList(TConfigEntry[] subList) {
        int width;
        int x;
        switch (align) {
            case LEFT -> {
                width = (int) (this.width * 0.3);
                x = (int) (this.width * 0.1);
            }
            case RIGHT -> {
                width = (int) (this.width * 0.3);
                x = (int) (this.width * 0.6);
            }
            default -> {
                width = this.width;
                x = 0;
            }
        }

        list = this.addRenderableWidget(new TConfigEntryListWidget(
                        width,
                        (int) (this.height * 0.7),
                        (int) (this.height * 0.15),
                        x,
                        24,
                        subList)
        );
        //#if MC >= 12006
        if (fullWidthBackgroundEvenIfSmaller) {
            list.setWidgetBackgroundToFullWidth();
        }
        //#endif

        //#if MC >= 12109
        list.setScrollAmount(0); // actually used to trigger repositionEntries() call to setup entry positions
        //#endif
    }

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    public interface Renderable {
        void render(GuiGraphics context, int mouseX, int mouseY);
    }
}
