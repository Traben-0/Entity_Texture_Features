package traben.entity_texture_features.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;

import static net.minecraftforge.fmllegacy.network.FMLNetworkConstants.IGNORESERVERONLY;

@Mod("entity_texture_features")
public class ETFClientForge {
    public ETFClientForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            //try {
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigGuiHandler.ConfigGuiFactory.class,
                        () -> new ConfigGuiHandler.ConfigGuiFactory((minecraftClient, screen) -> new ETFConfigScreenMain(screen)));
            //}// catch (NoClassDefFoundError e) {
               // System.out.println("[Entity Texture Features]: Mod settings cannot be edited in GUI without cloth config");
            //}


            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IGNORESERVERONLY, (a, b) -> true));
            ETFClientCommon.start();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod on the server, refusing");
        }
    }
}
