package traben.entity_texture_features;

import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

//an api that will remain unchanged for external mod access (primarily puzzle at this time)
public class ETFApi {

    final public static int ETFApiVersion = 2;
    //provides access to the ETF config object to read AND modify its values
    //please be sure to run the save config method below after any changes
    public static ETFConfig getETFConfigObject = ETFClientCommon.ETFConfigData;

    //saves any config changes to file and resets ETF to function with the new settings
    public static void saveETFConfigChangesAndResetETF() {
        ETFUtils2.saveConfig();
        ETFManager.resetInstance();
    }

    //resets ETF in its entirety, ETF will re asses all textures and properties files and recalculate all variants
    public static void resetETF() {
        ETFManager.resetInstance();
    }

    //for now only puzzle support has been considered
    //please notify the dev if you would like something added here
}
