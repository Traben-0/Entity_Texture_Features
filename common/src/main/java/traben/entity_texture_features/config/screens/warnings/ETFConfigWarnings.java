package traben.entity_texture_features.config.screens.warnings;

import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.HashSet;
import java.util.Set;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;


public abstract class ETFConfigWarnings {

    @SuppressWarnings("StaticCollection")
    private static final Set<ETFConfigWarning> REGISTERED_WARNINGS = new HashSet<>();

    static {
        registerConfigWarning(
                //figura
                new ETFConfigWarning.Simple(
                        "figura",
                        "figura",
                        "config." + ETFClientCommon.MOD_ID + ".warn.figura.text.1",
                        "config." + ETFClientCommon.MOD_ID + ".warn.figura.text.2",
                        () -> {
                            ETFConfigData.skinFeaturesEnabled = false;
                            ETFUtils2.saveConfig();
                        }),
                //EBE
                new ETFConfigWarning.Simple(
                        "enhancedblockentities",
                        "enhancedblockentities",
                        "config." + ETFClientCommon.MOD_ID + ".warn.ebe.text.1",
                        "config." + ETFClientCommon.MOD_ID + ".warn.ebe.text.2",
                        null),
                //quark
                new ETFConfigWarning.Simple(
                        "quark",
                        "quark",
                        "config." + ETFClientCommon.MOD_ID + ".warn.quark.text.3",
                        "config." + ETFClientCommon.MOD_ID + ".warn.quark.text.4",
                        null),
                //iris
                new ETFConfigWarning.Simple(
                        "iris",
                        "iris",
                        "config." + ETFClientCommon.MOD_ID + ".warn.iris.text.1",
                        "config." + ETFClientCommon.MOD_ID + ".warn.iris.text.2",
                        null),
                //iris and 3d skin layers trim warning
                new ETFConfigWarning.Simple(
                        "iris & 3d skin layers",
                        () -> ETFVersionDifferenceHandler.isThisModLoaded("iris") && ETFClientCommon.SKIN_LAYERS_DETECTED,
                        "config." + ETFClientCommon.MOD_ID + ".warn.iris_3d.text.1",
                        "config." + ETFClientCommon.MOD_ID + ".warn.iris_3d.text.2",
                        null),
                //no CEM mod, recommend EMF
                new ETFConfigWarning.Simple(
                        "emf",
                        () -> !ETFVersionDifferenceHandler.isThisModLoaded("entity_model_features") && !ETFVersionDifferenceHandler.isThisModLoaded("cem"),
                        "config." + ETFClientCommon.MOD_ID + ".warn.no_emf.text.1",
                        "config." + ETFClientCommon.MOD_ID + ".warn.no_emf.text.2",
                        null)
        );
    }

    /**
     * Register new {@link ETFConfigWarning}.
     *
     * @param warnings the {@link ETFConfigWarning}s to be registered
     */
    public static void registerConfigWarning(ETFConfigWarning... warnings) {
        for (ETFConfigWarning warn : warnings) {
            if (warn != null)
                REGISTERED_WARNINGS.add(warn);
        }
    }

    /**
     * Gets all registered {@link ETFConfigWarning}.
     *
     * @return the registered {@link ETFConfigWarning} Set
     */
    public static Set<ETFConfigWarning> getRegisteredWarnings() {
        return REGISTERED_WARNINGS;
    }

}

