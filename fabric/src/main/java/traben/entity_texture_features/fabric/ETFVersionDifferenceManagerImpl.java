package traben.entity_texture_features.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.List;

public class ETFVersionDifferenceManagerImpl {


    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }


    public static boolean isThisModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }



    public static boolean isForge() {
        return false;
    }

    public static List<String> modsLoaded() {
        return FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getId()).toList();
    }



}
