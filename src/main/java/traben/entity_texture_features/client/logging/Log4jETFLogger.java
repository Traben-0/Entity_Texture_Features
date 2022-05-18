package traben.entity_texture_features.client.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// logging files provided by @Maximum#8760
public class Log4jETFLogger implements ETFLogger {
    private static final Logger LOGGER = LogManager.getLogger("Entity Texture Features");

    @Override
    public void info(Object obj) {
        LOGGER.info(obj);
    }

    @Override
    public void warn(Object obj) {
        LOGGER.warn(obj);
    }

    @Override
    public void error(Object obj) {
        LOGGER.error(obj);
    }
}
