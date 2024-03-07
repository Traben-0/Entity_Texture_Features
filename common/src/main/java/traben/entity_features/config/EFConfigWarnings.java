package traben.entity_features.config;

import java.util.HashSet;
import java.util.Set;


public abstract class EFConfigWarnings {

    @SuppressWarnings("StaticCollection")
    private static final Set<EFConfigWarning> REGISTERED_WARNINGS = new HashSet<>();

    /**
     * Register new {@link EFConfigWarning}.
     *
     * @param warnings the {@link EFConfigWarning}s to be registered
     */
    public static void registerConfigWarning(EFConfigWarning... warnings) {
        for (EFConfigWarning warn : warnings) {
            if (warn != null)
                REGISTERED_WARNINGS.add(warn);
        }
    }

    /**
     * Gets all registered {@link EFConfigWarning}.
     *
     * @return the registered {@link EFConfigWarning} Set
     */
    public static Set<EFConfigWarning> getRegisteredWarnings() {
        return REGISTERED_WARNINGS;
    }

}

