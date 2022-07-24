package traben.entity_texture_features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.utils.ETFUtils2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ETFClientCommon {

    public static final File CONFIG_DIR = ETFVersionDifferenceHandler.getConfigDir();
    public static final String MOD_ID = "entity_texture_features";
    //logging object
    public final static Logger LOGGER = ETFVersionDifferenceHandler.getLogger();
    //config object
    public static ETFConfig ETFConfigData = new ETFConfig();


    public static void start() {
        LOGGER.info("Loading 1.19.84");
        etf$loadConfig();
    }

    // config code based on bedrockify & actually unbreaking fabric config code
    // https://github.com/juancarloscp52/BedrockIfy/blob/1.17.x/src/main/java/me/juancarloscp52/bedrockify/Bedrockify.java
    // https://github.com/wutdahack/ActuallyUnbreakingFabric/blob/1.18.1/src/main/java/wutdahack/actuallyunbreaking/ActuallyUnbreaking.java
    public static void etf$loadConfig() {
        File config = new File(CONFIG_DIR, "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.exists()) {
            try {
                FileReader fileReader = new FileReader(config);
                ETFConfigData = gson.fromJson(fileReader, ETFConfig.class);
                fileReader.close();
                ETFUtils2.saveConfig();
            } catch (IOException e) {
                ETFUtils2.logMessage("Config could not be loaded, using defaults", false);
                ETFConfigData = new ETFConfig();
                ETFUtils2.saveConfig();
            }
        } else {
            ETFConfigData = new ETFConfig();
            ETFUtils2.saveConfig();
        }
        if(ETFConfigData == null){
            ETFUtils2.logMessage("Config was null, using defaults", false);
            ETFConfigData = new ETFConfig();
        }
    }

}
