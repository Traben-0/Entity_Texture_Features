package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;


@Mixin(IllusionerRenderer.class)
public abstract class MixinIllusionerRenderer {

    @Unique
    private ETFEntityRenderState etf$heldEntity = null;

    @Inject(method =
            //#if MC >= 12103
            "render(Lnet/minecraft/client/renderer/entity/state/IllusionerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/monster/Illusioner;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
            at = @At(value = "HEAD"))
    private void etf$start(CallbackInfo ci
        //#if MC>= 12103
               , @Local(argsOnly = true) net.minecraft.client.renderer.entity.state.IllusionerRenderState state
        ) { ETFRenderContext.setCurrentEntity(((HoldsETFRenderState) state).etf$getState());
        //#else
        //$$     , @Local(argsOnly = true) net.minecraft.world.entity.monster.Illusioner entity
        //$$ ) { ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity) entity));
        //#endif
    }

    @Inject(method =
            //#if MC >= 12103
            "render(Lnet/minecraft/client/renderer/entity/state/IllusionerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/monster/Illusioner;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
            at = @At(value = "INVOKE", target =
                    //#if MC >= 12103
                    "Lnet/minecraft/client/renderer/entity/IllagerRenderer;render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    //#elseif MC >= 12100
                    //$$ "Lnet/minecraft/client/renderer/entity/IllagerRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    //#else
                    //$$ "Lnet/minecraft/client/renderer/entity/IllagerRenderer;render(Lnet/minecraft/world/entity/Mob;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    //#endif
                    shift = At.Shift.BEFORE))
    private void etf$loop(CallbackInfo ci) {
        //assert main entity each loop
        ETFRenderContext.setCurrentEntity(etf$heldEntity);
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }
}


