package traben.entity_texture_features.compat;

import traben.entity_texture_features.ETF;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Objects;

public abstract class IrisHandler {

    public abstract boolean isShaderActive();

    private static IrisHandler instance;
    public static IrisHandler getInstance() {
        //#if IRIS
        if (instance == null) {
            try{
                instance = new IrisHandlerImpl();
            } catch (Throwable e) {
                ETFUtils2.logMessage("ETF did not find the Iris API, disabling shader detection");
        //#endif
                instance = new IrisHandler() {
                    @Override
                    public boolean isShaderActive() {
                        return false;
                    }
                };
        //#if IRIS
            }
        }
        //#endif
        return instance;
    }
    //#if IRIS
    private static class IrisHandlerImpl extends IrisHandler {

        IrisHandlerImpl(){
            if (!ETF.IRIS_DETECTED) throw new RuntimeException("Iris not detected, cannot use this class");
            Objects.requireNonNull(net.irisshaders.iris.api.v0.IrisApi.getInstance()) ;
        }
        @Override
        public boolean isShaderActive() {
            return net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse();
        }
    }
    //#endif
}
