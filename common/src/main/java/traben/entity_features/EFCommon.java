package traben.entity_features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EFCommon {

    private static final Logger LOGGER = LoggerFactory.getLogger("Entity Features");
    //sets whether to display config load warning in gui
    public static boolean configHadLoadError = false;

    public static void log(String message){
        LOGGER.info("[EF] " + message);
    }

    public static void logError(String message){
        LOGGER.error("[EF] " + message);
    }

    public static void logWarn(String message){
        LOGGER.warn("[EF] " + message);
    }
}
