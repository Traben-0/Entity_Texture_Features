package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(EnderDragonEntityRenderer.class)
public abstract class MixinEnderDragonEntityRenderer extends EntityRenderer<EnderDragonEntity> {

    @Final
    @Shadow
    private static Identifier TEXTURE;           // = new Identifier("textures/entity/enderdragon/dragon.png");
    @Final
    @Shadow
    private static Identifier EYE_TEXTURE;      // = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
    @Final
    @Shadow
    private static RenderLayer DRAGON_CUTOUT;   //= RenderLayer.getEntityCutoutNoCull(TEXTURE);
    @Final
    @Shadow
    private static RenderLayer DRAGON_DECAL;    //= RenderLayer.getEntityDecal(TEXTURE);
    @Final
    @Shadow
    private static RenderLayer DRAGON_EYES;     //= RenderLayer.getEyes(EYE_TEXTURE);
    @Unique
    private ETFEntityWrapper etf$entity = null;
    @Final
    @Shadow
    private EnderDragonEntityRenderer.DragonEntityModel model;

    @SuppressWarnings("unused")
    protected MixinEnderDragonEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(EnderDragonEntity enderDragonEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        etf$entity = new ETFEntityWrapper(enderDragonEntity);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private RenderLayer etf$returnAlteredTexture(RenderLayer texturedRenderLayer) {

        if (ETFConfigData.enableCustomTextures) {
            try {
                Identifier alteredTexture;
                RenderLayer layerToReturn = null;
                if (texturedRenderLayer.equals(DRAGON_DECAL)) {
                    alteredTexture = ETFManager.getInstance().getETFTexture(TEXTURE, (etf$entity), ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveMobs).getTextureIdentifier(etf$entity);
                    layerToReturn = RenderLayer.getEntityDecal(alteredTexture);
                } else if (texturedRenderLayer.equals(DRAGON_CUTOUT)) {
                    alteredTexture = ETFManager.getInstance().getETFTexture(TEXTURE, (etf$entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs).getTextureIdentifier(etf$entity);
                    layerToReturn = RenderLayer.getEntityCutoutNoCull(alteredTexture);
                } else if (texturedRenderLayer.equals(DRAGON_EYES)) {
                    layerToReturn = RenderLayer.getEyes(ETFManager.getInstance().getETFTexture(EYE_TEXTURE, (etf$entity), ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveMobs).getTextureIdentifier(etf$entity));
                }
                if (layerToReturn != null) return layerToReturn;

            } catch (Exception e) {
                ETFUtils2.logError(e.toString(), false);
            }
        }

        return texturedRenderLayer;
    }

    @Inject(method = "render(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/client/render/entity/EnderDragonEntityRenderer$DragonEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissive(EnderDragonEntity enderDragonEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci, float h, float j, boolean bl, VertexConsumer vertexConsumer3) {
        //UUID id = livingEntity.getUuid();
        ETFManager.getInstance().getETFTexture(TEXTURE, (etf$entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs).renderEmissive(matrixStack, vertexConsumerProvider, model);

    }

}


