package traben.entity_texture_features.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;

@Environment(EnvType.CLIENT)
public class ETFModMenu implements ModMenuApi {



        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
                try {
                        ETFConfigScreen configGUI = new ETFConfigScreen();
                        ConfigScreenFactory<?> screen = parent -> configGUI.getConfigScreen(parent, MinecraftClient.getInstance().world != null);
                        return screen;
                }
                catch(NoClassDefFoundError e) {
                        //I definitely didn't catch an error, you saw nothing...
                        LogManager.getLogger().warn("[Entity Texture Features]: Mod settings cannot be edited in Mod Menu without cloth config");
                        return null;
                }

        }
    }