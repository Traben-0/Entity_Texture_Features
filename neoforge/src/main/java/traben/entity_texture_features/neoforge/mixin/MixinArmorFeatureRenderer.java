package traben.entity_texture_features.neoforge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
#if MC >= MC_20_6
import net.minecraft.core.Holder;
#else
import net.minecraft.world.item.ArmorItem;
#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Unique
    private ETFTexture thisETF$Texture = null;

    @Unique
    private ETFTexture thisETF$TrimTexture = null;

    @SuppressWarnings("unused")
    public MixinArmorFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$markNotToChange(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFRenderContext.preventRenderLayerTextureModify();
        ETFRenderContext.allowTexturePatching();
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "RETURN"))
    private void etf$markAllowedToChange(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.preventTexturePatching();
    }

    //todo this method is duplicated of forge, its possibly the source of the split mixins
    #if MC >= MC_20_6
    @ModifyArg(method = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;FFFLnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    #else
    @ModifyArg(method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    #endif
    private ResourceLocation etf$changeTexture(ResourceLocation texture) {
        if(ETF.config().getConfig().enableArmorAndTrims) {
            thisETF$Texture = ETFManager.getInstance().getETFTextureNoVariation(texture);
            //noinspection ConstantConditions
            if (thisETF$Texture != null) {
                thisETF$Texture.reRegisterBaseTexture();
                return thisETF$Texture.getTextureIdentifier(null);
            }
        }
        return texture;
    }
#if MC >= MC_20_6
    @Inject(method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;FFFLnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "TAIL"))
    private void etf$applyEmissive(final PoseStack arg, final MultiBufferSource arg2, final int i, final Model model, final float f, final float g, final float h, final ResourceLocation arg4, final CallbackInfo ci) {

#else
@Inject(method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V",
        at = @At(value = "TAIL"))
    private void etf$applyEmissive(final PoseStack arg, final MultiBufferSource arg2, final int i, final ArmorItem arg3, final Model model, final boolean bl, final float f, final float g, final float h, final ResourceLocation armorResource, final CallbackInfo ci) {

#endif
        //UUID id = livingEntity.getUuid();
        if (thisETF$Texture != null && ETF.config().getConfig().canDoEmissiveTextures()) {
            ResourceLocation emissive = thisETF$Texture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                //    textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
                //} else {
                textureVert = arg2.getBuffer(RenderType.armorCutoutNoCull(emissive));
                //}
                ETFRenderContext.startSpecialRenderOverlayPhase();
                model.renderToBuffer(arg, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY, f, g, h, 1.0F);
                ETFRenderContext.endSpecialRenderOverlayPhase();
            }
        }

    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void etf$cancelIfUi(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof ETFScreenOldCompat) {
            //cancel armour rendering
            ci.cancel();
        }
    }


#if MC >= MC_20_6
    @Inject(method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At(value = "HEAD"))
    private void etf$trimGet(final Holder<ArmorMaterial> arg, final PoseStack arg2, final MultiBufferSource arg3, final int i, final ArmorTrim arg4, final Model arg5, final boolean bl, final CallbackInfo ci) {

#else
    @Inject(method = "renderTrim(Lnet/minecraft/world/item/ArmorMaterial;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
        at = @At(value = "HEAD"))
    private void etf$trimGet(final ArmorMaterial arg, final PoseStack arg2, final MultiBufferSource arg3, final int i, final ArmorTrim arg4, final Model arg5, final boolean bl, final CallbackInfo ci) {
#endif
        if(ETF.config().getConfig().enableArmorAndTrims) {
            ResourceLocation trimBaseId = bl ? arg4.innerTexture(arg) : arg4.outerTexture(arg);
            //support modded trims with namespace
            ResourceLocation trimMaterialIdentifier = new ResourceLocation(trimBaseId.getNamespace(), "textures/" + trimBaseId.getPath() + ".png");
            thisETF$TrimTexture = ETFManager.getInstance().getETFTextureNoVariation(trimMaterialIdentifier);


            //if it is emmissive we need to create an identifier of the trim to render separately in iris
            if (!thisETF$TrimTexture.exists()
                    && ETF.config().getConfig().canDoEmissiveTextures()
                    && thisETF$TrimTexture.isEmissive()
                    && ETF.IRIS_DETECTED) {
                thisETF$TrimTexture.buildTrimTexture(arg4, bl);
            }
        }

    }
#if MC >= MC_20_6
    @ModifyArg(method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            index = 1)
#else
    @ModifyArg(method = "renderTrim(Lnet/minecraft/world/item/ArmorMaterial;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
        at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
        index = 1)
#endif
    private VertexConsumer etf$changeTrim(VertexConsumer par2) {
        //allow a specified override trim texture if you dont want to be confined by a pallette
        if(thisETF$TrimTexture!= null
                && par2 instanceof ETFVertexConsumer etfVertexConsumer
                && etfVertexConsumer.etf$getProvider() != null){
            if(thisETF$TrimTexture.exists()){
                return Objects.requireNonNull(etfVertexConsumer.etf$getProvider()).getBuffer(RenderType.armorCutoutNoCull(thisETF$TrimTexture.getTextureIdentifier(null)));
            }else if (ETF.config().getConfig().canDoEmissiveTextures() && thisETF$TrimTexture.isEmissive() && ETF.IRIS_DETECTED){
                //iris is weird and will always render the armor trim atlas over everything else
                // if for some reason no trim texture is present then just dont render it at all
                // this is to favour packs with fully emissive trims :/
                return Objects.requireNonNull(etfVertexConsumer.etf$getProvider()).getBuffer(RenderType.armorCutoutNoCull(Objects.requireNonNull(ETFManager.getErrorETFTexture().thisIdentifier)));
            }
        }
        return par2;
    }
#if MC >= MC_20_6
    @Inject(method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At(value = "TAIL"))
    private void etf$trimEmissive(final Holder<ArmorMaterial> arg, final PoseStack arg2, final MultiBufferSource arg3, final int i, final ArmorTrim arg4, final Model arg5, final boolean bl, final CallbackInfo ci) {
#else
@Inject(method = "renderTrim(Lnet/minecraft/world/item/ArmorMaterial;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
        at = @At(value = "TAIL"))

    private void etf$trimEmissive(final ArmorMaterial arg, final PoseStack arg2, final MultiBufferSource arg3, final int i, final ArmorTrim arg4, final Model arg5, final boolean bl, final CallbackInfo ci) {
#endif
    if(ETF.config().getConfig().canDoEmissiveTextures() && thisETF$TrimTexture != null){
            //trimTexture.renderEmissive(matrices,vertexConsumers,model);
            ResourceLocation emissive = thisETF$TrimTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert= arg3.getBuffer(RenderType.armorCutoutNoCull(emissive));
                ETFRenderContext.startSpecialRenderOverlayPhase();
                arg5.renderToBuffer(arg2, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
                ETFRenderContext.endSpecialRenderOverlayPhase();
            }
        }
    }

}


