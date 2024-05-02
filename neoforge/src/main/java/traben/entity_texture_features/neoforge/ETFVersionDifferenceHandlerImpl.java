package traben.entity_texture_features.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@SuppressWarnings({"SameReturnValue", "unused"})
public class ETFVersionDifferenceHandlerImpl {


    public static boolean isThisModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static File getConfigDir() {
        return FMLPaths.GAMEDIR.get().resolve(FMLPaths.CONFIGDIR.get()).toFile();
    }

    public static boolean isForge() {
        return true;
    }

    public static boolean isFabric() {
        return false;
    }



    public static Logger getLogger() {
        //1.19 & 1.18.2 variation
        return LoggerFactory.getLogger("Entity Texture Features");
    }

    public static Component getTextFromTranslation(String translationKey) {
        //1.19.84 version
        return Component.translatable(translationKey);
    }

    @Nullable
    public static String getBiomeString(Level world, BlockPos pos) {
        if(world == null || pos == null) return null;
        //1.19 & 1.18.2 variation
        return world.getBiome(pos).unwrapKey().toString().split(" / ")[1].replaceAll("[^\\da-zA-Z_:-]", "");
    }

    public static List<String> modsLoaded() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).toList();
    }

}
