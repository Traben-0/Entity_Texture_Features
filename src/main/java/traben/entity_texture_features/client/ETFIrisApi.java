package traben.entity_texture_features.client;

import net.irisshaders.iris.api.v0.IrisApi;

public class ETFIrisApi {
    //class is only called if Iris is present to prevent possible issues
    public static boolean isShaderOn() {
        return IrisApi.getInstance().isShaderPackInUse();
    }

}
