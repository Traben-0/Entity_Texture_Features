package traben.tconfig.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import traben.tconfig.TConfig;
import traben.tconfig.TConfigHandler;
import traben.tconfig.gui.entries.TConfigEntry;
import traben.tconfig.gui.entries.TConfigEntryCategory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TConfigScreenMain extends TConfigScreen {
    protected final TConfigEntryCategory entries;
    protected final List<Identifier> modIcons;
    protected final Set<TConfigHandler<?>> configHandlers = new HashSet<>();

    public TConfigScreenMain(final String title, final Screen parent, Set<TConfigHandler<?>> inputHandlers, List<TConfigEntry> defaultEntries) {
        super(title, parent, true);
        entries = new TConfigEntryCategory.Empty().addAll(defaultEntries);


        modIcons = new ArrayList<>();

        for (TConfigHandler<?> configHandler : inputHandlers) {
            this.configHandlers.add(configHandler);
            if (configHandler.doesGUI()){
                TConfig config = configHandler.getConfig();
                for (TConfigEntry value : config.getGUIOptions().getOptions().values()) {
                    entries.add(value);
                }
                var icon = config.getModIcon();
                if (icon != null) {
                    modIcons.add(icon);
                }
            }
        }

        this.resetDefaultValuesRunnable = entries::setValuesToDefault;
        this.undoChangesRunnable = entries::resetValuesToInitial;
    }

    @Override
    public void close() {
        if (entries.saveValuesToConfig()) {
            for (TConfigHandler<?> configHandler : configHandlers) {
                configHandler.saveToFile();
            }
            MinecraftClient.getInstance().reloadResources();
        }
        super.close();
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(
                new TConfigEntryListWidget(
                        (int) (this.width * 0.3),
                        (int) (this.height * 0.7),
                        (int) (this.height * 0.15),
                        24,
                        entries.getOptions().values().toArray(new TConfigEntry[0])
        )).setX((int) (this.width * 0.6));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        //draw mod icons in the top right corner of the screen
        // from left to right
        if (!modIcons.isEmpty()) {
            int ix = this.width - (modIcons.size() * 34);
            for (Identifier modIcon : modIcons) {
                context.drawTexture(modIcon, ix, 2, 0, 0, 32, 32, 32, 32);
                ix += 34;
            }
        }
    }

}
