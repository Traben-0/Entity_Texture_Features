package traben.entity_texture_features.mixin.mixins.entity.renderer.feature;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

//#if MC >= 12103
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.renderer.entity.state.WardenRenderState;
import net.minecraft.client.model.EntityModel;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;

import java.util.List;

@Mixin(WardenModel.class)
public abstract class MixinWardenExtraTextureParts extends EntityModel<WardenRenderState> {

    @SuppressWarnings("unused")
    protected MixinWardenExtraTextureParts() {
        super(null);
    }

    //#if MC>=12109
    @WrapOperation(method = {
            "createTendrilsLayer",
            "createHeartLayer",
            "createBioluminescentLayer",
            "createPulsatingSpotsLayer"
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/builders/LayerDefinition;apply(Lnet/minecraft/client/model/geom/builders/MeshTransformer;)Lnet/minecraft/client/model/geom/builders/LayerDefinition;"))
    private static LayerDefinition etf$modifyParts1(final LayerDefinition instance, final MeshTransformer meshTransformer, final Operation<LayerDefinition> original) {
        if (ETF.config().getConfig().enableFullBodyWardenTextures) {
            return instance; // cancel call and return original unmodified instance
        }
        return original.call(instance, meshTransformer);
    }
    //#else
    //$$
    //$$ @Shadow @Final protected ModelPart bone;
    //$$ @Shadow @Final protected ModelPart body;
    //$$ @Shadow @Final protected ModelPart head;
    //$$ @Shadow @Final protected ModelPart rightArm;
    //$$ @Shadow @Final protected ModelPart rightLeg;
    //$$ @Shadow @Final protected ModelPart rightRibcage;
    //$$ @Shadow @Final protected ModelPart rightTendril;
    //$$ @Shadow @Final protected ModelPart leftArm;
    //$$ @Shadow @Final protected ModelPart leftLeg;
    //$$ @Shadow @Final protected ModelPart leftRibcage;
    //$$ @Shadow @Final protected ModelPart leftTendril;
    //$$
    //$$
    //$$ @Inject(method = {
    //$$         "getPulsatingSpotsLayerModelParts",
    //$$         "getTendrilsLayerModelParts",
    //$$         "getBioluminescentLayerModelParts",
    //$$         "getHeartLayerModelParts"
    //$$ }, at = @At(value = "RETURN"), cancellable = true)
    //$$ private void etf$modifyParts1(final CallbackInfoReturnable<List<ModelPart>> cir) {
    //$$     if (ETF.config().getConfig().enableFullBodyWardenTextures) {
    //$$         cir.setReturnValue(List.of(bone, body, head, rightArm,rightLeg,rightRibcage,rightTendril,leftArm, leftLeg, leftRibcage, leftTendril));
    //$$     }
    //$$ }
    //#endif

}
//#else
//$$ import net.minecraft.client.model.WardenModel;
//$$ import net.minecraft.world.entity.monster.warden.Warden;
//$$ import net.minecraft.client.renderer.entity.layers.WardenEmissiveLayer;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import net.minecraft.client.renderer.entity.layers.RenderLayer;
//$$ import traben.entity_texture_features.ETF;
//$$ import traben.entity_texture_features.utils.ETFUtils2;
//$$
//$$ @Mixin(WardenEmissiveLayer.class)
//$$ public abstract class MixinWardenExtraTextureParts<T extends Warden, M extends WardenModel<T>> extends RenderLayer<T, M> {
//$$     @SuppressWarnings("unused")
//$$     public MixinWardenExtraTextureParts() {
//$$         super(null);
//$$     }
//$$
//$$         @Unique
//$$     private static final ResourceLocation VANILLA_TEXTURE = ETFUtils2.res("textures/entity/warden/warden.png");
//$$     @Shadow
//$$     @Final
//$$     private ResourceLocation texture;
//$$
//$$
//$$
//$$
//$$     @Shadow
//$$     protected abstract void resetDrawForAllParts();
//$$
//$$     @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/monster/warden/Warden;FFFFFF)V",
//$$             at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/WardenEmissiveLayer;onlyDrawSelectedParts()V",
//$$                     shift = At.Shift.AFTER))
//$$     private void etf$preventHiding(CallbackInfo ci) {
//$$         if (ETF.config().getConfig().enableFullBodyWardenTextures && !VANILLA_TEXTURE.equals(texture)) {
//$$             resetDrawForAllParts();
//$$         }
//$$     }
//$$ }
//#endif





