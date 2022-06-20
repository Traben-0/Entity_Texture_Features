package traben.entity_texture_features.mixin.client.entity.featureRenderers;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.utils.ETFUtils;

import static traben.entity_texture_features.client.ETFClient.*;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context, Identifier getArmorTexture) {
        super(context);
    }

    @Shadow
    protected abstract Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay);


    @ModifyArg(method = "renderArmorParts",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$changetexture(Identifier texture) {
        if (ETFConfigData.enableEmissiveTextures && PATH_EMISSIVE_TEXTURE_IDENTIFIER.containsKey(texture.toString())) {
            if (PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(texture.toString()) != null) {
                if (!PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(texture.toString())) {
                    //prevent flickering by removing pixels from the base texture
                    // the iris fix setting will now require a re-load
                    ETFUtils.applyETFEmissivePatchingToTexture(texture.toString());
                }
                if (PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(texture.toString())) {
                    if (PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.getBoolean(texture.toString())) {
                        return new Identifier(texture + "etf_iris_patched_file.png");
                    }
                }
            }
        }
        return texture;
    }

    @Inject(method = "renderArmorParts",
            at = @At(value = "TAIL"))
    private void etf$applyEmissive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs, float red, float green, float blue, String overlay, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (ETFConfigData.enableEmissiveTextures) {

            String fileString = getArmorTexture(item, legs, overlay).toString();
            if (!fileString.contains(".png"))
                fileString = fileString + ".png";
            if (!PATH_EMISSIVE_TEXTURE_IDENTIFIER.containsKey(fileString)) {
                //creates and sets emissive for texture if it exists
                Identifier fileName_e;
                for (String suffix1 :
                        emissiveSuffixes) {
                    fileName_e = new Identifier(fileString.replace(".png", suffix1 + ".png"));
                    if (ETFUtils.isExistingNativeImageFile(fileName_e)) {
                        PATH_EMISSIVE_TEXTURE_IDENTIFIER.put(fileString, fileName_e);
                        break;
                    }
                }
                if (!PATH_EMISSIVE_TEXTURE_IDENTIFIER.containsKey(fileString)) {
                    PATH_EMISSIVE_TEXTURE_IDENTIFIER.put(fileString, null);
                }
            }
            if (PATH_EMISSIVE_TEXTURE_IDENTIFIER.containsKey(fileString)) {
                if (PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString) != null) {
                    VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                    if (ETFConfigData.fullBrightEmissives) {
                        textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                    } else {
                        textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getEntityTranslucent(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString)), false, usesSecondLayer);
                    }


                    //one check most efficient instead of before and after applying
//                    if (ETFConfigData.doShadersEmissiveFix) {
//                        matrices.scale(1.01f, 1.01f, 1.01f);
//                        model.render(matrices, textureVert, 15728640, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
//                        matrices.scale(1f, 1f, 1f);
//                    } else {
                    model.render(matrices, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
//                    }
                }
            }
        }
    }


}


