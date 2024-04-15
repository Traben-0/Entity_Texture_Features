package traben.entity_texture_features.config;

import java.util.HashSet;
import java.util.Set;


public abstract class ETFConfigWarnings {

    @SuppressWarnings("StaticCollection")
    private static final Set<ETFConfigWarning> REGISTERED_WARNINGS = new HashSet<>();

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

