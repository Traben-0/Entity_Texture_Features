package traben.entity_texture_features.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public abstract class UScreen {

    //#if MC >= 26.2
    //$$ public static Screen currentScreen() { return Minecraft.getInstance().gui.screen(); }
    //$$ public static void setScreen(Screen screen) { Minecraft.getInstance().gui.setScreen(screen); }
    //#else
    public static Screen currentScreen() { return Minecraft.getInstance().screen; }
    public static void setScreen(Screen screen) { Minecraft.getInstance().setScreen(screen); }
    //#endif
}
