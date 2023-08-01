package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.entity_handlers.ETFEntityWrapper;
import traben.entity_texture_features.texture_handlers.ETFManager;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(EyesFeatureRenderer.class)
public abstract class MixinEyesFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {




    @SuppressWarnings("unused")
    public MixinEyesFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @ModifyConstant(method = "render", constant = @Constant(intValue = 15728640))
    private int etf$markLightValueForEMF(int value) {
        return ETFClientCommon.EYES_FEATURE_LIGHT_VALUE;
        //todo move to EMF
    }

    @Unique
    private RenderLayer etf$eyesRenderLayer = null;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void etf$mixin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        etf$eyesRenderLayer = null;
        if (ETFConfigData.enableCustomTextures) {
            //this variant on usage of the general method is due to the way this mixin injects and cancels
            //String check;// = this.etf$getAlteredEyesTexture((LivingEntity) entity);

            //only 3 instances of this, if-else block should be fine, but can possibly be made faster for runtime
            //I'm almost tempted to remove the eyes feature and add "_eyes" as a default emissive suffix, but I will not remove the vanilla behaviour
            if (entity instanceof EndermanEntity) {
                etf$setEyes("textures/entity/enderman/enderman_eyes.png",entity);
            } else if (entity instanceof SpiderEntity) {
                etf$setEyes("textures/entity/spider_eyes.png",entity);
            } else if (entity instanceof PhantomEntity) {
                etf$setEyes("textures/entity/phantom_eyes.png",entity);
            }
        }
    }

    @Unique
    private void etf$setEyes(String texture, T entity){
        Identifier textureId = new Identifier(texture);
        Identifier altered = ETFManager.getInstance().getETFTexture(textureId, new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveMobs).getTextureIdentifier(new ETFEntityWrapper(entity));
        //if the feature has changed to a variant perform the custom render and cancel the vanilla render
        if (!altered.equals(textureId)) {
            etf$eyesRenderLayer = RenderLayer.getEyes(altered);
        }
    }

    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"),
            index = 0
    )
    private RenderLayer etf$modifyRenderLayer(RenderLayer layer) {
        if(etf$eyesRenderLayer != null) return etf$eyesRenderLayer;
        return layer;
    }

}
