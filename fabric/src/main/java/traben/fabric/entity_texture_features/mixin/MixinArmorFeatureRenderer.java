package traben.fabric.entity_texture_features.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
#if MC >= MC_20_6
import net.minecraft.core.Holder;
#else
import net.minecraft.world.item.ArmorItem;
#endif
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.screens.skin.ETFScreenOldCompat;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;

import java.util.Objects;


@Mixin(HumanoidArmorLayer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Unique
    private ETFTexture thisETFTexture = null;
    @Unique
    private ETFTexture thisETFTrimTexture = null;

    @SuppressWarnings("unused")
    public MixinArmorFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
    }
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$markNotToChange(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFRenderContext.preventRenderLayerTextureModify();
//        ETFRenderContext.allowTexturePatching();
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "RETURN"))
    private void etf$markAllowedToChange(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.preventTexturePatching();
    }

    @ModifyArg(method = "renderModel",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"),index = 0)
    private ResourceLocation etf$changeTexture2(ResourceLocation texture) {
        if(ETF.config().getConfig().enableArmorAndTrims) {
            thisETFTexture = ETFManager.getInstance().getETFTextureNoVariation(texture);
            //noinspection ConstantConditions
            if (thisETFTexture != null) {
                thisETFTexture.reRegisterBaseTexture();
                return thisETFTexture.getTextureIdentifier(null);
            }
        }
        return texture;
    }

    @Inject(method = "renderModel",
            at = @At(value = "TAIL"))
    #if MC >= MC_20_6
    private void etf$applyEmissive(final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final A model, final float red, final float green, final float blue, final ResourceLocation overlay, final CallbackInfo ci) {
    #else
    private void etf$applyEmissive(final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final ArmorItem armorItem, final A model, final boolean bl, final float red, final float green, final float blue, final String string, final CallbackInfo ci) {
    #endif

        //UUID id = livingEntity.getUuid();
        if (thisETFTexture != null && ETF.config().getConfig().canDoEmissiveTextures()) {
            ResourceLocation emissive = thisETFTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                //    textureVert = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(emissive, true));//ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
                //} else {
                textureVert = vertexConsumers.getBuffer(RenderType.armorCutoutNoCull(emissive)); //ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getEntityTranslucent(emissive), false, usesSecondLayer);
                //}
                ETFRenderContext.startSpecialRenderOverlayPhase();
                model.renderToBuffer(matrices, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
                ETFRenderContext.startSpecialRenderOverlayPhase();
            }
        }

    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void etf$cancelIfUi(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (Minecraft.getInstance() != null) {
            if (Minecraft.getInstance().screen instanceof ETFScreenOldCompat) {
                //cancel armour rendering
                ci.cancel();
            }
        }
    }


    @Inject(method = "renderTrim",
            at = @At(value = "HEAD"))
    #if MC >= MC_20_6
    private void etf$trimGet(final Holder<ArmorMaterial> armorMaterial, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final ArmorTrim trim, final A model, final boolean leggings, final CallbackInfo ci) {
    #else
    private void etf$trimGet(final ArmorMaterial armorMaterial, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int i, final ArmorTrim trim, final A model, final boolean leggings, final CallbackInfo ci) {
    #endif
        if(ETF.config().getConfig().enableArmorAndTrims) {
            ResourceLocation trimBaseId = leggings ? trim.innerTexture(armorMaterial) : trim.outerTexture(armorMaterial);
            //support modded trims with namespace
            ResourceLocation trimMaterialIdentifier = new ResourceLocation(trimBaseId.getNamespace(), "textures/" + trimBaseId.getPath() + ".png");
            thisETFTrimTexture = ETFManager.getInstance().getETFTextureNoVariation(trimMaterialIdentifier);

            //if it is emmissive we need to create an identifier of the trim to render separately in iris
            if (!thisETFTrimTexture.exists()
                    && ETF.config().getConfig().canDoEmissiveTextures()
                    && thisETFTrimTexture.isEmissive()
                    && ETF.IRIS_DETECTED) {
                thisETFTrimTexture.buildTrimTexture(trim, leggings);
            }
        }
    }

    @ModifyArg(method = "renderTrim",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/HumanoidModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            index = 1)
    private VertexConsumer etf$changeTrim(VertexConsumer par2) {
        //allow a specified override trim texture if you dont want to be confined by a pallette
        if(thisETFTrimTexture!= null
                && par2 instanceof ETFVertexConsumer etfVertexConsumer
                && etfVertexConsumer.etf$getProvider() != null){
            if(thisETFTrimTexture.exists()){
                return Objects.requireNonNull(etfVertexConsumer.etf$getProvider()).getBuffer(RenderType.armorCutoutNoCull(thisETFTrimTexture.getTextureIdentifier(null)));
            }else if (thisETFTrimTexture.isEmissive() && ETF.config().getConfig().canDoEmissiveTextures() && ETF.IRIS_DETECTED){
                //iris is weird and will always render the armor trim atlas over everything else
                // if for some reason no trim texture is present then just dont render it at all
                // this is to favour packs with fully emissive trims :/
                return Objects.requireNonNull(etfVertexConsumer.etf$getProvider()).getBuffer(RenderType.armorCutoutNoCull(ETFManager.getErrorETFTexture().thisIdentifier));
            }
        }
        return par2;
    }

    @Inject(method = "renderTrim",
            at = @At(value = "TAIL"))
    #if MC >= MC_20_6
    private void etf$trimEmissive(final Holder<ArmorMaterial> armorMaterial, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final ArmorTrim trim, final A model, final boolean leggings, final CallbackInfo ci) {
    #else
    private void etf$trimEmissive(final ArmorMaterial armorMaterial, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int i, final ArmorTrim trim, final A model, final boolean bl, final CallbackInfo ci) {
    #endif
        if(thisETFTrimTexture != null && ETF.config().getConfig().canDoEmissiveTextures()){
            //trimTexture.renderEmissive(matrices,vertexConsumers,model);
            ResourceLocation emissive = thisETFTrimTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert= vertexConsumers.getBuffer(RenderType.armorCutoutNoCull(emissive));
                ETFRenderContext.startSpecialRenderOverlayPhase();
                model.renderToBuffer(matrices, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
                ETFRenderContext.endSpecialRenderOverlayPhase();
            }
        }
    }

}

