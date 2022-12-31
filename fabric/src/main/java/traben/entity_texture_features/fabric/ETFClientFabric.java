package traben.entity_texture_features.fabric;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.utils.ETFPlaceholderEntity;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETFClientFabric implements ClientModInitializer {

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static final EntityType<ETFPlaceholderEntity> ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(ETFClientCommon.MOD_ID + "etf_placeholder_entity"),
            //FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CubeEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
            //new FabricEntityType<>(this.factory, this.spawnGroup, this.saveable, this.summonable, this.fireImmune, this.spawnableFarFromPlayer, this.specificSpawnBlocks, dimensions, trackRange, trackedUpdateRate, forceTrackedVelocityUpdates, FeatureFlags.DEFAULT_ENABLED_FEATURES);
            // EntityType.Builder.create(ETFPlaceholderEntity::new,SpawnGroup.MISC).disableSummon().disableSaving().build("etf_placeholder_entity")
            new EntityType<>(ETFPlaceholderEntity::new, SpawnGroup.MISC, false, false, true, false, ImmutableSet.<Block>builder().build(), EntityDimensions.fixed(0.75f, 0.75f), 0, 0)

    );


    @Override
    public void onInitializeClient() {


        EntityRendererRegistry.INSTANCE.register(ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE, new EntityRendererRegistry.Factory() {
            @Override
            public EntityRenderer<? extends Entity> create(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
                return new EntityRenderer<Entity>(manager) {
                    @Override
                    public Identifier getTexture(Entity entity) {
                        return null;
                    }

                    @Override
                    public void render(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
                        //do nothing
                    }
                };
            }
        });
        ETFClientCommon.start();
    }


}
