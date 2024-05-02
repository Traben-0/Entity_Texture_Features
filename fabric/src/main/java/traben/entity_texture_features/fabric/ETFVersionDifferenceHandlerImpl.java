package traben.entity_texture_features.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@SuppressWarnings("SameReturnValue")
public class ETFVersionDifferenceHandlerImpl {


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
        return FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getId()).toList();
    }


}
