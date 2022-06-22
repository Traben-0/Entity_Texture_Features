package traben.entity_texture_features.client.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;

// iris compat improved by @Maximum#8760
public class IrisCompat {
    public static final boolean IRIS_DETECTED = FabricLoader.getInstance().isModLoaded("iris");

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