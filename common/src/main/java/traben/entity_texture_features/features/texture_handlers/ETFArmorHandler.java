package traben.entity_texture_features.features.texture_handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFUtils2;

//todo is the patching still required?
// this might have been only used for iris issues that were fixed by inflating the matrices?
public class ETFArmorHandler {

    private ETFTexture texture = null;
    private ETFTexture trimTexture = null;


    public void start(){
        ETFRenderContext.preventRenderLayerTextureModify();
        //todo ETFRenderContext.allowTexturePatching();
    }
    public void end(){
        ETFRenderContext.allowRenderLayerTextureModify();
        //todo ETFRenderContext.preventTexturePatching();
    }

    public ResourceLocation getBaseTexture(ResourceLocation vanilla) {
        if(ETF.config().getConfig().enableArmorAndTrims) {
            texture = ETFManager.getInstance().getETFTextureNoVariation(vanilla);
            //noinspection ConstantConditions
            if (texture != null) {
                //todo thisETFTexture.reRegisterBaseTexture();
                return texture.getTextureIdentifier(null);
            }
        }
        return vanilla;
    }

    public void renderBaseEmissive(final PoseStack matrices, final MultiBufferSource vertexConsumers, final Model model, final float red, final float green, final float blue) {

        //UUID id = livingEntity.getUuid();
        if (texture != null && ETF.config().getConfig().canDoEmissiveTextures()) {
            ResourceLocation emissive = texture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                //    textureVert = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(emissive, true));//ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
                //} else {
                textureVert = vertexConsumers.getBuffer(RenderType.armorCutoutNoCull(emissive)); //ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getEntityTranslucent(emissive), false, usesSecondLayer);
                //}
                ETFRenderContext.startSpecialRenderOverlayPhase();
                //do not pop/push as we want the scaling to trickle down to trim rendering, so they appear over the emissive
                if (ETF.IRIS_DETECTED) matrices.scale(1.001f,1.001f,1.001f);
                model.renderToBuffer(matrices, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY #if MC < MC_21 , 1, 1, 1, 1.0F #endif);
                ETFRenderContext.startSpecialRenderOverlayPhase();
            }
        }

    }

    public void setTrim(final #if MC >= MC_20_6 Holder<ArmorMaterial> #else ArmorMaterial #endif armorMaterial, final ArmorTrim trim, final boolean leggings) {
        if(ETF.config().getConfig().enableArmorAndTrims) {
            ResourceLocation trimBaseId = leggings ? trim.innerTexture(armorMaterial) : trim.outerTexture(armorMaterial);
            //support modded trims with namespace
            ResourceLocation trimMaterialIdentifier = ETFUtils2.res(trimBaseId.getNamespace(), "textures/" + trimBaseId.getPath() + ".png");
            trimTexture = ETFManager.getInstance().getETFTextureNoVariation(trimMaterialIdentifier);

            //if it is emmissive we need to create an identifier of the trim to render separately in iris
//todo            if (!trimTexture.exists()
//                    && ETF.config().getConfig().canDoEmissiveTextures()
//                    && trimTexture.isEmissive()
//                    && ETF.IRIS_DETECTED) {
//                trimTexture.buildTrimTexture(trim, leggings);
//            }
        }
    }

    public VertexConsumer modifyTrim(VertexConsumer par2) {
        //allow a specified override trim texture if you dont want to be confined by a pallette
//todo        if(trimTexture!= null
//                && par2 instanceof ETFVertexConsumer etfVertexConsumer
//                && etfVertexConsumer.etf$getProvider() != null){
//            if(trimTexture.exists()){
//                return Objects.requireNonNull(etfVertexConsumer.etf$getProvider()).getBuffer(RenderType.armorCutoutNoCull(trimTexture.getTextureIdentifier(null)));
//            }else if (trimTexture.isEmissive() && ETF.config().getConfig().canDoEmissiveTextures() && ETF.IRIS_DETECTED){
//                //iris is weird and will always render the armor trim atlas over everything else
//                // if for some reason no trim texture is present then just dont render it at all
//                // this is to favour packs with fully emissive trims :/
//                return Objects.requireNonNull(etfVertexConsumer.etf$getProvider()).getBuffer(RenderType.armorCutoutNoCull(ETFManager.getErrorETFTexture().thisIdentifier));
//            }
//        }
        return par2;
    }

    public void renderTrimEmissive(final PoseStack matrices, final MultiBufferSource vertexConsumers, final Model model) {
        if(trimTexture != null && ETF.config().getConfig().canDoEmissiveTextures()){
            //trimTexture.renderEmissive(matrices,vertexConsumers,model);
            ResourceLocation emissive = trimTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert= vertexConsumers.getBuffer(RenderType.armorCutoutNoCull(emissive));
                ETFRenderContext.startSpecialRenderOverlayPhase();
                if (ETF.IRIS_DETECTED) matrices.scale(1.001f,1.001f,1.001f);
                model.renderToBuffer(matrices, textureVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY #if MC < MC_21 , 1, 1, 1, 1.0F #endif);
                ETFRenderContext.endSpecialRenderOverlayPhase();
            }
        }
    }
}
