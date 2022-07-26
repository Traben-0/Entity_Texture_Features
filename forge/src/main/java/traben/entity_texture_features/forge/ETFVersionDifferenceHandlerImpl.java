package traben.entity_texture_features.forge;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import traben.entity_texture_features.forge.config.ETFConfigScreenForge;

import java.io.File;
import java.util.Objects;

public class ETFVersionDifferenceHandlerImpl {
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

    public static Logger getLogger() {
        //1.19 & 1.18.2 variation
        return LogManager.getLogger("Entity Texture Features");
    }

    public static Text getTextFromTranslation(String translationKey) {
        //1.18.2 version
        return new TranslatableText(translationKey);
    }

    public static String getBiomeString(World world, BlockPos pos) {
        //1.17 and before version
        return Objects.requireNonNull(world.getRegistryManager().get(Registry.BIOME_KEY).getId(world.getBiome(pos))).toString();
    }
}
