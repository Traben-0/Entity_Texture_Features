package traben.entity_texture_features.mixin.entity.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerEntity;
import traben.entity_texture_features.features.player.ETFPlayerFeatureRenderer;
import traben.entity_texture_features.features.player.ETFPlayerTexture;


@Mixin(SkullBlockRenderer.class)
public abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<BedBlockEntity> {


    @Unique
    private ETFPlayerTexture entity_texture_features$thisETFPlayerTexture = null;


    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "HEAD"))
    private void etf$markNotToChange(SkullBlockEntity skullBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        ETFRenderContext.allowTexturePatching();
    }

    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "RETURN"))
    private void etf$markAllowedToChange(SkullBlockEntity skullBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.preventTexturePatching();
    }

    #if MC >= MC_20_6
        @Inject(method = "render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;getRenderType(Lnet/minecraft/world/level/block/SkullBlock$Type;Lnet/minecraft/world/item/component/ResolvableProfile;)Lnet/minecraft/client/renderer/RenderType;"),
                locals = LocalCapture.CAPTURE_FAILHARD)
        private void etf$alterTexture(final SkullBlockEntity skullBlockEntity, final float f, final PoseStack matrixStack, final MultiBufferSource vertexConsumerProvider, final int i, final int j, final CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.Type skullType, SkullModelBase skullBlockEntityModel) {
    #else
    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;getRenderType(Lnet/minecraft/world/level/block/SkullBlock$Type;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/renderer/RenderType;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void etf$alterTexture(final SkullBlockEntity skullBlockEntity, final float partialTick, final PoseStack poseStack, final MultiBufferSource buffer, final int packedLight, final int packedOverlay, final CallbackInfo ci, final float f, final BlockState blockState, final boolean bl, final Direction direction, final int i, final float g, final SkullBlock.Type skullType, final SkullModelBase skullModelBase) {
#endif

        entity_texture_features$thisETFPlayerTexture = null;

        if (skullType == SkullBlock.Types.PLAYER && ETF.config().getConfig().skinFeaturesEnabled && ETF.config().getConfig().enableCustomTextures && ETF.config().getConfig().enableCustomBlockEntities) {
            if (skullBlockEntity.getOwnerProfile() != null) {
                ResourceLocation identifier =
                        #if MC >= MC_20_6
                            Minecraft.getInstance().getSkinManager().getInsecureSkin(skullBlockEntity.getOwnerProfile().gameProfile()).texture();
                        #elif MC > MC_20_1
                            Minecraft.getInstance().getSkinManager().getInsecureSkin(skullBlockEntity.getOwnerProfile()).texture();
                        #else
                            Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(skullBlockEntity.getOwnerProfile());
                        #endif

                entity_texture_features$thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture((ETFPlayerEntity) skullBlockEntity, identifier);
                if (entity_texture_features$thisETFPlayerTexture != null) {
                    ETFRenderContext.preventRenderLayerTextureModify();
                }
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void etf$renderFeatures(SkullBlockEntity skullBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.Type skullType, SkullModelBase skullBlockEntityModel, RenderType renderLayer) {
        if (entity_texture_features$thisETFPlayerTexture != null && ETF.config().getConfig().enableEmissiveBlockEntities) {
            //vanilla positional code copy
            matrixStack.pushPose();
            if (direction == null) {
                matrixStack.translate(0.5F, 0.0F, 0.5F);
            } else {
                matrixStack.translate(0.5F - (float) direction.getStepX() * 0.25F, 0.25F, 0.5F - (float) direction.getStepZ() * 0.25F);
            }
            matrixStack.scale(-1.0F, -1.0F, 1.0F);
            skullBlockEntityModel.setupAnim(g, h, 0.0F);
            //vanilla end

            ETFPlayerFeatureRenderer.renderSkullFeatures(matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, entity_texture_features$thisETFPlayerTexture, h);

            matrixStack.popPose();
        }

    }

    @ModifyArg(method = "render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;renderSkull(Lnet/minecraft/core/Direction;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/SkullModelBase;Lnet/minecraft/client/renderer/RenderType;)V")
            , index = 7)
    private RenderType etf$modifyRenderLayer(RenderType renderLayer) {
        if (entity_texture_features$thisETFPlayerTexture != null) {
            ResourceLocation skin = entity_texture_features$thisETFPlayerTexture.getBaseHeadTextureIdentifierOrNullForVanilla();
            if (skin != null) {
                return RenderType.entityTranslucent(skin);
            }
        }
        return renderLayer;
    }


}


