package traben.entity_texture_features.fabric.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;

@Environment(EnvType.CLIENT)
public class ETFModMenu implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ETFConfigScreenMain::new;
    }

}