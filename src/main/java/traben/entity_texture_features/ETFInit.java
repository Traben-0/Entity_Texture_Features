package traben.entity_texture_features;

//#if FABRIC
import net.fabricmc.api.ClientModInitializer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETFInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() { ETF.start(); }
//#elseif FORGE
//$$ import net.minecraft.client.gui.screens.Screen;
//$$ import net.minecraftforge.api.distmarker.Dist;
//$$ import net.minecraftforge.client.ConfigScreenHandler;
//$$ import net.minecraftforge.fml.IExtensionPoint;
//$$ import net.minecraftforge.fml.ModLoadingContext;
//$$ import net.minecraftforge.fml.common.Mod;
//$$ import net.minecraftforge.fml.loading.FMLEnvironment;
//$$ import java.util.function.Function;
//$$
//$$ @net.minecraftforge.fml.common.Mod("entity_texture_features")
//$$ public class ETFInit {
//$$    public ETFInit() {
//$$        if (FMLEnvironment.dist == Dist.CLIENT) {
//$$            try {
//$$                 ModLoadingContext.get().registerExtensionPoint(
//$$                         ConfigScreenHandler.ConfigScreenFactory.class,
//$$                         () -> new ConfigScreenHandler.ConfigScreenFactory(
                        //#if MC >= 12004 || MC == 12001
                        //$$ (Function<Screen, Screen>)
                        //#endif
//$$                         ETF::getConfigScreen
//$$             ));
//$$             } catch (NoClassDefFoundError e) {
//$$                 System.out.println("[Entity Texture Features]: Mod config broken, download latest forge version");
//$$             }
//$$
//$$             ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
//$$             ETF.start();
//$$         }
//$$     }
//#else
    //#if MC >= 12006
    //$$ import net.minecraft.client.Minecraft;
    //$$ import net.minecraft.client.gui.screens.Screen;
    //$$ import net.neoforged.fml.ModContainer;
    //$$ import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
    //#else
    //$$ import net.neoforged.neoforge.client.ConfigScreenHandler;
    //#endif
//$$ import net.neoforged.fml.ModList;
//$$ import net.neoforged.fml.ModLoadingContext;
//$$ import net.neoforged.fml.common.Mod;
//$$ import net.neoforged.fml.loading.FMLEnvironment;
//$$ import net.neoforged.neoforgespi.language.IModInfo;
//$$ import traben.entity_texture_features.ETF;
//$$ import java.util.List;
//$$
//$$ @net.neoforged.fml.common.Mod("entity_texture_features")
//$$ public class ETFInit {
//$$     public ETFInit() {
//$$        if (FMLEnvironment
               //#if MC >= 12109
               //$$ .getDist()
               //#else
               //$$ .dist
               //#endif
//$$                .isClient()) {
//$$            try {
//$$                ModLoadingContext.get().registerExtensionPoint(
                        //#if MC >= 12100
                        //$$     IConfigScreenFactory.class, ()-> this::createScreen
                        //#elseif MC >= 12006
                        //$$     IConfigScreenFactory.class, ()-> ETF::getConfigScreen
                        //#else
                        //$$     ConfigScreenHandler.ConfigScreenFactory.class, ()-> new ConfigScreenHandler.ConfigScreenFactory(ETF::getConfigScreen)
                        //#endif
//$$                );
//$$            } catch (NoClassDefFoundError e) {
//$$                System.out.println("[Entity Texture Features]: Mod config broken, download latest neoforge version");
//$$            }
//$$            ETF.start();
//$$        }
//$$    }
        //#if MC >= 12100
        //$$ Screen createScreen(ModContainer arg, Screen arg2){
        //$$     return ETF.getConfigScreen(arg2);
        //$$ }
        //#endif
//#endif



}
