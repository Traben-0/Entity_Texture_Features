package traben.entity_texture_features.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.utils.ETFUtils2;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(Identifier.class)
public abstract class MixinIdentifier {


    @Inject(method = "isPathValid(Ljava/lang/String;)Z", cancellable = true, at = @At("RETURN"))
    private static void etf$illegalPathOverride(String path, CallbackInfoReturnable<Boolean> cir) {
        if (ETFConfigData != null) {
            if (ETFConfigData.illegalPathSupportMode != ETFConfig.IllegalPathMode.None) {
                if (!cir.getReturnValue() && path != null) {


                    switch (ETFConfigData.illegalPathSupportMode) {
                        case Entity -> {
                            if ((path.contains("/entity/") || path.contains("/optifine/") || path.contains("/etf/"))
                                    && (path.endsWith(".png") || path.endsWith(".properties") || path.endsWith(".mcmeta"))) {
                                ETFUtils2.logWarn(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.illegal_path_warn").getString()
                                        + " [" + path + "]");

                                // String filename =path.replace(".png", "");
                                // String[] split = filename.split("/");
                                //ETFManager.getInstance().EXCUSED_ILLEGAL_PATHS.add(split[split.length-1]);

                                cir.setReturnValue(true);
                            }
                        }
                        case All -> {
                            ETFUtils2.logWarn(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.illegal_path_warn").getString()
                                    + " [" + path + "]");
                            if (!path.isBlank())
                                cir.setReturnValue(true);
                        }
                        default -> ETFUtils2.logWarn("this message should not appear #65164");
                    }
                }
            }
        }
    }
}


