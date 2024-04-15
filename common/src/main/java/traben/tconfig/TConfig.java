package traben.tconfig;

import net.minecraft.util.Identifier;
import traben.tconfig.gui.entries.TConfigEntryCategory;

public abstract class TConfig {

    public abstract TConfigEntryCategory getGUIOptions();

    public abstract Identifier getModIcon();

    public boolean doesGUI() {
        return true;
    }


    public static class NoGUI extends TConfig {
        @Override
        public TConfigEntryCategory getGUIOptions() {
            return null;
        }

        @Override
        public Identifier getModIcon() {
            return null;
        }

        @Override
        public boolean doesGUI() {
            return false;
        }
    }

}
