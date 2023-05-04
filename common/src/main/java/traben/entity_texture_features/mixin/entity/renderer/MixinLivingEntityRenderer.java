package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.mod_compat.ETF3DSkinLayersUtil;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;
import traben.entity_texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFEntityWrapper;
import traben.entity_texture_features.utils.ETFUtils2;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;


@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {


    private ETFTexture thisETFTexture = null;
    private ETFPlayerTexture thisETFPlayerTexture = null;

    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);

    }

    @Shadow
    public abstract M getModel();

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER)
    )
    private void etf$applyRenderFeatures(T livingEntity, float a, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (livingEntity instanceof PlayerEntity) {

            if (ETFConfigData.skinFeaturesEnabled && thisETFPlayerTexture != null) {


                thisETFPlayerTexture.renderFeatures(matrixStack, vertexConsumerProvider, i, this.getModel());

            }
            //just a little harmless particle effect on the dev
//            if (livingEntity.getUuid().equals(ETFPlayerTexture.Dev) && !MinecraftClient.getInstance().isPaused() && livingEntity.getRandom().nextInt(64) == 0 && (MinecraftClient.getInstance().player == null || !(ETFVersionDifferenceHandler.areShadersInUse() == ETFPlayerTexture.Dev.equals(MinecraftClient.getInstance().player.getUuid())))) {
//                livingEntity.world.addParticle(ParticleTypes.TOTEM_OF_UNDYING, livingEntity.getX(), livingEntity.getRandomBodyY(), livingEntity.getZ(), livingEntity.getRandom().nextFloat() - 0.5, livingEntity.getRandom().nextFloat() * 0.5, livingEntity.getRandom().nextFloat() - 0.5);
//            }
            //else nothing
        } else {
            if (thisETFTexture != null) {
                thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, this.getModel());
            }
        }
    }


// the redirect is not helpful for EMF compatibility the mess down below is preferable unfortunately
//    @Redirect(
//            method = "getRenderLayer",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
//    private Identifier etf$getTextureRedirect(LivingEntityRenderer<?,?> instance, Entity entity){
//            return etf$getAndSetTexture(entity, getTexture((T) entity));
//
//    }

    @Inject(
            method = "getRenderLayer",
            at = @At(value = "HEAD"))
    private void etf$getEntityParameter(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderLayer> cir){
        etf$thisEntity = entity;
    }

    @ModifyArg(
            method = "getRenderLayer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getItemEntityTranslucentCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$changeFirstPossibility(Identifier texture){
        return etf$getAndSetTexture(etf$thisEntity, texture);
    }
    @ModifyArg(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;getLayer(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$changeSecondPossiblility(Identifier texture){
        return etf$getAndSetTexture(etf$thisEntity, texture);
    }
    @ModifyArg(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getOutline(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$changeThirdPossiblility(Identifier texture){
        return etf$getAndSetTexture(etf$thisEntity, texture);
    }

    private T etf$thisEntity = null;
    private Identifier etf$thisIdentifier = null;



    private Identifier etf$getAndSetTexture(T entity, Identifier vanillaTexture) {

        if (entity instanceof PlayerEntity player) {
            if (ETFConfigData.skinFeaturesEnabled) {

                thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(player, ((AbstractClientPlayerEntity) player).getSkinTexture());
                if (thisETFPlayerTexture != null) {

                    Identifier etfTexture = thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(player);
                    etf$thisIdentifier = etfTexture == null ? vanillaTexture : etfTexture;
                    return etf$thisIdentifier;
                }

            }

            //Identifier vanillaTexture = vanillaTexture;
            //only return vanilla if it isn't steve or alex etc
            if (!("minecraft:texture/entity/steve.png".equals(vanillaTexture.toString())
                    || "minecraft:texture/entity/alex.png".equals(vanillaTexture.toString())
                    || vanillaTexture.toString().contains("minecraft:texture/entity/player/"))) {
                etf$thisIdentifier = vanillaTexture;
                return etf$thisIdentifier;
            }
            //otherwise uses regular optifine properties in offline mode as with any other mob
        }
        thisETFTexture = ETFManager.getInstance().getETFTexture(vanillaTexture, new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        etf$thisIdentifier = thisETFTexture.getTextureIdentifier(new ETFEntityWrapper( entity));
        return etf$thisIdentifier;


    }



    @Inject(method = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;",
            at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void etf$renderLayerModify(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderLayer> cir) {
        //Identifier identifier = etf$getTexture(entity);
        if (!translucent && showBody && ETFManager.getInstance().ENTITY_TYPE_RENDER_LAYER.containsKey(entity.getType())) {
            //Identifier identifier = this.getTexture(entity);
            int choice = ETFManager.getInstance().ENTITY_TYPE_RENDER_LAYER.getInt(entity.getType());
            //noinspection EnhancedSwitchMigration
            switch (choice) {
                case 1:
                    cir.setReturnValue(RenderLayer.getEntityTranslucent(etf$thisIdentifier));
                    break;
                case 2:
                    cir.setReturnValue(RenderLayer.getEntityTranslucentCull(etf$thisIdentifier));
                    break;
                case 3:
                    cir.setReturnValue(RenderLayer.getEndGateway());
                    break;
                case 4:
                    cir.setReturnValue(RenderLayer.getOutline(etf$thisIdentifier));
                    break;
                default:
                    cir.setReturnValue(cir.getReturnValue());
                    break;
            }
        } else {
            cir.setReturnValue(cir.getReturnValue());
        }
    }



    @ModifyArg(
            method = "addFeature",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),
            index = 0
    )
    private Object etf$3dSkinLayerCompat(Object featureRenderer) {
        // replace 3d skin layers mod feature renderers with ETF's child versions
        if (ETFManager.getInstance().skinLayersModPresent && ETFConfigData.use3DSkinLayerPatch){
            try {
                // handler class is only ever accessed if the mod is present
                // prevents NoClassDefFoundError
                if (ETF3DSkinLayersUtil.canReplace((FeatureRenderer<?, ?>) featureRenderer)) {
                    return ETF3DSkinLayersUtil.getReplacement((FeatureRenderer<?, ?>) featureRenderer, this);
                }
            } catch (Exception e) {
                ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod compatibility: " + e);
            } catch (NoClassDefFoundError error) {
                // Should never be thrown
                // unless a significant change in 3d skin layers mod
                ETFUtils2.logError("Error with ETF's 3D skin layers mod compatibility: " + error);
            }
        }
        return featureRenderer;
    }

}


