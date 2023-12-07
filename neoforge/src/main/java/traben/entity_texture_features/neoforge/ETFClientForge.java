package traben.entity_texture_features.neoforge;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;

@Mod("entity_texture_features")
public class ETFClientForge {



    public ETFClientForge() {

        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            try {
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory((minecraftClient, screen) -> {
                            try {
                                return new ETFConfigScreenMain(screen);}
                            catch (Exception e) {
                                System.out.println("[Entity Texture Features]: Mod config broken: " + e.getMessage());
                                return null;
                            }
                        }));
            } catch (NoClassDefFoundError e) {
                System.out.println("[Entity Texture Features]: Mod config broken, download latest forge version");
            }


            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
            ETFClientCommon.start();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod on the server, refusing");
        }
    }
}
