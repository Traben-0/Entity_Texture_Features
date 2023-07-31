package traben.entity_texture_features.forge.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
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
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFTexture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Unique
    private ETFTexture thisETF$Texture = null;

    @Unique
    private ETFTexture thisETF$TrimTexture = null;

    @SuppressWarnings("unused")
    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @ModifyArg(method = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderModel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/util/Identifier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$changeTexture(Identifier texture) {
        thisETF$Texture = ETFManager.getInstance().getETFTexture(texture, null, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveArmour);
        //noinspection ConstantConditions
        if (thisETF$Texture != null) {
            return thisETF$Texture.getTextureIdentifier(null, ETFConfigData.enableEmissiveTextures);
        }
        return texture;
    }

    @Inject(method = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderModel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/util/Identifier;)V",
            at = @At(value = "TAIL"))
    private void etf$applyEmissive(MatrixStack arg, VertexConsumerProvider arg2, int i, ArmorItem arg3, Model arg4, boolean bl, float f, float g, float h, Identifier armorResource, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (thisETF$Texture != null && ETFConfigData.enableEmissiveTextures) {
            Identifier emissive = thisETF$Texture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                //    textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
                //} else {
                textureVert = arg2.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive));
                //}
                arg4.render(arg, textureVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, f, g, h, 1.0F);
            }
        }

    }

    @Inject(method = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void etf$cancelIfUi(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (MinecraftClient.getInstance() != null) {
            if (MinecraftClient.getInstance().currentScreen instanceof ETFConfigScreen) {
                //cancel armour rendering
                ci.cancel();
            }
        }
    }

    @Unique
    private VertexConsumerProvider etf$VCP =null;

    @Inject(method = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderTrim(Lnet/minecraft/item/ArmorMaterial;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At(value = "HEAD"))
    private void etf$trimGet(ArmorMaterial material, MatrixStack arg2, VertexConsumerProvider vertexConsumers, int i, ArmorTrim trim, Model arg5, boolean leggings, CallbackInfo ci) {


        Identifier trimBaseId = leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material) ;
        //support modded trims with namespace
        Identifier trimMaterialIdentifier = new Identifier(trimBaseId.getNamespace(),"textures/"+trimBaseId.getPath()+".png");
        thisETF$TrimTexture = ETFManager.getInstance().getETFTexture(trimMaterialIdentifier, null, ETFManager.TextureSource.ENTITY_FEATURE, false);
        etf$VCP = vertexConsumers;

        //if it is emmissive we need to create an identifier of the trim to render seperately in iris
        if(!thisETF$TrimTexture.exists()
                && ETFConfigData.enableEmissiveTextures
                && thisETF$TrimTexture.isEmissive()
                && ETFClientCommon.IRIS_DETECTED){
            thisETF$TrimTexture.buildTrimTexture(trim,leggings);
        }


    }

    @ModifyArg(method = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderTrim(Lnet/minecraft/item/ArmorMaterial;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
            index = 1)
    private VertexConsumer etf$changeTrim(VertexConsumer par2) {
        //allow a specified override trim texture if you dont want to be confined by a pallette
        if(thisETF$TrimTexture!= null){
            if(thisETF$TrimTexture.exists()){
                return etf$VCP.getBuffer(RenderLayer.getArmorCutoutNoCull(thisETF$TrimTexture.getTextureIdentifier(null)));
            }else if (ETFConfigData.enableEmissiveTextures && thisETF$TrimTexture.isEmissive() && ETFClientCommon.IRIS_DETECTED){
                //iris is weird and will always render the armor trim atlas over everything else
                // if for some reason no trim texture is present then just dont render it at all
                // this is to favour packs with fully emissive trims :/
                return etf$VCP.getBuffer(RenderLayer.getArmorCutoutNoCull(ETFManager.getErrorETFTexture().thisIdentifier));
            }
        }
        return par2;
    }

    @Inject(method = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderTrim(Lnet/minecraft/item/ArmorMaterial;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At(value = "TAIL"))
    private void etf$trimEmissive(ArmorMaterial arg, MatrixStack arg2, VertexConsumerProvider arg3, int i, ArmorTrim arg4, Model arg5, boolean bl, CallbackInfo ci) {
        if(ETFConfigData.enableEmissiveTextures && thisETF$TrimTexture != null){
            //trimTexture.renderEmissive(matrices,vertexConsumers,model);
            Identifier emissive = thisETF$TrimTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert= arg3.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive));
                arg5.render(arg2, textureVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
            }
        }
    }

}


