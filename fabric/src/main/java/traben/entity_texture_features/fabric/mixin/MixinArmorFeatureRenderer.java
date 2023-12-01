package traben.entity_texture_features.fabric.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.config.screens.ETFConfigScreen;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Unique
    private ETFTexture thisETFTexture = null;
    @Unique
    private ETFTexture thisETFTrimTexture = null;

    @SuppressWarnings("unused")
    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$markNotToChange(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFRenderContext.preventRenderLayerTextureModify();
//        ETFRenderContext.allowTexturePatching();
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "RETURN"))
    private void etf$markAllowedToChange(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.preventTexturePatching();
    }

    @ModifyArg(method = "renderArmorParts",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"),index = 0)
    private Identifier etf$changeTexture2(Identifier texture) {
        thisETFTexture = ETFManager.getInstance().getETFTextureNoVariation(texture);
        //noinspection ConstantConditions
        if (thisETFTexture != null) {
            thisETFTexture.reRegisterBaseTexture();
            return thisETFTexture.getTextureIdentifier(null);
        }
        return texture;
    }

    @Inject(method = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmorParts(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ArmorItem;Lnet/minecraft/client/render/entity/model/BipedEntityModel;ZFFFLjava/lang/String;)V",
            at = @At(value = "TAIL"))
    private void etf$applyEmissive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, A model, boolean secondTextureLayer, float red, float green, float blue, String overlay, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (thisETFTexture != null && ETFConfigData.enableEmissiveTextures) {
            Identifier emissive = thisETFTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                //    textureVert = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(emissive, true));//ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
                //} else {
                textureVert = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive)); //ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getEntityTranslucent(emissive), false, usesSecondLayer);
                //}
                ETFRenderContext.startSpecialRenderOverlayPhase();
                model.render(matrices, textureVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
                ETFRenderContext.startSpecialRenderOverlayPhase();
            }
        }

    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void etf$cancelIfUi(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (MinecraftClient.getInstance() != null) {
            if (MinecraftClient.getInstance().currentScreen instanceof ETFConfigScreen) {
                //cancel armour rendering
                ci.cancel();
            }
        }
    }


    @Inject(method = "renderTrim",
            at = @At(value = "HEAD"))
    private void etf$trimGet(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings, CallbackInfo ci) {


        Identifier trimBaseId = leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material) ;
        //support modded trims with namespace
        Identifier trimMaterialIdentifier = new Identifier(trimBaseId.getNamespace(),"textures/"+trimBaseId.getPath()+".png");
        thisETFTrimTexture = ETFManager.getInstance().getETFTextureNoVariation(trimMaterialIdentifier);

        //if it is emmissive we need to create an identifier of the trim to render separately in iris
        if(!thisETFTrimTexture.exists()
                && ETFConfigData.enableEmissiveTextures
                && thisETFTrimTexture.isEmissive()
                && ETFClientCommon.IRIS_DETECTED){
            thisETFTrimTexture.buildTrimTexture(trim,leggings);
        }


    }

    @ModifyArg(method = "renderTrim",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
            index = 1)
    private VertexConsumer etf$changeTrim(VertexConsumer par2) {
        //allow a specified override trim texture if you dont want to be confined by a pallette
        if(thisETFTrimTexture!= null){
            if(thisETFTrimTexture.exists()){
                return ETFRenderContext.getCurrentProvider().getBuffer(RenderLayer.getArmorCutoutNoCull(thisETFTrimTexture.getTextureIdentifier(null)));
            }else if (thisETFTrimTexture.isEmissive() && ETFConfigData.enableEmissiveTextures && ETFClientCommon.IRIS_DETECTED){
                //iris is weird and will always render the armor trim atlas over everything else
                // if for some reason no trim texture is present then just dont render it at all
                // this is to favour packs with fully emissive trims :/
                return ETFRenderContext.getCurrentProvider().getBuffer(RenderLayer.getArmorCutoutNoCull(ETFManager.getErrorETFTexture().thisIdentifier));
            }
        }
        return par2;
    }

    @Inject(method = "renderTrim",
            at = @At(value = "TAIL"))
    private void etf$trimEmissive(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings, CallbackInfo ci) {
        if(ETFConfigData.enableEmissiveTextures && thisETFTrimTexture != null){
            //trimTexture.renderEmissive(matrices,vertexConsumers,model);
            Identifier emissive = thisETFTrimTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert= vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive));
                ETFRenderContext.startSpecialRenderOverlayPhase();
                model.render(matrices, textureVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
                ETFRenderContext.endSpecialRenderOverlayPhase();
            }
        }
    }

}

