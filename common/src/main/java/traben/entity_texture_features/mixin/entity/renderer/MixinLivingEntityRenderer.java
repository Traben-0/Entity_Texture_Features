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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
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



    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier etf$getTextureRedirect(LivingEntityRenderer<?,?> instance, Entity entity){
            etf$thisIdentifier = etf$getTexture(entity);
            return etf$thisIdentifier;
    }

    private Identifier etf$thisIdentifier = null;



    private Identifier etf$getTexture(Entity inEntity) {
        @SuppressWarnings("unchecked")// is always safe as it's always a living entity but IntelliJ won't believe it
        T entity = (T) inEntity;
        if (entity instanceof PlayerEntity player) {
            if (ETFConfigData.skinFeaturesEnabled) {

                thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(player, ((AbstractClientPlayerEntity) player).getSkinTexture());
                if (thisETFPlayerTexture != null) {

                    Identifier etfTexture = thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(player);
                    return etfTexture == null ? getTexture(entity) : etfTexture;
                }

            }

            Identifier vanillaTexture = getTexture(entity);
            //only return vanilla if it isn't steve or alex etc
            if (!("minecraft:texture/entity/steve.png".equals(vanillaTexture.toString())
                    || "minecraft:texture/entity/alex.png".equals(vanillaTexture.toString())
                    || vanillaTexture.toString().contains("minecraft:texture/entity/player/"))) {
                return vanillaTexture;
            }
            //otherwise uses regular optifine properties in offline mode as with any other mob
        }
        thisETFTexture = ETFManager.getInstance().getETFTexture(getTexture(entity), new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);

        return thisETFTexture.getTextureIdentifier(new ETFEntityWrapper( entity));


//
//        return getTexture(entity);
    }

//    @Inject(
//            method = "getRenderLayer",
//            at = @At(value = "RETURN"),cancellable = true)
//    private void etf$alterTextureLayerForGUI(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderLayer> cir) {
//
//        if (ETFConfigData.skinFeaturesEnabled && entity instanceof PlayerEntity && MinecraftClient.getInstance().currentScreen instanceof ETFConfigScreen) {
//            System.out.println(cir.getReturnValue().toString());
//            PlayerEntity player = (PlayerEntity) entity;
//            thisETFPlayerTexture = ETFManager.getPlayerTexture(player);
//            if (thisETFPlayerTexture != null) {
//                cir.setReturnValue( RenderLayer.getEntityTranslucentCull(thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(player)));
//            }
//        }
//    }
/*
     potion effects - pre rewrite feature
     potion status is just not sent to clients except in first seen / spawn packet
     there is no way to implement this feature reliably as a client only mod and has been removed for now
     similar features may return in some capacity

    private void etf$renderPotion(T livingEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int color) {
        if (thisETFTexture != null) {

            Collection<StatusEffectInstance> collection = livingEntity.getStatusEffects();
            boolean isRenderingPotions =  containsOnlyAmbientEffects(collection);
            Color potionColorAverage = new Color(PotionUtil.getColor(collection));

            VertexConsumer textureVert;
            switch (ETFConfigData.enchantedPotionEffects) {
                case ENCHANTED -> {
                    textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(thisETFTexture.getTextureIdentifier(livingEntity)), false, true);
                    this.getModel().render(matrixStack, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, potionColorAverage.getRed()/255f, potionColorAverage.getGreen()/255f, potionColorAverage.getBlue()/255f, 0.16F);
                }
                case GLOWING -> {
                    //textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(etf$returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity), true));
                    if (ETFConfigData.fullBrightEmissives) {
                        textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(thisETFTexture.getTextureIdentifier(livingEntity), true));
                    } else {
                        textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(thisETFTexture.getTextureIdentifier(livingEntity)));
                    }

                    this.getModel().render(matrixStack, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, potionColorAverage.getRed()/255f, potionColorAverage.getGreen()/255f, potionColorAverage.getBlue()/255f, 0.16F);
                }
                case CREEPER_CHARGE -> {
                    int f = (int) ((float) livingEntity.world.getTime()/10);
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEnergySwirl(new Identifier("textures/entity/creeper/creeper_armor.png"), f * 0.01F % 1.0F, f * 0.01F % 1.0F));
                    matrixStack.scale(1.1f, 1.1f, 1.1f);
                    this.getModel().render(matrixStack, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, potionColorAverage.getRed()/255f, potionColorAverage.getGreen()/255f, potionColorAverage.getBlue()/255f, 0.5F);
                    matrixStack.scale(1f, 1f, 1f);
                }
            }
        }
    }
*/

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
        if (ETFVersionDifferenceHandler.isThisModLoaded("skinlayers") || ETFVersionDifferenceHandler.isThisModLoaded("skinlayers3d")) {
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
                // unless a significant change if skin layers mod
                ETFUtils2.logError("Error with ETF's 3D skin layers mod compatibility: " + error);
            }
        }
        return featureRenderer;
    }

// initial skin layers compat testing
//    @Inject(
//            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V"
//                    , shift = At.Shift.AFTER)
//            , locals = LocalCapture.CAPTURE_FAILSOFT)
//    private void etf$3dSkinLayerCompat(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci, float h, float j, float k, float m, float l, float n, float o, MinecraftClient minecraftClient, boolean bl, boolean bl2, boolean bl3, RenderLayer renderLayer, Iterator var19, FeatureRenderer featureRenderer) {
//        // this appears to be the simplest method of *injecting* into the 3d skin layers mod's rendering
//        // as long as the rendering method names & parameters in the featureRenderers of that mod don't change this will always work with it,
//        // including any future changes internally to those classes
//        if (livingEntity instanceof AbstractClientPlayerEntity && ETFVersionDifferenceHandler.isThisModLoaded("skinlayers")) {
//            try {
//                //separate handler class so no skinlayers class is ever accidentally loaded if it's not present
//                //noinspection unchecked
//                ETF3DSkinLayersUtil.tryRenderWithETFFeatures((FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>) this, featureRenderer, (AbstractClientPlayerEntity) livingEntity, g, matrixStack, vertexConsumerProvider, i, k, m, l, n, o);
//            } catch (Exception e) {
//                ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod compatibility: " + e);
//            } catch (NoClassDefFoundError error) {
//                ETFUtils2.logError("Error with ETF's 3D skin layers mod compatibility: " + error);
//            }
//        }
//    }
}


