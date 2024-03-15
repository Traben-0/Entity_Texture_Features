package traben.entity_texture_features.features.property_reading.properties;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.*;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.*;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class RandomProperties {
    @SuppressWarnings("StaticCollection")
    private static final ObjectOpenHashSet<RandomPropertyFactory> REGISTERED_PROPERTIES = new ObjectOpenHashSet<>();

    public static void forEachProperty(@NotNull Consumer<RandomPropertyFactory> consumer) {
        REGISTERED_PROPERTIES.forEach(consumer);
    }

    static {
        register(
                //ETF properties
                RandomPropertyFactory.of("angry",AngryProperty::getPropertyOrNull),
                RandomPropertyFactory.of("creeperCharged",ChargedCreeperProperty::getPropertyOrNull),
                RandomPropertyFactory.of("distance",DistanceToPlayerProperty::getPropertyOrNull),
                RandomPropertyFactory.of("items",ItemProperty::getPropertyOrNull),
                RandomPropertyFactory.of("jumpStrength",true,JumpProperty::getPropertyOrNull),
                RandomPropertyFactory.of("llamaInventory",true,LlamaInventoryProperty::getPropertyOrNull),
                RandomPropertyFactory.of("maxHealth",true,MaxHealthProperty::getPropertyOrNull),
                RandomPropertyFactory.of("moving",MovingProperty::getPropertyOrNull),
                RandomPropertyFactory.of("hiddenGene",true,PandaGeneProperty::getPropertyOrNull),
                RandomPropertyFactory.of("playerCreated",true,PlayerCreatedProperty::getPropertyOrNull),
                RandomPropertyFactory.of("screamingGoat",true,ScreamingGoatProperty::getPropertyOrNull),
                RandomPropertyFactory.of("maxSpeed",true,SpeedProperty::getPropertyOrNull),
                RandomPropertyFactory.of("isSpawner",true,SpawnerProperty::getPropertyOrNull),
                RandomPropertyFactory.of("dimension",true,DimensionProperty::getPropertyOrNull),
                RandomPropertyFactory.of("light",LightProperty::getPropertyOrNull),
                RandomPropertyFactory.of("variant",true,VariantProperty::getPropertyOrNull),
                RandomPropertyFactory.of("isCreative",CreativeProperty::getPropertyOrNull),
                RandomPropertyFactory.of("isTeammate",TeammateProperty::getPropertyOrNull),
                RandomPropertyFactory.of("isClientPlayer",true,ClientPlayerProperty::getPropertyOrNull),
                RandomPropertyFactory.of("teams",TeamProperty::getPropertyOrNull),

                //etf externals
                RandomPropertyFactory.of("hour",HourProperty::getPropertyOrNull),
                RandomPropertyFactory.of("minute",MinuteProperty::getPropertyOrNull),
                RandomPropertyFactory.of("monthDay",true,MonthDayProperty::getPropertyOrNull),
                RandomPropertyFactory.of("month",true,MonthProperty::getPropertyOrNull),
                RandomPropertyFactory.of("second",SecondProperty::getPropertyOrNull),
                RandomPropertyFactory.of("weekDay",true,WeekDayProperty::getPropertyOrNull),
                RandomPropertyFactory.of("yearDay",true,YearDayProperty::getPropertyOrNull),
                RandomPropertyFactory.of("year",true,YearProperty::getPropertyOrNull),
                RandomPropertyFactory.of("language",true,LanguageProperty::getPropertyOrNull),

                //OptiFine properties
                RandomPropertyFactory.of("baby",BabyProperty::getPropertyOrNull),
                RandomPropertyFactory.of("biomes",true,BiomeProperty::getPropertyOrNull),
                RandomPropertyFactory.of("blocks",true,BlocksProperty::getPropertyOrNull),
                RandomPropertyFactory.of("colors",ColorProperty::getPropertyOrNull),
                RandomPropertyFactory.of("health",HealthProperty::getPropertyOrNull),
                RandomPropertyFactory.of("heights",true,HeightProperty::getPropertyOrNull),
                RandomPropertyFactory.of("moonPhase",true,MoonPhaseProperty::getPropertyOrNull),
                RandomPropertyFactory.of("name",NameProperty::getPropertyOrNull),
                RandomPropertyFactory.of("nbt",NBTProperty::getPropertyOrNull),
                RandomPropertyFactory.of("professions",ProfessionProperty::getPropertyOrNull),
                RandomPropertyFactory.of("sizes",SizeProperty::getPropertyOrNull),
                RandomPropertyFactory.of("dayTime",true,TimeOfDayProperty::getPropertyOrNull),
                RandomPropertyFactory.of("weather",true,WeatherProperty::getPropertyOrNull)
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

        static @NotNull RandomPropertyFactory of(@NotNull String id,
                                                 @NotNull BiFunction <Properties, Integer, RandomProperty> factory) {
            return of(id, false, factory);
        }
        static @NotNull RandomPropertyFactory of(@NotNull String id,
                                                 boolean isSpawnLocked,
                                                 @NotNull BiFunction <Properties, Integer, RandomProperty> factory) {
            return new RandomPropertyFactory() {
                @Override
                public RandomProperty getPropertyOrNull(Properties properties, int propertyNum) {
                    if (ETF.config().getConfig().isPropertyDisabled(this)) return null;
                    if (properties == null ) return null;
                    var property = factory.apply(properties, propertyNum);
                    property.setCanUpdate(ETF.config().getConfig().canPropertyUpdate(this));
                    return property;
                }

                @Override
                public @NotNull String getPropertyId() {
                    return id;
                }

                @Override
                public boolean equals(final Object obj) {
                    return obj instanceof RandomPropertyFactory && ((RandomPropertyFactory) obj).getPropertyId().equals(getPropertyId());
                }

                @Override
                public int hashCode() {
                    return getPropertyId().hashCode();
                }

                @Override
                public boolean updatesOverTime() {
                    return !isSpawnLocked;
                }
            };
        }
        @Nullable RandomProperty getPropertyOrNull(@SuppressWarnings("unused") Properties properties, @SuppressWarnings("unused") int propertyNum);

        @NotNull String getPropertyId();

        boolean updatesOverTime();

    }

}
