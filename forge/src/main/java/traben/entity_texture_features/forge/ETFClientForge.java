package traben.entity_texture_features.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;
import traben.entity_texture_features.ETF;

@Mod("entity_texture_features")
public class ETFClientForge {

   // public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ETFClientCommon.MOD_ID);

    //public static final EntityType<ETFPlaceholderEntity> ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE = EntityType.Builder.<ETFPlaceholderEntity>create(ETFPlaceholderEntity::new, SpawnGroup.MISC).disableSummon().disableSaving()
    //        .build(new Identifier(ETFClientCommon.MOD_ID + ":etf_placeholder_entity").toString());

    // public static EntityType<ETFPlaceholderEntity> ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE= new EntityType<>( (EntityType.EntityFactory<ETFPlaceholderEntity>) ETFPlaceholderEntity::new, SpawnGroup.MISC, false, false, true, false, ImmutableSet.<Block>builder().build() , EntityDimensions.fixed(0.75f, 0.75f), 0, 0, FeatureFlags.DEFAULT_ENABLED_FEATURES);


//    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
//    public static final RegistryObject<EntityType<ETFPlaceholderEntity>> ETF_PLACEHOLDER_ENTITY_ENTITY_REGISTRY = ENTITIES.register(
//            "etf_placeholder_entity",
//            () -> new EntityType<>(ETFPlaceholderEntity::new, SpawnGroup.MISC, false, false, true, false, ImmutableSet.<Block>builder().build(), EntityDimensions.fixed(0.75f, 0.75f), 0, 0));
//

    //public static EntityType<?> ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE = EntityType.Builder.create(ETFPlaceholderEntity::new, SpawnGroup.MISC).disableSaving().disableSummon().build(ETFClientCommon.MOD_ID + ":etf_placeholder_entity");
    public ETFClientForge() {

//        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());


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


            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
            ETF.start();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod on the server, refusing");
        }
    }
}
