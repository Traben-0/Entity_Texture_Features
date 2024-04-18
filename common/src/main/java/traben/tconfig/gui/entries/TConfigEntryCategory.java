package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.tconfig.gui.TConfigScreenList;

import java.util.Collection;

public class TConfigEntryCategory extends TConfigEntry {

    private final Object2ObjectLinkedOpenHashMap<String, TConfigEntry> options = new Object2ObjectLinkedOpenHashMap<>();
    private final String translationKey;
    private TConfigScreenList screen = null;
    private Text emptyTooltip = ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_features.empty");
    private TConfigScreenList.Align align = TConfigScreenList.Align.CENTER;
    private TConfigScreenList.Renderable renderFeature = null;

    public TConfigEntryCategory(@Translatable final String text, @Translatable final String tooltip) {
        super(text, tooltip);
        translationKey = text;
    }

    public TConfigEntryCategory(@Translatable final String text) {
        super(text, null);
        translationKey = text;
    }

    @SuppressWarnings("unused")
    public void setAlign(final TConfigScreenList.Align align) {
        this.align = align;
    }

    public Object2ObjectLinkedOpenHashMap<String, TConfigEntry> getOptions() {
        return options;
    }

    //don't need to init screen each time
    public TConfigScreenList getScreen() {
        if (screen == null) {
            screen = new TConfigScreenList(translationKey, MinecraftClient.getInstance().currentScreen, options.values().toArray(new TConfigEntry[0]), this::setValuesToDefault, this::resetValuesToInitial, align);
            screen.setRenderFeature(renderFeature);
        }
        return screen;
    }

    @Override
    public ClickableWidget getWidget(final int x, final int y, final int width, final int height) {
        return new CategoryButton(x, y, width, height, getText(),
                (button) -> MinecraftClient.getInstance().setScreen(getScreen()));
    }

    @Override
    public boolean saveValuesToConfig() {
        boolean found = false;
        for (TConfigEntry option : options.values()) {
            found |= option.saveValuesToConfig();
        }
        return found;
    }

    @Override
    public void setValuesToDefault() {
        for (TConfigEntry option : options.values()) {
            option.setValuesToDefault();
        }
    }

    @Override
    public void resetValuesToInitial() {
        for (TConfigEntry option : options.values()) {
            option.resetValuesToInitial();
        }
    }

    public TConfigEntryCategory add(final TConfigEntry... option) {
        for (TConfigEntry tConfigEntry : option) {
            add(tConfigEntry);
        }
        return this;
    }

    public TConfigEntryCategory addAll(final Collection<TConfigEntry> option) {
        if (option == null) return this;
        for (TConfigEntry tConfigEntry : option) {
            add(tConfigEntry);
        }
        return this;
    }

    public TConfigEntryCategory add(final TConfigEntry option) {
        if (option == null) {
            return this;
        }
        if (option instanceof TConfigEntryCategory category) {
            return addOrMerge(category);
        }
        options.put(option.getText().getString(), option);
        return this;
    }

    private TConfigEntryCategory addOrMerge(final TConfigEntryCategory category) {
        if (options.containsKey(category.getText().getString())
                && options.get(category.getText().getString()) instanceof TConfigEntryCategory existingCategory) {
            for (TConfigEntry option : category.options.values()) {
                existingCategory.add(option);
            }
        } else {
            options.put(category.getText().getString(), category);
        }
        return this;
    }

    @Override
    boolean hasChangedFromInitial() {
        boolean changed = false;
        for (TConfigEntry value : options.values()) {
            if (value.hasChangedFromInitial()) {
                changed = true;
                break;
            }
        }
        return changed;
    }

    public TConfigEntryCategory setEmptyTooltip(@NotNull @Translatable final String emptyTooltipKey) {
        this.emptyTooltip = ETFVersionDifferenceHandler.getTextFromTranslation(emptyTooltipKey);
        return this;
    }

    @SuppressWarnings("unused")
    public void setRenderFeature(final TConfigScreenList.Renderable renderFeature) {
        this.renderFeature = renderFeature;
    }

    public static class Empty extends TConfigEntryCategory {
        public Empty() {
            //noinspection NoTranslation
            super("", null);
        }

        @Override
        public ClickableWidget getWidget(int x, int y, int width, int height) {
            return null;
        }


    }


    private class CategoryButton extends ButtonWidget {

        protected CategoryButton(final int x, final int y, final int width, final int height, final Text message, final PressAction onPress) {
            super(x, y, width, height, message, onPress,  options.isEmpty() ? getTooltip(emptyTooltip) : getTooltip());
            active = !options.isEmpty();
//            if (!active) {
////                setTooltip(emptyTooltip);
//            }
        }


        @Override
        public Text getMessage() {
            return hasChangedFromInitial() ? Text.of(CHANGED_COLOR + super.getMessage().getString()) : super.getMessage();
        }
    }

}
