package traben.entity_texture_features;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.screen.Screen;

import java.io.File;

public class ETFCrossPlatformHandler {

    @ExpectPlatform
    public static Screen getConfigScreen(Screen parent, boolean isTransparent) {
        return null;
    }

    @ExpectPlatform
    public static boolean isThisModLoaded(String modId) {
        return false;
    }

    @ExpectPlatform
    public static File getConfigDir() {
        return null;
    }

    @ExpectPlatform
    public static boolean isForge() {
        return false;
    }

    @ExpectPlatform
    public static boolean isFabric() {
        return false;
    }

    @ExpectPlatform
    public static boolean areShadersInUse() {
        return false;
    }


}
