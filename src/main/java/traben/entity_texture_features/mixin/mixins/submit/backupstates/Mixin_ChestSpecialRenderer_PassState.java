package traben.entity_texture_features.mixin.mixins.submit.backupstates;

//#if MC >=12109

import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.ETFSubmitData;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(ChestSpecialRenderer.class)
public class Mixin_ChestSpecialRenderer_PassState {

    @Inject(method = "submit", at = @At(value = "HEAD"))
    private static void emf$dummyState(CallbackInfo ci) {
        var state = ETFEntityRenderState.forEntity(
                // TODO do we really need the actual chest type here? this is just so inventory anims can play
                (ETFEntity) new ChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState()));
        ETFState.mount(state);
    }

    @Inject(method = "submit", at = @At(value = "TAIL"))
    private static void emf$reset(CallbackInfo ci) {
        ETFState.unMount();
    }

}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class Mixin_ChestSpecialRenderer_PassState { }
//#endif