package traben.entity_features.config;

import net.minecraft.util.Identifier;
import traben.entity_features.config.gui.builders.EFOptionCategory;

public abstract class EFConfig {

    public abstract EFOptionCategory getGUIOptions();

    public abstract Identifier getModIcon();






    public static class NoGUI extends EFConfig {
        @Override
        public EFOptionCategory getGUIOptions() {
            return null;
        }

        @Override
        public Identifier getModIcon() {
            return null;
        }
    }

}
