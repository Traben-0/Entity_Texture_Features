package traben.entity_texture_features.forge;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import traben.entity_texture_features.forge.config.ETFConfigScreenForge;

import java.io.File;

public class ETFCrossPlatformHandlerImpl {
    public static Screen getConfigScreen(Screen parent, boolean isTransparent) {
        return ETFConfigScreenForge.getConfigScreen(parent,isTransparent);
    }

    public static boolean isThisModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static File getConfigDir() {
        return FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).toFile();
    }

    public static boolean isForge() {
        return true;
    }

    public static boolean isFabric() {
        return false;
    }

    public static boolean areShadersInUse() {
        //todo follow up
        return false;
    }
}
