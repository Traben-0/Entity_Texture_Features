package traben.entity_texture_features.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import traben.entity_texture_features.ETF;

import java.io.File;
import java.util.List;

@Mod("entity_texture_features")
public class ETFClientForge {



    public ETFClientForge() {

        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            try {
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory(ETF::getConfigScreen));
            } catch (NoClassDefFoundError e) {
                System.out.println("[Entity Texture Features]: Mod config broken, download latest forge version");
            }


            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
            ETF.start(ETFClientForge::isThisModLoaded, ETFClientForge::modsLoaded, getConfigDir(),true);
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod on the server, refusing");
        }
    }

    public static boolean isThisModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static File getConfigDir() {
        return FMLPaths.GAMEDIR.get().resolve(FMLPaths.CONFIGDIR.get()).toFile();
    }
    public static List<String> modsLoaded() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).toList();
    }

}
