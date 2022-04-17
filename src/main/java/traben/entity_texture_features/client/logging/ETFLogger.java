package traben.entity_texture_features.client.logging;

import net.minecraft.MinecraftVersion;

// We need to make our own logging abstraction over log4j and slf4j because we support 1.18 / 1.18.1 and 1.18.2
public interface ETFLogger {
    void info(Object obj);
    void warn(Object obj);
    void error(Object obj);

    static ETFLogger create() {
        if (MinecraftVersion.CURRENT.getName().equals("1.18") || MinecraftVersion.CURRENT.getName().equals("1.18.1")) {
            return new Log4jETFLogger();
        } else {
            return new Slf4jETFLogger();
        }
    }
}
