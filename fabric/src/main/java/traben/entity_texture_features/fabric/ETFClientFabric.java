package traben.entity_texture_features.fabric;

import net.fabricmc.api.ClientModInitializer;
import traben.entity_texture_features.ETF;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETFClientFabric implements ClientModInitializer {

//    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
//    public static final EntityType<ETFPlaceholderEntity> ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE = Registry.register(
//            Registry.ENTITY_TYPE,
//            new Identifier(ETFClientCommon.MOD_ID + "etf_placeholder_entity"),
//            //FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CubeEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
//            //new FabricEntityType<>(this.factory, this.spawnGroup, this.saveable, this.summonable, this.fireImmune, this.spawnableFarFromPlayer, this.specificSpawnBlocks, dimensions, trackRange, trackedUpdateRate, forceTrackedVelocityUpdates, FeatureFlags.DEFAULT_ENABLED_FEATURES);
//            // EntityType.Builder.create(ETFPlaceholderEntity::new,SpawnGroup.MISC).disableSummon().disableSaving().build("etf_placeholder_entity")
//            new EntityType<>(ETFPlaceholderEntity::new, SpawnGroup.MISC, false, false, true, false, ImmutableSet.<Block>builder().build(), EntityDimensions.fixed(0.75f, 0.75f), 0, 0)
//
//    );


    @Override
    public void onInitializeClient() {
        ETF.start();
    }
}
