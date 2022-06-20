package traben.entity_texture_features.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.config.ModMenuConfigManager;
import com.terraformersmc.modmenu.gui.ModMenuOptionsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import traben.entity_texture_features.client.utils.ETFUtils;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ETFModMenu implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
            ETFConfigScreen configGUI = new ETFConfigScreen();
            return parent -> configGUI.getConfigScreen(parent, MinecraftClient.getInstance().world != null);
        } catch (Exception | NoClassDefFoundError e) {
            //I definitely didn't catch an error, you saw nothing...
            ETFUtils.logError("Mod settings cannot be edited in Mod Menu without cloth config");
            return null ;
        }

    }

}