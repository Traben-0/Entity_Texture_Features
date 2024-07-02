package traben.entity_texture_features.neoforge;



import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.List;

public class ETFVersionDifferenceManagerImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.GAMEDIR.get().resolve(FMLPaths.CONFIGDIR.get());
    }

    public static boolean isThisModLoaded(final String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isForge() {
        return true;
    }

    public static List<String> modsLoaded() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).toList();
    }
}
