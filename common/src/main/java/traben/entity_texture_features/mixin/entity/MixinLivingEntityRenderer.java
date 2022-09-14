package traben.entity_texture_features.mixin.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;
import traben.entity_texture_features.texture_handlers.ETFTexture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;


@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    private ETFTexture thisETFTexture = null;
    private ETFPlayerTexture thisETFPlayerTexture = null;


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
                //System.out.println("test 1");
                // noinspection unchecked
                thisETFPlayerTexture.renderFeatures(matrixStack, vertexConsumerProvider, i, (PlayerEntityModel<PlayerEntity>) this.getModel());
                //System.out.println("test 2");
            }
            //just a little harmless particle effect on the dev
            if (livingEntity.getUuid().equals(ETFPlayerTexture.Dev) && !MinecraftClient.getInstance().isPaused() && livingEntity.getRandom().nextInt(64) == 0 && (MinecraftClient.getInstance().player == null || !(ETFVersionDifferenceHandler.areShadersInUse() == ETFPlayerTexture.Dev.equals(MinecraftClient.getInstance().player.getUuid())))) {
                livingEntity.world.addParticle(ParticleTypes.TOTEM_OF_UNDYING, livingEntity.getX(), livingEntity.getRandomBodyY(), livingEntity.getZ(), livingEntity.getRandom().nextFloat() - 0.5, livingEntity.getRandom().nextFloat() * 0.5, livingEntity.getRandom().nextFloat() - 0.5);
            }
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
    private Identifier etf$alterTexture(LivingEntityRenderer<T, M> instance, Entity inentity) {


        @SuppressWarnings("unchecked") T entity = (T) inentity;

        if (ETFConfigData.skinFeaturesEnabled && entity instanceof PlayerEntity player) {
            thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(player);
            if (thisETFPlayerTexture != null) {

                Identifier etfTexture = thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(player);
                return etfTexture == null ? getTexture(entity) : etfTexture;
            }
            return getTexture(entity);
        }
        thisETFTexture = ETFManager.getInstance().getETFTexture(getTexture(entity), entity, ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);

        return thisETFTexture.getTextureIdentifier(entity);


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

}


