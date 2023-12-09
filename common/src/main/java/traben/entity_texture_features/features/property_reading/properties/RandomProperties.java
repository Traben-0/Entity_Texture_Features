package traben.entity_texture_features.features.property_reading.properties;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.*;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.*;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

public class RandomProperties {
    @SuppressWarnings("StaticCollection")
    private static final Set<RandomPropertyFactory> REGISTERED_PROPERTIES = new ObjectOpenHashSet<>();

    static {
        register(
                //ETF properties
                AngryProperty::getPropertyOrNull,
                ChargedCreeperProperty::getPropertyOrNull,
                DistanceToPlayerProperty::getPropertyOrNull,
                ItemProperty::getPropertyOrNull,
                JumpProperty::getPropertyOrNull,
                LlamaInventoryProperty::getPropertyOrNull,
                MaxHealthProperty::getPropertyOrNull,
                MovingProperty::getPropertyOrNull,
                PandaGeneProperty::getPropertyOrNull,
                PlayerCreatedProperty::getPropertyOrNull,
                ScreamingGoatProperty::getPropertyOrNull,
                SpeedProperty::getPropertyOrNull,

                //OptiFine properties
                BabyProperty::getPropertyOrNull,
                BiomeProperty::getPropertyOrNull,
                BlocksProperty::getPropertyOrNull,
                ColorProperty::getPropertyOrNull,
                HealthProperty::getPropertyOrNull,
                HeightProperty::getPropertyOrNull,
                MoonPhaseProperty::getPropertyOrNull,
                NameProperty::getPropertyOrNull,
                NBTProperty::getPropertyOrNull,
                ProfessionProperty::getPropertyOrNull,
                SizeProperty::getPropertyOrNull,
                TeamProperty::getPropertyOrNull,
                TimeOfDayProperty::getPropertyOrNull,
                WeatherProperty::getPropertyOrNull
        );
    }

    /**
     * Register new {@link RandomProperty} objects to be tested against entities.
     *
     * @param properties the property to be registered for testing
     */
    public static void register(RandomPropertyFactory... properties) {
        for (RandomPropertyFactory factory :
                properties) {
            if (factory != null) {
                REGISTERED_PROPERTIES.add(factory);
            }
        }
    }

    /**
     * Get an array of all {@link RandomProperty} that are present for this property index.
     *
     * @param properties  the properties file
     * @param propertyNum the property index
     * @return the array of all {@link RandomProperty} that are present for this property index.
     */
    public static RandomProperty[] getAllRegisteredRandomPropertiesOfIndex(Properties properties, int propertyNum) {
        ArrayList<RandomProperty> randomProperties = new ArrayList<>();
        for (RandomPropertyFactory factory :
                REGISTERED_PROPERTIES) {
            if (factory != null) {
                RandomProperty property = factory.getPropertyOrNull(properties, propertyNum);
                if (property != null) randomProperties.add(property);
            }
        }
        return randomProperties.toArray(new RandomProperty[0]);
    }


    public interface RandomPropertyFactory {
        RandomProperty getPropertyOrNull(@SuppressWarnings("unused") Properties properties, @SuppressWarnings("unused") int propertyNum);
    }

}
