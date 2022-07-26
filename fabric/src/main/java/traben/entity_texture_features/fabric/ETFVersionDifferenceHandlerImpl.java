package traben.entity_texture_features.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.fabric.config.ETFConfigScreenFabric;

import java.io.File;

public class ETFVersionDifferenceHandlerImpl {
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

    public static Logger getLogger() {
        //1.19 & 1.18.2 variation
        return LoggerFactory.getLogger("Entity Texture Features");
    }

    public static Text getTextFromTranslation(String translationKey) {
        //1.18.2 version
        return new TranslatableText(translationKey);
    }

    @NotNull
    public static String getBiomeString(World world, BlockPos pos) {
        //1.19 & 1.18.2 variation
        return world.getBiome(pos).getKey().toString().split("\s/\s")[1].replaceAll("[^\\da-zA-Z_:-]", "");
    }
}
