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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;

import java.util.Map;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MIX_ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> implements ETF_METHODS {
    public MIX_ArmorFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Final
    @Shadow
    private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;


    @Inject(method = "renderArmorParts",
            at = @At(value = "TAIL"))
    private void ETF_applyEmissive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs, float red, float green, float blue, String overlay, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (ETF_ConfigData.enableEmissiveTextures) {

            String fileString = ETF_getArmorTexture(item, legs, overlay).toString();

            if (!ETF_PATH_EmissiveTextureIdentifier.containsKey(fileString)) {
                //creates and sets emissive for texture if it exists
                Identifier fileName_e;
                for (String suffix1 :
                        ETF_emissiveSuffixes) {
                    fileName_e = new Identifier(fileString.replace(".png", suffix1 + ".png"));
                    if (ETF_isExistingFile(fileName_e)) {
                        ETF_PATH_EmissiveTextureIdentifier.put(fileString, fileName_e);
                        break;
                    }
                }
                if (!ETF_PATH_EmissiveTextureIdentifier.containsKey(fileString)) {
                    ETF_PATH_EmissiveTextureIdentifier.put(fileString, null);
                }
            }
            if (ETF_PATH_EmissiveTextureIdentifier.containsKey(fileString)) {
                if (ETF_PATH_EmissiveTextureIdentifier.get(fileString) != null) {
                    //VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(Texture_Emissive.get(fileString), true));
                    VertexConsumer textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(ETF_PATH_EmissiveTextureIdentifier.get(fileString), true), false, usesSecondLayer);
                    //one check most efficient instead of before and after applying
                    if (ETF_ConfigData.doShadersEmissiveFix) {
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

    private Identifier ETF_getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay) {
        String var10000 = item.getMaterial().getName();
        String string = "textures/models/armor/" + var10000 + "_layer_" + (legs ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
    }

}


