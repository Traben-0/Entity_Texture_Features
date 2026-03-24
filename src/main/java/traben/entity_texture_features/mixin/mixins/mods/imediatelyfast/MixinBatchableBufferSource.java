package traben.entity_texture_features.mixin.mixins.mods.imediatelyfast;

//#if IMMEDIATELYFAST
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.compat.SodiumGetBufferInjector;
import traben.entity_texture_features.features.ETFRenderContext;

@Pseudo
@Mixin(value = BatchableBufferSource.class, priority = 800)
public class MixinBatchableBufferSource {

    @ModifyVariable(
            method = "getBuffer",
            at = @At(value = "HEAD"),
            ordinal = 0, argsOnly = true, require = 0)
    private RenderType etf$modifyRenderLayer2(RenderType value) {
        return ETFRenderContext.modifyRenderLayerIfRequired(value);
    }

    @Inject(
            method = "getBuffer",
            at = @At(value = "RETURN"), require = 0)
    private void etf$injectIntoGetBufferReturn(RenderType renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!ETFRenderContext.isCurrentlyRenderingEntity()) return; // faster cancel

        var returned = cir.getReturnValue();
        ETFRenderContext.insertETFDataIntoVertexConsumer((MultiBufferSource) this, renderLayer, returned);

        //todo is this required with immedeately fast?
        // quarantined class to contain all sodium interaction
        // sodium ExtendedBufferBuilder classes contain a delegate that must instead have the above data passed into
        SodiumGetBufferInjector.inject((MultiBufferSource) this, renderLayer, returned);
    }

}
//#else
//$$ import net.minecraft.client.Minecraft;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class MixinBatchableBufferSource {}
//#endif