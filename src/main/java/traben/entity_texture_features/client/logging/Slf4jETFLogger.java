package traben.entity_texture_features.client.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jETFLogger implements ETFLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("Entity Texture Features");

    @Override
    public void info(Object obj) {
        LOGGER.info(obj.toString());
    }

    @Override
    public void warn(Object obj) {
        LOGGER.warn(obj.toString());
    }

    @Override
    public void error(Object obj) {
        LOGGER.error(obj.toString());
    }
}
