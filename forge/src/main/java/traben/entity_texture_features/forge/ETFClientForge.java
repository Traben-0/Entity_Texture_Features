package traben.entity_texture_features.forge;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;
import traben.entity_texture_features.utils.ETFPlaceholderEntity;


@Mod("entity_texture_features")
public class ETFClientForge {


    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ETFClientCommon.MOD_ID);

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static final RegistryObject<EntityType<ETFPlaceholderEntity>> ETF_PLACEHOLDER_ENTITY_ENTITY_REGISTRY = ENTITIES.register(
            "etf_placeholder_entity",
            () -> new EntityType<>(ETFPlaceholderEntity::new, SpawnGroup.MISC, false, false, true, false, ImmutableSet.<Block>builder().build(), EntityDimensions.fixed(0.75f, 0.75f), 0, 0));


    public ETFClientForge() {


        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());



        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            try {

                ModLoadingContext.get().registerExtensionPoint(
                        ExtensionPoint.CONFIGGUIFACTORY,
                                                () -> (mc, screen) -> new ETFConfigScreenMain(screen));


            } catch (NoClassDefFoundError e) {
                System.out.println("[Entity Texture Features]: Mod config error");
            }

            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, ()-> Pair.of(()-> FMLNetworkConstants.IGNORESERVERONLY, (version, network) -> {return true;}));
            ETFClientCommon.start();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod on the server, refusing");
        }
    }
}
