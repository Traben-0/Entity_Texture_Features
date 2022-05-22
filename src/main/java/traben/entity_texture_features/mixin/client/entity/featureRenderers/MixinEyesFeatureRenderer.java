package traben.entity_texture_features.mixin.client.entity.featureRenderers;

import net.minecraft.client.render.*;
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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.utils.ETFUtils;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;

@Mixin(EyesFeatureRenderer.class)
public abstract class MixinEyesFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {


    public MixinEyesFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void etf$mixin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (ETFConfigData.enableCustomTextures) {
            //this variant on usage of the general method is due to the way this mixin injects and cancels
            String check = this.etf$getAlteredEyesTexture((LivingEntity) entity);
            if (check != null) {
                Identifier altered = ETFUtils.generalReturnAlteredFeatureTextureOrOriginal(new Identifier(check), entity);
                //if the feature has changed to a variant perform the render
                if (!altered.toString().equals(check)) {
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(altered));
                    this.getContextModel().render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    ci.cancel();//cancel prevents the vanilla eyes rendering
                }
            }
        }
    }

    @Nullable
    private String etf$getAlteredEyesTexture(LivingEntity entity) {
        //String newPath = "";
        if (entity instanceof EndermanEntity) {
            //Identifier vanilla = new Identifier("textures/entity/enderman/enderman.png");
            return "textures/entity/enderman/enderman_eyes.png";
            //newPath = ETFUtils.generalReturnAlreadySetAlteredTexture(vanilla, entity).toString();
            //newPath = newPath.replace("enderman/enderman", "enderman/enderman_eyes");
        } else if (entity instanceof SpiderEntity) {
            //Identifier vanilla = new Identifier("textures/entity/spider/spider.png");
            return "textures/entity/spider/spider_eyes.png";
            //newPath = ETFUtils.generalReturnAlreadySetAlteredTexture(vanilla, entity).toString();
            //newPath = newPath.replace("spider/spider", "spider/spider_eyes");
        } else if (entity instanceof PhantomEntity) {
            //Identifier vanilla = new Identifier("textures/entity/phantom.png");
            return "textures/entity/phantom_eyes.png";
            //newPath = ETFUtils.generalReturnAlreadySetAlteredTexture(vanilla, entity).toString();
            //newPath = newPath.replace("entity/phantom", "entity/phantom_eyes");
        }
        return null;
        //return newPath;
    }

}
