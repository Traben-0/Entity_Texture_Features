package traben.entity_texture_features.mixin.entity.featureRenderers;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.mixin.accessor.ElytraEntityModelAccessor;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Optional;

import static traben.entity_texture_features.ETFClientCommon.ELYTRA_MODELPART_TO_SKIP;
import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.texture_handlers.ETFManager.TEXTURE_MAP_TO_OPPOSITE_ELYTRA;

@Mixin(ElytraFeatureRenderer.class)
public abstract class MixinElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {


    //todo rewrite
    @Final
    @Shadow
    private ElytraEntityModel<T> elytra;
    private ETFTexture thisOtherETFTexture = null;
    private ETFTexture thisETFTexture = null;
    private ModelPart etf$rightWing = null;
    private ModelPart etf$leftWing = null;

    public MixinElytraFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$returnPatchedAlways(Identifier texture) {
        //renderlayers cause issue with elytra emissive even in vanilla so always patch
        thisETFTexture = ETFManager.getETFTexture(texture, null, ETFManager.TextureSource.ENTITY_FEATURE);
        return thisETFTexture.getTextureIdentifier(null, ETFConfigData.enableEmissiveTextures);


        // return texture;
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$checkForAndRenderDifferentWings(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, Identifier identifier, VertexConsumer vertexConsumer) {
        if (ETFConfigData.enableElytra) {
            //System.out.println(identifier.toString());
            if (!TEXTURE_MAP_TO_OPPOSITE_ELYTRA.containsKey(identifier)) {
                //first time check other texture exists and put null if not

                Identifier otherWingIdentifier = new Identifier(identifier.toString().replace(".png", "_left.png"));
                //Optional<Resource> otherWing = ;
                //Optional<Resource> thisWing = ;
                if (ETFUtils2.isExistingResource(otherWingIdentifier) && ETFUtils2.isExistingResource(identifier)) {
                    try {
                        String otherName = MinecraftClient.getInstance().getResourceManager().getResource(otherWingIdentifier).getResourcePackName();
                        String thisName = MinecraftClient.getInstance().getResourceManager().getResource(identifier).getResourcePackName();
                        //ObjectSet<String> set = new ObjectOpenHashSet<>();
                        //set.add(thisName);
                        //set.add(otherName);
                        if (otherName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{thisName, otherName}))) {

                            TEXTURE_MAP_TO_OPPOSITE_ELYTRA.put(identifier, new ETFTexture(otherWingIdentifier));
                        }
                    }catch(Exception ignored){

                    }
                }
                TEXTURE_MAP_TO_OPPOSITE_ELYTRA.putIfAbsent(identifier, null);
            }
            if (TEXTURE_MAP_TO_OPPOSITE_ELYTRA.containsKey(identifier)) {
                if (TEXTURE_MAP_TO_OPPOSITE_ELYTRA.get(identifier) != null) {
                    thisETFTexture = ETFManager.getETFTexture(identifier, null, ETFManager.TextureSource.ENTITY_FEATURE);
                    //remove one wing from vanilla render and render second
                    thisOtherETFTexture = TEXTURE_MAP_TO_OPPOSITE_ELYTRA.get(identifier);
                    ImmutableList<ModelPart> wingParts = (ImmutableList<ModelPart>) ((ElytraEntityModelAccessor) elytra).callGetBodyParts();
                    //0=left  1=right
                    etf$leftWing = wingParts.get(0);
                    etf$rightWing = wingParts.get(1);

                    ELYTRA_MODELPART_TO_SKIP.add(etf$leftWing);




                    ELYTRA_MODELPART_TO_SKIP.add(etf$rightWing);
                    VertexConsumer vertexConsumerOther = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(thisOtherETFTexture.getTextureIdentifier(null, ETFConfigData.enableEmissiveTextures)), false, itemStack.hasGlint());
                    this.elytra.render(matrixStack, vertexConsumerOther, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    ELYTRA_MODELPART_TO_SKIP.remove(etf$rightWing);
                    ELYTRA_MODELPART_TO_SKIP.add(etf$leftWing);


                }//else do nothing
            }
        }
    }


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, Identifier identifier, VertexConsumer vertexConsumer) {

        if (ETFConfigData.enableElytra && ETFConfigData.enableEmissiveTextures && thisETFTexture != null) {
            Identifier emissive = thisETFTexture.getEmissiveIdentifierOfCurrentState();
            if(emissive != null) {
                VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive));
                if (!ELYTRA_MODELPART_TO_SKIP.isEmpty()) {

                    //left is invis already
                    //thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, elytra, ETFManager.EmissiveRenderModes.DULL);
                    elytra.render(matrixStack, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);

                    //etf$leftWing.hidden = etf$vanillaVisibility;
                    //etf$rightWing.hidden = true;
                    ELYTRA_MODELPART_TO_SKIP.remove(etf$leftWing);
                    ELYTRA_MODELPART_TO_SKIP.add(etf$rightWing);
                    //thisOtherETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, elytra, ETFManager.EmissiveRenderModes.DULL);
                    elytra.render(matrixStack, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);

                   // etf$rightWing.hidden = etf$vanillaVisibility;
                    //etf$vanillaVisibility = null;
                    ELYTRA_MODELPART_TO_SKIP.remove(etf$rightWing);
                } else {
                    //easy vanilla
                    //thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, elytra, ETFManager.EmissiveRenderModes.DULL);

                    elytra.render(matrixStack, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
                }
            }
        }
    }
}


