package traben.entity_texture_features.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;

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
