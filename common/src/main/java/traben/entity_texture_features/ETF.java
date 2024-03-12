package traben.entity_texture_features;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.LightmapTextureManager;
import org.slf4j.Logger;
import traben.entity_texture_features.config.screens.ETFConfigScreenWarnings;
import traben.tconfig.TConfigHandler;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.utils.ETFUtils2;

import java.io.File;
import java.util.Random;
import java.util.Set;


public class ETF {

    public static final File CONFIG_DIR = ETFVersionDifferenceHandler.getConfigDir();
    public static final String MOD_ID = "entity_texture_features";
    //logging object
    public final static Logger LOGGER = ETFVersionDifferenceHandler.getLogger();


    public static final int EMISSIVE_FEATURE_LIGHT_VALUE = LightmapTextureManager.MAX_LIGHT_COORDINATE + 2;
    public static TConfigHandler<ETFConfigScreenWarnings.WarningConfig> warningConfigHandler = null;
    public static boolean IRIS_DETECTED = false;


    public static boolean SKIN_LAYERS_DETECTED = false;

    private static TConfigHandler<ETFConfig> CONFIG = null;
    public static Set<TConfigHandler<?>> configHandlers = null;

    public static TConfigHandler<ETFConfig> config() {
        if (CONFIG == null) {
            CONFIG = new TConfigHandler<>(ETFConfig::new, MOD_ID, "ETF");
            registerConfigHandler(CONFIG);
        }
        return CONFIG;
    }


    public static void start() {
        //check only once
        SKIN_LAYERS_DETECTED = ETFVersionDifferenceHandler.isThisModLoaded("skinlayers3d");
        IRIS_DETECTED = ETFVersionDifferenceHandler.isThisModLoaded("iris") || ETFVersionDifferenceHandler.isThisModLoaded("oculus");

        LOGGER.info("Loading Entity Texture Features, " + randomQuip());

        warningConfigHandler = new TConfigHandler<>(ETFConfigScreenWarnings.WarningConfig::new, "ef_warnings.json", "EF");
        registerConfigHandler(warningConfigHandler);

        ETFUtils2.checkModCompatibility();

        ETFConfigWarnings.registerConfigWarning(
                //figura
                new ETFConfigWarning.Simple(
                        "figura",
                        "figura",
                        "config." + ETF.MOD_ID + ".warn.figura.text.1",
                        "config." + ETF.MOD_ID + ".warn.figura.text.2",
                        () -> {
                            CONFIG.getConfig().skinFeaturesEnabled = false;
                            CONFIG.saveToFile();
                        }),
                //EBE
                new ETFConfigWarning.Simple(
                        "enhancedblockentities",
                        "enhancedblockentities",
                        "config." + ETF.MOD_ID + ".warn.ebe.text.1",
                        "config." + ETF.MOD_ID + ".warn.ebe.text.2",
                        null),
                //quark
                new ETFConfigWarning.Simple(
                        "quark",
                        "quark",
                        "config." + ETF.MOD_ID + ".warn.quark.text.3",
                        "config." + ETF.MOD_ID + ".warn.quark.text.4",
                        null),
                //iris and 3d skin layers trim warning
                new ETFConfigWarning.Simple(
                        "iris & 3d skin layers",
                        () -> ETF.IRIS_DETECTED && ETF.SKIN_LAYERS_DETECTED,
                        "config." + ETF.MOD_ID + ".warn.iris_3d.text.1",
                        "config." + ETF.MOD_ID + ".warn.iris_3d.text.2",
                        null),
                //no CEM mod, recommend EMF
                new ETFConfigWarning.Simple(
                        "emf",
                        () -> !ETFVersionDifferenceHandler.isThisModLoaded("entity_model_features") && !ETFVersionDifferenceHandler.isThisModLoaded("cem"),
                        "config." + ETF.MOD_ID + ".warn.no_emf.text.1",
                        "config." + ETF.MOD_ID + ".warn.no_emf.text.2",
                        null)
        );

    }

    private static String randomQuip() {
        String[] quips = getQuips();
        int rand = new Random().nextInt(quips.length);
        return quips[rand];
    }

    private static String[] getQuips() {
        return new String[]{
                "also try EMF!",
                "also known as ETF!",
                "not to be confused with CIT, seriously, why does that keep happening?",
                "the worst server plugin one guy on my discord has ever seen!",
                "your third cousin's, dog's, previous owner's, uncle's, old boss's, fourth favourite mod!",
                "Thanks for 10 Million plus downloads!!",
                "why does no one download Solid Mobs :(",
                "breaking your resource packs since 17 Jan 2022.",
                "not fit for consumption in the US.",
                "one of the mods ever made!",
                ",serutaeF erutxeT ytitnE gnidoaL",
                "hello there!",
                "you just lost the game.",
                "did you know if you turn off the lights and whisper 'OptiFine' 3 times you will lose 20fps.",
                "now compatible with Minecraft!",
                "now available for Terraria!",
                "OptiFine's weirder younger half-brother that runs around making train noises.",
                ":)",
                "did you know this mod was made because I missed the glowing drowned textures in the Fresh animations addons.",
                "0% Opti, 100% Fine.",
                "Curse you Perry the Platypus!",
                "Lisa needs braces.",
                "Paranormal ResourcePacktivity.",
                "Has Anyone Really Been Far Even as Decided to Use Even Go Want to do Look More Like?"
        };
    }

    public static void registerConfigHandler(TConfigHandler<?> configHandler) {
        if (configHandlers == null) configHandlers = new ObjectArraySet<>();
        configHandlers.add(configHandler);
    }


    public static Screen getConfigScreen(Screen parent) {
        try {
            return new ETFConfigScreenMain(parent);
        } catch (Exception e) {
            return null;
        }
    }

    public static Screen getConfigScreen(MinecraftClient ignored, Screen parent) {
        return getConfigScreen(parent);
    }


    // config code based on bedrockify & actually unbreaking fabric config code
    // https://github.com/juancarloscp52/BedrockIfy/blob/1.17.x/src/main/java/me/juancarloscp52/bedrockify/Bedrockify.java
    // https://github.com/wutdahack/ActuallyUnbreakingFabric/blob/1.18.1/src/main/java/wutdahack/actuallyunbreaking/ActuallyUnbreaking.java


}
