package traben.entity_texture_features.fabric.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;

@Mixin(EnergySwirlOverlayFeatureRenderer.class)
public abstract class MixinCreeperEnergySwirlFeatureRenderer<T extends Entity & SkinOverlayOwner, M extends EntityModel<T>> extends FeatureRenderer<T, M> {


    @Shadow protected abstract EntityModel<T> getEnergySwirlModel();

    public MixinCreeperEnergySwirlFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Unique
    ETFEntityWrapper etf$entity = null;
    @Unique
    private ETFTexture thisETFTexture = null;

    @Inject(method = "Lnet/minecraft/client/render/entity/feature/EnergySwirlOverlayFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V",
            at = @At(value = "TAIL"))
    private void etf$applyEmissive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        //emissives specifically do not use the energy swirl layer and don't rotate, this gives them a use here
        if (thisETFTexture != null && entity.shouldRenderOverlay())
            thisETFTexture.renderEmissive(matrices, vertexConsumers, getEnergySwirlModel());
    }

    @Inject(
            method = "Lnet/minecraft/client/render/entity/feature/EnergySwirlOverlayFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        etf$entity = new ETFEntityWrapper(entity);
    }

    @ModifyArg(
            method = "Lnet/minecraft/client/render/entity/feature/EnergySwirlOverlayFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getEnergySwirl(Lnet/minecraft/util/Identifier;FF)Lnet/minecraft/client/render/RenderLayer;")
            , index = 0)
    private Identifier etf$returnAlteredTexture(Identifier texture) {

        thisETFTexture = ETFManager.getInstance().getETFTexture(texture, etf$entity, ETFManager.TextureSource.ENTITY_FEATURE, false);//never patch, emissives are a special case here
        return thisETFTexture.getTextureIdentifier(etf$entity);
    }
}


