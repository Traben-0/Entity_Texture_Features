package traben.entity_texture_features.mixin.mixins.submit;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

//#if MC>=12109
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.state.ETFSubmitData;
import traben.entity_texture_features.features.state.ETFSubmitExtension;

//#if MC >= 26.2
//$$ @Mixin(net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit.class)
//#else
@Mixin(net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit.class)
//#endif
public abstract class Mixin_ModelSubmit_AddData implements ETFSubmitExtension {

    @Unique private final @Nullable ETFSubmitData data = new ETFSubmitData();

    @Override
    public @Nullable ETFSubmitData emf$getData() {
        return data;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void emf$initBackupState(CallbackInfo ci) {
        if (data != null) {
            ETFSubmitData.DATA_IN.forEach(entry -> entry.accept(data, (
                    //#if MC >= 26.2
                    //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit
                    //#else
                    net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit
                    //#endif
                    ) (Object) this));
        }
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelSubmit_AddData { }
//#endif
