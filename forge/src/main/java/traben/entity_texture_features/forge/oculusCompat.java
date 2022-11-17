package traben.entity_texture_features.forge;


import net.irisshaders.iris.api.v0.IrisApi;
import traben.entity_texture_features.ETFVersionDifferenceHandler;


public abstract class oculusCompat {
    public static final boolean IRIS_DETECTED = ETFVersionDifferenceHandler.isThisModLoaded("oculus");

    private static final InternalHandler INTERNAL_HANDLER = IRIS_DETECTED ? new InternalHandlerImpl() : new InternalHandler() {
    };

    public static boolean isShaderPackInUse() {
        return INTERNAL_HANDLER.isShaderPackInUse();
    }

    private interface InternalHandler {
        default boolean isShaderPackInUse() {
            return false;
        }

    }

    private static class InternalHandlerImpl implements InternalHandler {
        @Override
        public boolean isShaderPackInUse() {
            return IrisApi.getInstance().isShaderPackInUse();
        }

    }
}