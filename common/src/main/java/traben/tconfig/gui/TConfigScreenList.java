package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.screen.Screen;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigScreenList extends TConfigScreen {

    private final TConfigEntry[] options;


    public TConfigScreenList(@Translatable final String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges) {
        super(title, parent, true);
        this.options = options;
        this.parent = parent;
        this.resetDefaultValuesRunnable = resetValuesToDefault;
        this.undoChangesRunnable = undoChanges;
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(
                new TConfigEntryListWidget(
                        this.width,
                        (int) (this.height * 0.7),
                        (int) (this.height * 0.15),
                        0,
                        24,
                        options)
        );
    }

}
