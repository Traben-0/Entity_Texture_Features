package traben.entity_texture_features.mixin.client.entity.featureRenderers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.utils.ETFUtils;
import traben.entity_texture_features.mixin.client.accessor.ElytraEntityModelAccessor;

import static traben.entity_texture_features.client.ETFClient.*;

@Mixin(ElytraFeatureRenderer.class)
public abstract class MixinElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public MixinElytraFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Final
    @Shadow
    private ElytraEntityModel<T> elytra;

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$returnPatchedAlways(Identifier texture) {
        //renderlayers cause issue with elytra emissive even in vanilla so always patch
        if (ETFConfigData.enableEmissiveTextures && PATH_EMISSIVE_TEXTURE_IDENTIFIER.containsKey(texture.toString())) {
            if (PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(texture.toString()) != null) {
                if (!PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(texture.toString())) {
                    ETFUtils.applyETFEmissivePatchingToTexture(texture.toString());
                }
                if (PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(texture.toString())) {
                    if (PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.getBoolean(texture.toString())) {
                        return new Identifier(texture.toString() + "etf_iris_patched_file.png");
                    }
                }
            }
        }

        return texture;
    }


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$checkForAndRenderDifferentWings(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, Identifier identifier) {
        if (ETFConfigData.enableElytra ) {
            //System.out.println(identifier.toString());
            if(!IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER.containsKey(identifier)) {
                //first time check other texture exists and put null if not
                Identifier otherWingIdentifier = new Identifier(identifier.toString().replace(".png", "_other.png"));
                if (ETFUtils.isExistingFileAndSameOrHigherResourcepackAs(otherWingIdentifier, identifier, true)) {
                    IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER.put(identifier, otherWingIdentifier);
                } else {
                    IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER.put(identifier, null);
                }
            }
            if(IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER.containsKey(identifier)){
                if(IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER.get(identifier) != null) {
                    //remove one wing from vanilla render and render second
                    ImmutableList<ModelPart> wingParts = (ImmutableList<ModelPart>) ((ElytraEntityModelAccessor) elytra).callGetBodyParts();
                    //0=left  1=right
                    etf$leftWing = wingParts.get(0);
                    etf$rightWing = wingParts.get(1);
                    //etf$vanillaVisibility = etf$leftWing.hidden;

                    ELYTRA_MODELPART_TO_SKIP.add(etf$leftWing);

                }//else do nothing
            }
        }
    }

    private ModelPart etf$rightWing = null;
    private ModelPart etf$leftWing = null;


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, Identifier identifier, VertexConsumer vertexConsumer) {
//        if(etf$vanillaVisibility != null){
//
//            etf$leftWing.visible = etf$vanillaVisibility;
//        }
        if (ETFConfigData.enableElytra && ETFConfigData.enableEmissiveTextures) {
            if(etf$rightWing !=null && etf$leftWing != null){
                //only the case if IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER not null no need to check
                //ETFUtils.generalEmissiveRenderPart(matrixStack, vertexConsumerProvider, IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER.get(identifier), etf$leftWing,false);
                //ETFUtils.generalEmissiveRenderPart(matrixStack, vertexConsumerProvider, identifier, etf$rightWing,false);



                //left is invis already
                ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, etf$returnPatchedAlways(identifier), elytra);
                ELYTRA_MODELPART_TO_SKIP.remove(etf$leftWing);
                ELYTRA_MODELPART_TO_SKIP.add(etf$rightWing);
                VertexConsumer vertexConsumerOther = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(etf$returnPatchedAlways(IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER.get(identifier))), false, itemStack.hasGlint());
                this.elytra.render(matrixStack, vertexConsumerOther, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, etf$returnPatchedAlways(IDENTIFIER_OPPOSITE_ELYTRA_IDENTIFIER.get(identifier)), elytra);
                ELYTRA_MODELPART_TO_SKIP.remove(etf$rightWing);
                etf$rightWing = null;
                etf$leftWing = null;
            }else {
                //easy vanilla
                ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, etf$returnPatchedAlways(identifier), elytra);
            }
        }
    }
}


