package traben.entity_texture_features;

import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

//an api that will remain unchanged for external mod access (primarily puzzle at this time)
public class ETFApi {

    //provides access to the ETF config object to read AND modify it's values
    //please be sure to run the save config method below after any changes
    public static ETFConfig getETFConfigObject = ETFClientCommon.ETFConfigData;
    final public static int ETFApiVersion = 1;

    //saves any config changes to file and resets ETF to function with the new settings
    public static void saveETFConfigChangesAndResetETF() {
        ETFUtils2.saveConfig();
        ETFManager.reset();
    }

    //for now only puzzle support has been considered
    //please notify the dev if you would like something added here
}
