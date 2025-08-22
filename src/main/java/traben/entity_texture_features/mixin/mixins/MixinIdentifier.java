package traben.entity_texture_features.mixin.mixins;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.HashSet;
import java.util.Set;


@Mixin(ResourceLocation.class)
public abstract class MixinIdentifier {

    private static final Set<String> EXCUSED_ILLEGAL_PATHS = new HashSet<>();


    @Inject(method = "isValidPath", cancellable = true, at = @At("RETURN"), require = 0)
    private static void etf$illegalPathOverride(String path, CallbackInfoReturnable<Boolean> cir) {
        if (ETF.config().getConfig() != null) {
            if (ETF.config().getConfig().illegalPathSupportMode != ETFConfig.IllegalPathMode.None) {
                if (!cir.getReturnValue() && path != null) {

                    if (path.equals("DUMMY") // something uses this and relies on it's failure
                            || path.isBlank()
                    ) return;

                    switch (ETF.config().getConfig().illegalPathSupportMode) {
                        case Entity -> {
                            if ((path.contains("/entity/") || path.contains("optifine/") || path.contains("etf/") || path.contains("emf/"))
                                    && (path.endsWith(".png") || path.endsWith(".properties") || path.endsWith(".mcmeta") || path.endsWith(".jem") || path.endsWith(".jpm"))) {

                                allowPathAndLog(path, cir);
                            }
                        }
                        case All -> allowPathAndLog(path, cir);
                        default -> ETFUtils2.logWarn("this message should not appear #65164");
                    }
                }
            }
        }
    }

    @Unique
    private static void allowPathAndLog(final String path, final CallbackInfoReturnable<Boolean> cir) {
        if (!EXCUSED_ILLEGAL_PATHS.contains(path)) {
            EXCUSED_ILLEGAL_PATHS.add(path);
            ETFUtils2.logWarn(ETF.getTextFromTranslation("config.entity_texture_features.illegal_path_warn").getString() + " [" + path + "]");
        }
        cir.setReturnValue(true);
    }
}


