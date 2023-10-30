package traben.entity_texture_features.mixin.entity.renderer.feature;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.mixin.accessor.ElytraEntityModelAccessor;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Optional;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ElytraFeatureRenderer.class)
public abstract class MixinElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    //the sneaky 3 way boolean
    @Unique
    boolean etf$twoTextures = false;

    @Final
    @Shadow
    private ElytraEntityModel<T> elytra;
    @Unique
    private ETFTexture entity_texture_features$thisOtherETFTexture = null;
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;
    @Unique
    private ModelPart etf$rightWing = null;
    @Unique
    private ModelPart etf$leftWing = null;

    @SuppressWarnings("unused")
    public MixinElytraFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$returnPatchedAlways(Identifier texture) {
        //renderlayers cause issue with elytra emissive even in vanilla so always patch
        entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(texture, null, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveElytra);
        return entity_texture_features$thisETFTexture.getTextureIdentifier(null, ETFConfigData.enableEmissiveTextures);


        // return texture;
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void hideWing(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, Identifier identifier) {
        //hide left wing
        if (ETFConfigData.enableElytra) {
            //System.out.println(identifier.toString());
            if (!ETFManager.getInstance().TEXTURE_MAP_TO_OPPOSITE_ELYTRA.containsKey(identifier) && ETFManager.getInstance().TEXTURE_MAP_TO_OPPOSITE_ELYTRA.get(identifier) != null) {
                ((ImmutableList<ModelPart>) ((ElytraEntityModelAccessor) elytra).callGetBodyParts()).get(0).hidden = true;
            }
        }

    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, Identifier identifier, VertexConsumer vertexConsumer) {
        if (ETFConfigData.enableElytra) {
            //System.out.println(identifier.toString());
            if (!ETFManager.getInstance().TEXTURE_MAP_TO_OPPOSITE_ELYTRA.containsKey(identifier)) {
                //first time check other texture exists and put null if not

                Identifier otherWingIdentifier = new Identifier(identifier.toString().replace(".png", "_left.png"));
                Optional<Resource> otherWing = MinecraftClient.getInstance().getResourceManager().getResource(otherWingIdentifier);
                Optional<Resource> thisWing = MinecraftClient.getInstance().getResourceManager().getResource(identifier);
                if (otherWing.isPresent() && thisWing.isPresent()) {
                    String otherName = otherWing.get().getResourcePackName();
                    String thisName = thisWing.get().getResourcePackName();
                    //ObjectSet<String> set = new ObjectOpenHashSet<>();
                    //set.add(thisName);
                    //set.add(otherName);
                    if (otherName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{thisName, otherName}))) {
                        ETFManager.getInstance().TEXTURE_MAP_TO_OPPOSITE_ELYTRA.put(identifier, new ETFTexture(otherWingIdentifier, ETFConfigData.removePixelsUnderEmissiveElytra));
                    }
                }
                ETFManager.getInstance().TEXTURE_MAP_TO_OPPOSITE_ELYTRA.putIfAbsent(identifier, null);
            }
            if (ETFManager.getInstance().TEXTURE_MAP_TO_OPPOSITE_ELYTRA.containsKey(identifier)) {
                if (ETFManager.getInstance().TEXTURE_MAP_TO_OPPOSITE_ELYTRA.get(identifier) != null) {
                    entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(identifier, null, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveElytra);
                    //remove one wing from vanilla render and render second
                    entity_texture_features$thisOtherETFTexture = ETFManager.getInstance().TEXTURE_MAP_TO_OPPOSITE_ELYTRA.get(identifier);
                    ImmutableList<ModelPart> wingParts = (ImmutableList<ModelPart>) ((ElytraEntityModelAccessor) elytra).callGetBodyParts();
                    //0=left  1=right
                    etf$leftWing = wingParts.get(0);
                    etf$rightWing = wingParts.get(1);

                    etf$leftWing.hidden = false;// etf$leftWing.hidden;
                    etf$rightWing.hidden = true;
                    VertexConsumer vertexConsumerOther = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(entity_texture_features$thisOtherETFTexture.getTextureIdentifier(null, ETFConfigData.enableEmissiveTextures)), false, itemStack.hasGlint());
                    elytra.render(matrixStack, vertexConsumerOther, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    etf$rightWing.hidden = false;// etf$vanillaVisibility;
                    etf$leftWing.hidden = true;

                    etf$twoTextures = true;
                }//else do nothing
            }
        }
        if (ETFConfigData.enableElytra && ETFConfigData.enableEmissiveTextures && entity_texture_features$thisETFTexture != null) {
            Identifier emissive = entity_texture_features$thisETFTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive));
                if (etf$twoTextures) {
                    //left is invis already
                    //thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, elytra, ETFManager.EmissiveRenderModes.DULL);
                    elytra.render(matrixStack, textureVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);

                    etf$leftWing.hidden = false;//etf$vanillaVisibility;
                    etf$rightWing.hidden = true;
                    //thisOtherETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, elytra, ETFManager.EmissiveRenderModes.DULL);
                    if (entity_texture_features$thisOtherETFTexture.isEmissive()) {
                        VertexConsumer textureVertLeft = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(entity_texture_features$thisOtherETFTexture.getEmissiveIdentifierOfCurrentState()));
                        elytra.render(matrixStack, textureVertLeft, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
                    }
                    etf$rightWing.hidden = false;//etf$vanillaVisibility;
                    etf$twoTextures = false;
                    entity_texture_features$thisOtherETFTexture = null;
                } else {
                    //easy vanilla
                    //thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, elytra, ETFManager.EmissiveRenderModes.DULL);

                    elytra.render(matrixStack, textureVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
                }
            }
        }
    }
}


