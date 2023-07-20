package traben.entity_texture_features.fabric;

import net.irisshaders.iris.api.v0.IrisApi;
import traben.entity_texture_features.ETFClientCommon;

// iris compat improved by @Maximum#8760
public abstract class IrisCompat {

    private static final InternalHandler INTERNAL_HANDLER = ETFClientCommon.IRIS_DETECTED ? new InternalHandlerImpl() : new InternalHandler() {
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