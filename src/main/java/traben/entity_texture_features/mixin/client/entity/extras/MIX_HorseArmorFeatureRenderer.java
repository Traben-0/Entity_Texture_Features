package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.ETF_METHODS;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(HorseArmorFeatureRenderer.class)
public abstract class MIX_HorseArmorFeatureRenderer extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> implements ETF_METHODS {


    public MIX_HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> context) {
        super(context);
    }


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/HorseEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/HorseEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)

    private void ETF_applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntity horseEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, HorseArmorItem horseArmorItem, float n, float o, float p, VertexConsumer vertexConsumer) {
        //UUID id = livingEntity.getUuid();
        if (ETF_ConfigData.enableEmissiveTextures) {

            String fileString = horseArmorItem.getEntityTexture().toString();

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
                    VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(ETF_PATH_EmissiveTextureIdentifier.get(fileString), true));
                    //one check most efficient instead of before and after applying
                    if (ETF_ConfigData.doShadersEmissiveFix) {
                        matrixStack.scale(1.01f, 1.01f, 1.01f);
                        this.model.render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, n, o, p, 1.0F);
                        matrixStack.scale(1f, 1f, 1f);
                    } else {
                        model.render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, n, o, p, 1.0F);
                    }
                }
            }
        }
    }

    @Final
    @Shadow
    private HorseEntityModel<HorseEntity> model;

}


