package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETFUtils;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context, Identifier getArmorTexture) {
        super(context);
    }

    @Shadow
    protected abstract Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay);


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
                    //VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(Texture_Emissive.get(fileString), true));
                    VertexConsumer textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                    //one check most efficient instead of before and after applying
                    if (ETFConfigData.doShadersEmissiveFix) {
                        matrices.scale(1.01f, 1.01f, 1.01f);
                        model.render(matrices, textureVert, 15728640, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
                        matrices.scale(1f, 1f, 1f);
                    } else {
                        model.render(matrices, textureVert, 15728640, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
                    }
                }
            }
        }
    }


}


