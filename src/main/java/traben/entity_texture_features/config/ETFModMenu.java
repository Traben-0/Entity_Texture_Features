package traben.entity_texture_features.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import traben.entity_texture_features.client.ETFUtils;

@Environment(EnvType.CLIENT)
public class ETFModMenu implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
            ETFConfigScreen configGUI = new ETFConfigScreen();
            return parent -> configGUI.createConfigScreen(parent, MinecraftClient.getInstance().world != null);
        } catch (NoClassDefFoundError e) {
            //I definitely didn't catch an error, you saw nothing...
            ETFUtils.modWarn("Mod settings cannot be edited in Mod Menu without cloth config", false);
            return null;
        }

    }
}