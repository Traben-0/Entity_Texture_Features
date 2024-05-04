package traben.entity_texture_features.config;

import com.demonwav.mcdev.annotations.Translatable;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.screens.ETFConfigScreenWarnings;

import java.util.function.Supplier;


/**
 * An object pertaining to an ETF config screen warning used in {@link ETFConfigScreenWarnings}
 * <p>
 * In summary, it holds:
 * <p>- a test to see if the warning should display
 * <p>- the message of the warning
 * <p>- a Runnable fix that can be applied to ETF to allow compatibility
 * <p>- functionality to disable/hide the warning/fix
 */
public abstract class ETFConfigWarning {

    /**
     * Condition that must be met to display this warning.
     */
    public abstract boolean isConditionMet();


    /**
     * The warning title text, treated as a translation key.
     */
    public abstract String getTitle();

    /**
     * The warning sub-title text, treated as a translation key.
     */
    public abstract String getSubTitle();

    /**
     * The ID of this warning.
     */
    public abstract String getID();

    public abstract void testWarningAndApplyFixIfEnabled();

    protected boolean isEnabled() {
        return isConditionMet() && !ETFConfigScreenWarnings.getIgnoredWarnings().contains(getID());
    }

    public abstract boolean doesShowDisableButton();


    @SuppressWarnings("SameParameterValue")
    public static class Simple extends ETFConfigWarning {
        final public String TITLE_TRANSLATION_KEY;
        final public String SUB_TITLE_TRANSLATION_KEY;
        final public String ID;
        final private Supplier<Boolean> CONDITION;

        @Nullable
        final private Runnable FIX;

        /**
         * Instantiates a new Etf config warning.
         *
         * @param id                        The ID of this warning.
         * @param condition                 Condition that must be met to display this warning.
         * @param title_translation_key     The warning title text.
         * @param sub_title_translation_key The warning sub-title text.
         * @param fix                       Runnable to apply changes to ETF to fix compatibility
         */
        public Simple(String id, Supplier<Boolean> condition, @Translatable String title_translation_key, @Translatable String sub_title_translation_key, @Nullable Runnable fix) {
            this.ID = id;
            this.CONDITION = condition;
            this.TITLE_TRANSLATION_KEY = title_translation_key;
            this.SUB_TITLE_TRANSLATION_KEY = sub_title_translation_key;
            this.FIX = fix;
        }

        /**
         * Instantiates a new Etf config warning.
         *
         * @param id                        The ID of this warning.
         * @param modName                   the mod name that must be present to trigger the warning.
         * @param title_translation_key     The warning title text.
         * @param sub_title_translation_key The warning sub-title text.
         * @param fix                       Runnable to apply changes to ETF to fix compatibility
         */
        public Simple(String id, String modName, @Translatable String title_translation_key, @Translatable String sub_title_translation_key, @Nullable Runnable fix) {
            this.ID = id;
            this.CONDITION = () -> ETF.isThisModLoaded(modName);
            this.TITLE_TRANSLATION_KEY = title_translation_key;
            this.SUB_TITLE_TRANSLATION_KEY = sub_title_translation_key;
            this.FIX = fix;
        }

        public boolean isConditionMet() {
            return CONDITION.get();
        }


        @Override
        public String getTitle() {
            return TITLE_TRANSLATION_KEY;
        }

        @Override
        public String getSubTitle() {
            return SUB_TITLE_TRANSLATION_KEY;
        }

        @Override
        public String getID() {
            return ID;
        }

        @Override
        public void testWarningAndApplyFixIfEnabled() {
            if (FIX != null && isEnabled()) FIX.run();
        }

        @Override
        public boolean doesShowDisableButton() {
            return FIX != null;
        }

    }
}

