package traben.entity_texture_features.neoforge;


import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import traben.entity_texture_features.ETF;

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
            ETF.start();
        } else {
            throw new UnsupportedOperationException("Attempting to load a clientside only mod on the server, refusing");
        }
    }
}
