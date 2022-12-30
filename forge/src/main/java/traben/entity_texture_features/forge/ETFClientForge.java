package traben.entity_texture_features.forge;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;
import traben.entity_texture_features.utils.ETFPlaceholderEntity;

@Mod("entity_texture_features")
public class ETFClientForge {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ETFClientCommon.MOD_ID);

    //public static final EntityType<ETFPlaceholderEntity> ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE = EntityType.Builder.<ETFPlaceholderEntity>create(ETFPlaceholderEntity::new, SpawnGroup.MISC).disableSummon().disableSaving()
    //        .build(new Identifier(ETFClientCommon.MOD_ID + ":etf_placeholder_entity").toString());

    // public static EntityType<ETFPlaceholderEntity> ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE= new EntityType<>( (EntityType.EntityFactory<ETFPlaceholderEntity>) ETFPlaceholderEntity::new, SpawnGroup.MISC, false, false, true, false, ImmutableSet.<Block>builder().build() , EntityDimensions.fixed(0.75f, 0.75f), 0, 0, FeatureFlags.DEFAULT_ENABLED_FEATURES);


    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static final RegistryObject<EntityType<ETFPlaceholderEntity>> ETF_PLACEHOLDER_ENTITY_ENTITY_REGISTRY = ENTITIES.register(
            "etf_placeholder_entity",
            () -> new EntityType<>(ETFPlaceholderEntity::new, SpawnGroup.MISC, false, false, true, false, ImmutableSet.<Block>builder().build(), EntityDimensions.fixed(0.75f, 0.75f), 0, 0));


    //public static EntityType<?> ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE = EntityType.Builder.create(ETFPlaceholderEntity::new, SpawnGroup.MISC).disableSaving().disableSummon().build(ETFClientCommon.MOD_ID + ":etf_placeholder_entity");
    public ETFClientForge() {

        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());


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


            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
            ETFClientCommon.start();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod on the server, refusing");
        }
    }
}
