package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;

//#if MC >= 12103
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {
//#else
//$$ import net.minecraft.client.renderer.entity.RenderLayerParent;
//$$ @Mixin(LivingEntityRenderer.class)
//$$ public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
//#endif


    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);

    }

    //#if MC >= 12109
    private static final String RENDER = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V";
    //#elseif MC >= 12103
    //$$ private static final String RENDER = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";
    //#else
    //$$ private static final String RENDER = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";
    //#endif

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void etf$markFeatures(CallbackInfo ci) {
        ETFState.pushRenderLayerModifyState(true);
        ETFState.isRenderingFeatures = true;
    }

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    private void etf$markFeaturesEnd(CallbackInfo ci) {
        ETFState.isRenderingFeatures = false;
        ETFState.popRenderLayerModifyState();
    }

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void emf$grabEntity(CallbackInfo ci, @Share("stateCaptureEntity") LocalRef<ETFEntityRenderState> stateCaptureEntity) {
        stateCaptureEntity.set(ETFState.state());
    }

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private void emf$eachFeatureLoop(CallbackInfo ci, @Share("stateCaptureEntity") LocalRef<ETFEntityRenderState> stateCaptureEntity) {
        // Assert state each call in case things got cancelled and couldn't be undone
        if (stateCaptureEntity.get() != null) {
            ETFState.stackVerify(stateCaptureEntity.get());
        }
    }


    @Inject(method = RENDER, at = @At("TAIL"))
    private void emf$postRender(CallbackInfo ci, @Share("stateCaptureEntity") LocalRef<ETFEntityRenderState> stateCaptureEntity) {
        if (stateCaptureEntity.get() != null) {
            ETFState.stackVerify(stateCaptureEntity.get());
        }
    }
}


