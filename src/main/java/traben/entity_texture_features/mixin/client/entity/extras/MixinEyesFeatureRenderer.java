package traben.entity_texture_features.mixin.client.entity.extras;

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
import traben.entity_texture_features.client.utils.ETFUtils;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;
import static traben.entity_texture_features.client.ETFClient.PATH_IS_EXISTING_FEATURE;

@Mixin(EyesFeatureRenderer.class)
public abstract class MixinEyesFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {


    public MixinEyesFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void etf$mixin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (ETFConfigData.enableCustomTextures) {
            String check = this.etf$getAlteredEyesTexture((LivingEntity) entity);
            if (!PATH_IS_EXISTING_FEATURE.containsKey(check)) {
                PATH_IS_EXISTING_FEATURE.put(check, ETFUtils.isExistingNativeImageFile(new Identifier(check)));
            }
            if (PATH_IS_EXISTING_FEATURE.get(check)) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(new Identifier(check)));
                this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                ci.cancel();
            }
        }
    }


    private String etf$getAlteredEyesTexture(LivingEntity entity) {
        String newPath = "";
        if (entity instanceof EndermanEntity) {
            Identifier vanilla = new Identifier("textures/entity/enderman/enderman.png");
            newPath = ETFUtils.generalReturnAlreadySetAlteredTexture(vanilla, entity).toString();
            newPath = newPath.replace("enderman/enderman", "enderman/enderman_eyes");
        } else if (entity instanceof SpiderEntity) {
            Identifier vanilla = new Identifier("textures/entity/spider/spider.png");
            newPath = ETFUtils.generalReturnAlreadySetAlteredTexture(vanilla, entity).toString();
            newPath = newPath.replace("spider/spider", "spider/spider_eyes");
        } else if (entity instanceof PhantomEntity) {
            Identifier vanilla = new Identifier("textures/entity/phantom.png");
            newPath = ETFUtils.generalReturnAlreadySetAlteredTexture(vanilla, entity).toString();
            newPath = newPath.replace("entity/phantom", "entity/phantom_eyes");
        }
        return newPath;
    }

}
