package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class TConfigEntryValue<V> extends TConfigEntry {


    protected final Supplier<V> getter;
    protected final Consumer<V> setter;
    protected final V defaultValue;


    protected TConfigEntryValue(@Translatable String translationKey, @Translatable String tooltip, Supplier<V> getter, Consumer<V> setter, V defaultValue) {
        super(translationKey, tooltip);
        this.getter = getter;
        this.setter = setter;
        this.defaultValue = defaultValue;
    }

    @SuppressWarnings("unused")
    protected TConfigEntryValue(@Translatable String translationKey, Supplier<V> getter, Consumer<V> setter, V defaultValue) {
        super(translationKey, null);
        this.getter = getter;
        this.setter = setter;
        this.defaultValue = defaultValue;
    }

    public boolean saveValuesToConfig() {
        if (hasChangedFromInitial()) {
            setter.accept(getValueFromWidget());
            return true;
        } else {
            return false;
        }
    }

    protected abstract V getValueFromWidget();


    @Override
    void setValuesToDefault() {
        setWidgetToDefaultValue();
    }

    abstract void setWidgetToDefaultValue();

    @Override
    void resetValuesToInitial() {
        resetWidgetToInitialValue();
    }

    abstract void resetWidgetToInitialValue();

    @Override
    boolean hasChangedFromInitial() {
        return !getValueFromWidget().equals(getter.get());
    }
}
