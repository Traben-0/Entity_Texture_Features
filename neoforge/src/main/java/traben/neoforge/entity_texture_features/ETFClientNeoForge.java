package traben.neoforge.entity_texture_features;


import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforgespi.language.IModInfo;
import traben.entity_texture_features.ETF;

import java.io.File;
import java.util.List;

@Mod("entity_texture_features")
public class ETFClientNeoForge {

    public ETFClientNeoForge() {

        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        if (FMLEnvironment.dist.isClient()) {
            try {
                ModLoadingContext.get().registerExtensionPoint(
                        IConfigScreenFactory.class,
                        ()-> ETF::getConfigScreen);
                       // () -> new ConfigScreenHandler.ConfigScreenFactory(ETF::getConfigScreen));
            } catch (NoClassDefFoundError e) {
                System.out.println("[Entity Texture Features]: Mod config broken, download latest forge version");
            }
            ETF.start(
                    ETFClientNeoForge::isThisModLoaded,
                    ETFClientNeoForge::modsLoaded,
                    ETFClientNeoForge::getConfigDir,
                    ETFClientNeoForge::isForge,
                    ETFClientNeoForge::isFabric);

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

    public static boolean isForge() {
        return true;
    }

    public static boolean isFabric() {
        return false;
    }

    public static List<String> modsLoaded() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).toList();
    }
}
