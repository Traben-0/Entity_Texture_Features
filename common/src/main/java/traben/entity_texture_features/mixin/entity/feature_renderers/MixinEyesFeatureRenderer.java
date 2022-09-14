package traben.entity_texture_features.mixin.entity.feature_renderers;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.texture_handlers.ETFManager;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(EyesFeatureRenderer.class)
public abstract class MixinEyesFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {


    public MixinEyesFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void etf$mixin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (ETFConfigData.enableCustomTextures) {
            //this variant on usage of the general method is due to the way this mixin injects and cancels
            String check;// = this.etf$getAlteredEyesTexture((LivingEntity) entity);

            //only 3 instances of this, if-else block should be fine, but can possibly be made faster for runtime
            //I'm almost tempted to remove the eyes feature and add "_eyes" as a default emissive suffix, but I will not remove the vanilla behaviour
            if (entity instanceof EndermanEntity) {
                check = "textures/entity/enderman/enderman_eyes.png";
            } else if (entity instanceof SpiderEntity) {
                check = "textures/entity/spider_eyes.png";
            } else if (entity instanceof PhantomEntity) {
                check = "textures/entity/phantom_eyes.png";
            } else {
                check = null;
            }

            if (check != null) {
                Identifier altered = ETFManager.getInstance().getETFTexture(new Identifier(check), entity, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveMobs).getTextureIdentifier((LivingEntity) entity);
                //if the feature has changed to a variant perform the custom render and cancel the vanilla render
                if (!altered.toString().equals(check)) {
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(altered));

                    this.getContextModel().render(matrices, vertexConsumer, 15728640/* light value to match vanilla not ETFClientCommon.MAX_LIGHT_COORDINATE*/, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    ci.cancel();//cancel prevents the vanilla eyes rendering
                }
            }
        }
    }

}
