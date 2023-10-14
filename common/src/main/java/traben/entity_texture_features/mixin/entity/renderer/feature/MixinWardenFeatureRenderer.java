package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WardenFeatureRenderer;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WardenEntity;
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
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(WardenFeatureRenderer.class)
public abstract class MixinWardenFeatureRenderer<T extends WardenEntity, M extends WardenEntityModel<T>> extends FeatureRenderer<T, M> {

    @Unique
    private static final Identifier VANILLA_TEXTURE = new Identifier("textures/entity/warden/warden.png");
    @Unique
    ETFEntityWrapper etf$entity = null;
    @Shadow
    @Final
    private Identifier texture;
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;
    @SuppressWarnings("unused")
    public MixinWardenFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Shadow
    protected abstract void unhideAllModelParts();

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/WardenEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/WardenEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)

    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, VertexConsumer vertexConsumer) {
        //UUID id = livingEntity.getUuid();
        if (entity_texture_features$thisETFTexture != null)
            entity_texture_features$thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, (this.getContextModel()));
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/WardenEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$entity = new ETFEntityWrapper(wardenEntity);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/WardenEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityTranslucentEmissive(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$returnAlteredTexture(Identifier texture) {
        entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(texture, etf$entity, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveMobs);
        return entity_texture_features$thisETFTexture.getTextureIdentifier(etf$entity);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/WardenEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/WardenFeatureRenderer;updateModelPartVisibility()V",
                    shift = At.Shift.AFTER))

    private void etf$preventHiding(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (ETFConfigData.enableFullBodyWardenTextures && !texture.equals(VANILLA_TEXTURE)) {
            unhideAllModelParts();
        }
    }
}


