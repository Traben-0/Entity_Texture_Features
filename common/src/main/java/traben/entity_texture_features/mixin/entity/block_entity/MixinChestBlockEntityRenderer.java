package traben.entity_texture_features.mixin.entity.block_entity;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.mixin.accessor.SpriteContentsAccessor;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFBlockEntityWrapper;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class MixinChestBlockEntityRenderer<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<T> {

    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;
    @Unique
    private boolean entity_texture_features$isAnimatedTexture = false;
    @Unique
    private ETFBlockEntityWrapper etf$chestStandInDummy = null;
    @Unique
    private Identifier etf$textureOfThis = null;
    @Unique
    private VertexConsumerProvider etf$vertexConsumerProviderOfThis = null;

    @ModifyArg(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/ChestBlockEntityRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;FII)V"),
            index = 1)
    private VertexConsumer etf$alterTexture(VertexConsumer vertices) {
        try {
            if (entity_texture_features$isAnimatedTexture || !ETFConfigData.enableCustomTextures || !ETFConfigData.enableCustomBlockEntities)
                return vertices;
            entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(etf$textureOfThis, etf$chestStandInDummy, ETFManager.TextureSource.BLOCK_ENTITY, false);
            //etf$textureOfThis = ETFUtils.generalProcessAndReturnAlteredTexture(etf$textureOfThis, etf$chestStandInDummy);

            VertexConsumer alteredReturn = etf$vertexConsumerProviderOfThis.getBuffer(RenderLayer.getEntityCutout(entity_texture_features$thisETFTexture.getTextureIdentifier(etf$chestStandInDummy)));
            return alteredReturn == null ? vertices : alteredReturn;
        } catch (Exception e) {
            return vertices;
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$getChestTexture(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci, World world, boolean bl, BlockState blockState, ChestType chestType, Block block, AbstractChestBlock<?> abstractChestBlock, boolean bl2, float f, DoubleBlockProperties.PropertySource<?> propertySource, float g, int i, SpriteIdentifier spriteIdentifier) {
        try {
            entity_texture_features$isAnimatedTexture = ((SpriteContentsAccessor) spriteIdentifier.getSprite().getContents()).callGetFrameCount() != 1;
            if (!entity_texture_features$isAnimatedTexture) {
                //hopefully works in modded scenarios, assumes the mod dev uses the actual vanilla code process and texture pathing rules
                String nameSpace = spriteIdentifier.getTextureId().getNamespace();
                String texturePath = "textures/" + spriteIdentifier.getTextureId().getPath() + ".png";
                etf$textureOfThis = new Identifier(nameSpace, texturePath);
                etf$vertexConsumerProviderOfThis = vertexConsumers;
                if (ETFConfigData.enableCustomTextures && ETFConfigData.enableCustomBlockEntities) {

                    int hash = chestType.hashCode();
                    if (entity instanceof Nameable nameable) {
                        if (nameable.hasCustomName()) {
                            //noinspection DataFlowIssue
                            hash += nameable.getCustomName().getString().hashCode();
                        }
                    }
                    //chests don't have uuid so set UUID from something repeatable this uses blockPos chestType & container name
                    World worldCheck = entity.getWorld();
                    if (worldCheck == null) worldCheck = MinecraftClient.getInstance().world;
                    if (worldCheck != null) {
                        etf$chestStandInDummy = new ETFBlockEntityWrapper(entity, hash);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;FII)V",
            at = @At(value = "TAIL"))
    private void etf$renderEmissiveChest(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart latch, ModelPart base, float openFactor, int light, int overlay, CallbackInfo ci) {
        try {
            if (!entity_texture_features$isAnimatedTexture && ETFConfigData.enableEmissiveBlockEntities && (entity_texture_features$thisETFTexture != null)) {
                entity_texture_features$thisETFTexture.renderEmissive(matrices, etf$vertexConsumerProviderOfThis, lid, ETFManager.EmissiveRenderModes.blockEntityMode());
                entity_texture_features$thisETFTexture.renderEmissive(matrices, etf$vertexConsumerProviderOfThis, latch, ETFManager.EmissiveRenderModes.blockEntityMode());
                entity_texture_features$thisETFTexture.renderEmissive(matrices, etf$vertexConsumerProviderOfThis, base, ETFManager.EmissiveRenderModes.blockEntityMode());

                etf$textureOfThis = null;
                etf$vertexConsumerProviderOfThis = null;
            }
        } catch (Exception ignored) {

        }
    }


}


