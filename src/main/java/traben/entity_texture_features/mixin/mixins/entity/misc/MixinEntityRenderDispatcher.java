package traben.entity_texture_features.mixin.mixins.entity.misc;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
    //#if MC >= 12103
        //#if MC>= 12109
        @Inject(method = "submit", at = @At(value = "HEAD"))
        //#elseif MC>= 12105
        //$$ @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
        //$$         at = @At(value = "HEAD"))
        //#else
        //$$ @Inject(method = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
        //$$         at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
        //#endif
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState>
            void etf$grabContext(final CallbackInfo ci, @SuppressWarnings("LocalMayBeArgsOnly") @Local S state, @Share("state_etf") LocalRef<ETFEntityRenderState> stateRef) {
        var etf = ((HoldsETFRenderState) state).etf$getState();
            if (etf != null) {
                ETFState.mount(etf);
            }
            stateRef.set(etf);
    }
    //#else
    //$$ @Inject(method = "render",
    //$$     at = @At(value = "HEAD"))
    //$$ private <E extends net.minecraft.world.entity.Entity> void etf$grabContext(CallbackInfo ci, @Local(argsOnly = true) E entity, @Share("state_etf") LocalRef<ETFEntityRenderState> stateRef) {
    //$$     var etf = ETFEntityRenderState.forEntity((ETFEntity) entity);
    //$$     if (etf != null) {
    //$$         ETFState.mount(etf);
    //$$     }
    //$$     stateRef.set(etf);
    //$$ }
    //#endif

    @Inject(method =
            //#if MC>= 12109
            "submit",
            //#elseif MC >= 12103
            //$$ "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            //#else
            //$$     "render",
            //#endif
            at = @At(value = "RETURN"))
    private void etf$clearContext(CallbackInfo ci, @Share("state_etf") LocalRef<ETFEntityRenderState> stateRef) {
        if (stateRef.get() != null) {
            ETFState.stackVerify(stateRef.get());
            ETFState.unMount();
        }
    }

}
