package traben.entity_texture_features.config;


import net.minecraft.client.gui.screen.Screen;
import traben.entity_texture_features.ETFCrossPlatformHandler;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

// config translation rework done by @Maximum#8760
public class ETFConfigScreen {

    public static Screen getConfigScreen(Screen parent, boolean isTransparent) {
        return ETFCrossPlatformHandler.getConfigScreen( parent,  isTransparent);
    }

    //this needs to be here due to puzzle mod compatibility
    public void saveConfig() {
        ETFUtils2.saveConfig();
    }

    //same as above
    public void resetVisuals() {
        ETFManager.reset();
    }
}
