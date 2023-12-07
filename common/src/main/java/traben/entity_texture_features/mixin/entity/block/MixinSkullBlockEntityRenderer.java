package traben.entity_texture_features.mixin.entity.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerEntity;
import traben.entity_texture_features.features.player.ETFPlayerFeatureRenderer;
import traben.entity_texture_features.features.player.ETFPlayerTexture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(SkullBlockEntityRenderer.class)
public abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<BedBlockEntity> {


    @Unique
    private ETFPlayerTexture entity_texture_features$thisETFPlayerTexture = null;


    @Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "HEAD"))
    private void etf$markNotToChange(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        ETFRenderContext.allowTexturePatching();
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "RETURN"))
    private void etf$markAllowedToChange(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.preventTexturePatching();
    }


    @Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;getRenderLayer(Lnet/minecraft/block/SkullBlock$SkullType;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/render/RenderLayer;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void etf$alterTexture(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.SkullType skullType, SkullBlockEntityModel skullBlockEntityModel) {

        entity_texture_features$thisETFPlayerTexture = null;

        if (skullType == SkullBlock.Type.PLAYER && ETFConfigData.skinFeaturesEnabled && ETFConfigData.enableCustomTextures && ETFConfigData.enableCustomBlockEntities) {
            if (skullBlockEntity.getOwner() != null) {
                Identifier identifier = MinecraftClient.getInstance()
                        .getSkinProvider().getSkinTextures(skullBlockEntity.getOwner()).texture();

                entity_texture_features$thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture((ETFPlayerEntity) skullBlockEntity, identifier);
                if(entity_texture_features$thisETFPlayerTexture != null){
                    ETFRenderContext.preventRenderLayerTextureModify();
                }
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void etf$renderFeatures(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.SkullType skullType, SkullBlockEntityModel skullBlockEntityModel, RenderLayer renderLayer) {
        if (entity_texture_features$thisETFPlayerTexture != null && ETFConfigData.enableEmissiveBlockEntities) {
            //vanilla positional code copy
            matrixStack.push();
            if (direction == null) {
                matrixStack.translate(0.5F, 0.0F, 0.5F);
            } else {
                matrixStack.translate(0.5F - (float) direction.getOffsetX() * 0.25F, 0.25F, 0.5F - (float) direction.getOffsetZ() * 0.25F);
            }
            matrixStack.scale(-1.0F, -1.0F, 1.0F);
            skullBlockEntityModel.setHeadRotation(g, h, 0.0F);
            //vanilla end

            ETFPlayerFeatureRenderer.renderSkullFeatures(matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, entity_texture_features$thisETFPlayerTexture,h);

            matrixStack.pop();
        }

    }

    @ModifyArg(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;renderSkull(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;)V")
            , index = 7)
    private RenderLayer etf$modifyRenderLayer(RenderLayer renderLayer) {
        if (entity_texture_features$thisETFPlayerTexture != null) {
            Identifier skin = entity_texture_features$thisETFPlayerTexture.getBaseHeadTextureIdentifierOrNullForVanilla();
            if (skin != null) {
                return RenderLayer.getEntityTranslucent(skin);
            }
        }
        return renderLayer;
    }


}


