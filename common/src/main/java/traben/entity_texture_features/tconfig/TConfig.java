package traben.entity_texture_features.tconfig;

import net.minecraft.resources.ResourceLocation;
import traben.entity_texture_features.tconfig.gui.entries.TConfigEntryCategory;

public abstract class TConfig {

    public abstract TConfigEntryCategory getGUIOptions();

    public abstract ResourceLocation getModIcon();

    public boolean doesGUI() {
        return true;
    }


    public static class NoGUI extends TConfig {
        @Override
        public TConfigEntryCategory getGUIOptions() {
            return null;
        }

        @Override
        public ResourceLocation getModIcon() {
            return null;
        }

        @Override
        public boolean doesGUI() {
            return false;
        }
    }

}
