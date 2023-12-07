package traben.entity_texture_features.neoforge;

import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@SuppressWarnings("SameReturnValue")
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

    public static Text getTextFromTranslation(String translationKey) {
        //1.19.84 version
        return Text.translatable(translationKey);
    }

    @Nullable
    public static String getBiomeString(World world, BlockPos pos) {
        if(world == null || pos == null) return null;
        //1.19 & 1.18.2 variation
        return world.getBiome(pos).getKey().toString().split(" / ")[1].replaceAll("[^\\da-zA-Z_:-]", "");
    }



}
