package traben.entity_texture_features.property_reading.properties;

import traben.entity_texture_features.property_reading.properties.optifine_properties.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class RandomProperties {
    private static final LinkedList<RandomPropertyFactory> REGISTERED_PROPERTIES = new LinkedList<>();

    /**
     * Register new {@link RandomProperty} objects to be tested against entities.
     *
     * @param properties the property to be registered for testing
     */
    public static void registerRandomProperty(RandomPropertyFactory... properties){
        REGISTERED_PROPERTIES.addAll(List.of(properties));
    }

    public static LinkedList<RandomProperty> getAllRegisteredRandomPropertiesOfIndex(Properties properties,int propertyNum){
        LinkedList<RandomProperty> randomProperties = new LinkedList<>();
        for (RandomPropertyFactory factory:
             REGISTERED_PROPERTIES) {
            RandomProperty property = factory.getPropertyOrNull(properties, propertyNum);
            if(property != null) randomProperties.add(property);
        }
        return randomProperties;
    }

    public interface RandomPropertyFactory{
        RandomProperty getPropertyOrNull(Properties properties, int propertyNum);
    }

    static{
        registerRandomProperty(//todo check all registered
                NameProperty::getPropertyOrNull,
                BiomeProperty::getPropertyOrNull,
                HeightProperty::getPropertyOrNull,
                ProfessionProperty::getPropertyOrNull,
                ColorProperty::getPropertyOrNull,
                BabyProperty::getPropertyOrNull,
                WeatherProperty::getPropertyOrNull,
                HealthProperty::getPropertyOrNull,
                TimeOfDayProperty::getPropertyOrNull,
                MoonPhaseProperty::getPropertyOrNull
        );
    }

}
