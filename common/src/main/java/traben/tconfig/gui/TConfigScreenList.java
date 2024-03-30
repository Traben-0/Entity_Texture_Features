package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigScreenList extends TConfigScreen {

    private final TConfigEntry[] options;
    private final Align align;
    private Renderable renderFeature = null;

    public TConfigScreenList(@Translatable final String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges, Align align) {
        super(title, parent, true);
        this.options = options;
        this.parent = parent;
        this.resetDefaultValuesRunnable = resetValuesToDefault;
        this.undoChangesRunnable = undoChanges;
        this.align = align;
    }


    @SuppressWarnings("unused")
    public TConfigScreenList(@Translatable final String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges) {
        this(title, parent, options, resetValuesToDefault, undoChanges, Align.CENTER);
    }

    public void setRenderFeature(final Renderable renderFeature) {
        this.renderFeature = renderFeature;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (renderFeature != null) {
            renderFeature.render(context, mouseX, mouseY);
        }
    }

    @Override
    protected void init() {
        super.init();

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
        this.addDrawableChild(
                new TConfigEntryListWidget(
                        width,
                        (int) (this.height * 0.7),
                        (int) (this.height * 0.15),
                        x,
                        24,
                        options)
        );
    }

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    public interface Renderable {
        void render(DrawContext context, int mouseX, int mouseY);
    }
}
