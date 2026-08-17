package traben.entity_texture_features.features.texture_handlers;


//#if MC < 12103
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import com.mojang.blaze3d.vertex.VertexConsumer;
//$$ import net.minecraft.client.model.Model;
//$$ import net.minecraft.client.renderer.MultiBufferSource;
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import net.minecraft.client.renderer.texture.OverlayTexture;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import net.minecraft.world.item.ArmorMaterial;
//$$ import net.minecraft.world.item.armortrim.ArmorTrim;
//$$ import traben.entity_texture_features.ETF;
//$$ import traben.entity_texture_features.features.ETFManager;
//$$ import traben.entity_texture_features.utils.ETFUtils2;
//#else
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
//#endif


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.model.Model;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.URenderTypeToVertexConsumer;

//todo 2 better cancelling out for post 1.21.2?
//todo is the patching still required?
// this might have been only used for iris issues that were fixed by inflating the matrices?
@SuppressWarnings("unused")
public class ETFArmorHandler {


    private ETFTexture trimTexture = null;

//#if MC < 12103
//$$
//$$     private ETFTexture texture = null;
//$$
//$$     public void start(){
//$$             ETFState.pushRenderLayerModifyState(false);
//$$         //todo ETFRenderContext.allowTexturePatching();
//$$     }
//$$     public void end(){
//$$         ETFState.popRenderLayerModifyState();
//$$         //todo ETFRenderContext.preventTexturePatching();
//$$     }
//$$
//$$     public ResourceLocation getBaseTexture(ResourceLocation vanilla) {
//$$         if(ETF.config().getConfig().enableArmorAndTrims) {
//$$             texture = ETFManager.getInstance().getETFTextureNoVariation(vanilla);
//$$             //noinspection ConstantConditions
//$$             if (texture != null) {
//$$                 //todo thisETFTexture.reRegisterBaseTexture();
//$$                 return texture.getTextureIdentifier(null);
//$$             }
//$$         }
//$$         return vanilla;
//$$     }
//$$
//$$     public void renderBaseEmissive(final PoseStack matrices, final URenderTypeToVertexConsumer vertexConsumers, final Model model, final float red, final float green, final float blue) {
//$$
//$$    //UUID id = livingEntity.getUuid();
//$$         if (texture != null && ETF.config().getConfig().canDoEmissiveTextures()) {
//$$             ResourceLocation emissive = texture.getEmissiveIdentifierOfCurrentState();
//$$             if (emissive != null) {
//$$                 VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers.delegate, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
//$$                 //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
//$$                 //    textureVert = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(emissive, true));//ItemRenderer.getArmorGlintConsumer(vertexConsumers.delegate, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
//$$                 //} else {
//$$                 textureVert = vertexConsumers.getBuffer(RenderType.armorCutoutNoCull(emissive)); //ItemRenderer.getArmorGlintConsumer(vertexConsumers.delegate, RenderLayer.getEntityTranslucent(emissive), false, usesSecondLayer);
//$$                 //}
//$$                 ETFState.startSpecialRenderOverlayPhase();
//$$                 //do not pop/push as we want the scaling to trickle down to trim rendering, so they appear over the emissive
//$$                 if (ETF.IRIS_DETECTED) matrices.scale(1.001f,1.001f,1.001f);
//$$                 model.renderToBuffer(matrices, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY
                //#if MC < 12100
                //$$  , 1, 1, 1, 1.0F
                //#endif
//$$                 );
//$$                 ETFState.startSpecialRenderOverlayPhase();
//$$             }
//$$         }
//$$     }
//$$
//$$     public void renderTrimEmissive(final PoseStack matrices, final URenderTypeToVertexConsumer vertexConsumers, final Model model) {
//$$         if(trimTexture != null && ETF.config().getConfig().canDoEmissiveTextures()){
//$$             //trimTexture.renderEmissive(matrices,vertexConsumers,model);
//$$             ResourceLocation emissive = trimTexture.getEmissiveIdentifierOfCurrentState();
//$$             if (emissive != null) {
//$$                 VertexConsumer textureVert= vertexConsumers.getBuffer(RenderType.armorCutoutNoCull(emissive));
//$$                 ETFState.startSpecialRenderOverlayPhase();
//$$                 if (ETF.IRIS_DETECTED) matrices.scale(1.001f,1.001f,1.001f);
//$$                 model.renderToBuffer(matrices, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY
//#if MC < 12100
//$$ , 1, 1, 1, 1.0F
//#endif
//$$ );
//$$                 ETFState.endSpecialRenderOverlayPhase();
//$$             }
//$$         }
//$$     }
//$$
//$$
//$$     public void setTrim(final
                                //#if MC >= 12006
                                //$$ Holder<ArmorMaterial>
                                //#else
                                //$$     ArmorMaterial
                                //#endif
//$$                                 armorMaterial, final ArmorTrim trim, final boolean leggings) {
//$$
//$$         if(ETF.config().getConfig().enableArmorAndTrims) {
//$$
//$$             ResourceLocation trimBaseId = leggings ? trim.innerTexture(armorMaterial) : trim.outerTexture(armorMaterial);
//$$             //support modded trims with namespace
//$$             ResourceLocation trimMaterialIdentifier = ETFUtils2.res(trimBaseId.getNamespace(), "textures/" + trimBaseId.getPath() + ".png");
//$$             trimTexture = ETFManager.getInstance().getETFTextureNoVariation(trimMaterialIdentifier);
//$$
//$$         }
//$$
//$$     }
//#else

    public void start(){
        ETFState.allowTexturePatching = true;
    }
    public void end(){
        ETFState.allowTexturePatching = false;
    }

    public void renderTrimEmissive(final PoseStack matrices, final URenderTypeToVertexConsumer vertexConsumers, final Model model) {
        if(trimTexture != null && ETF.config().getConfig().canDoEmissiveTextures()){
            ResourceLocation emissive = trimTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                ETFState.startSpecialRenderOverlayPhase();
                VertexConsumer textureVert = vertexConsumers.getBuffer(
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                                .armorCutoutNoCull(emissive));
//                if (ETF.IRIS_DETECTED)
                    matrices.scale(1.001f,1.001f,1.001f);//inflate
                model.renderToBuffer(matrices, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY
                //#if MC >= 26.2
                //$$ , -1
                //#endif
                );
                ETFState.endSpecialRenderOverlayPhase();
            }
        }
        trimTexture = null;
    }


    public void setTrim(ResourceLocation trimBaseId) {
        if(ETF.config().getConfig().enableArmorAndTrims) {
            //support modded trims with namespace
            ResourceLocation trimMaterialIdentifier = ETFUtils2.res(trimBaseId.getNamespace(), "textures/" + trimBaseId.getPath() + ".png");
            var trim = ETFManager.getInstance().getETFTextureNoVariation(trimMaterialIdentifier);
            if (trim.isEmissive())
                trimTexture = trim;
        }
    }


//#endif
}
