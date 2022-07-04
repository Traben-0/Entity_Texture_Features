package traben.entity_texture_features.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import traben.entity_texture_features.fabric.config.ETFConfigScreenFabric;

import java.io.File;

public class ETFCrossPlatformHandlerImpl {
    public static Screen getConfigScreen(Screen parent, boolean isTransparent) {
        return ETFConfigScreenFabric.getConfigScreen(parent,isTransparent);
    }

    public static boolean isThisModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static File getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    public static boolean isForge() {
        return false;
    }

    public static boolean isFabric() {
        return true;
    }

    public static boolean areShadersInUse() {
        return IrisCompat.isShaderPackInUse();
    }
}
