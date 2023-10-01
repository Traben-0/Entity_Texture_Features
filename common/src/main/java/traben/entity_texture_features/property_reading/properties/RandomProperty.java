package traben.entity_texture_features.property_reading.properties;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.entity_handlers.ETFEntity;

import java.util.Properties;

/**
 * Abstract class to hold each random property to test entities against
 * Must be registered with {@link RandomProperties#register(RandomProperties.RandomPropertyFactory...)}
 */
public abstract class RandomProperty {

    /**
     * Test the entity against this property.
     * <p>
     * This method wraps {@link RandomProperty#testEntityInternal(ETFEntity, boolean, Object2BooleanOpenHashMap)} testEntityInternal()}
     * to provide spawn condition & repeated check efficiency
     *
     * @param entity          the ETFEntity being tested by this property
     * @param isUpdate        flags if this test is part of an update
     * @param spawnConditions the original spawn conditions map of this entity, possibly holding a prior value for this test
     * @return true if the entity meets the requirements of this property
     */
    public boolean testEntity(ETFEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<String> spawnConditions){
        if(isUpdate && !isPropertyUpdatable()){
            String key = getPropertyId();
            if (spawnConditions.containsKey(key)) {
                return spawnConditions.getBoolean(key);
            }
            return false;
        }
        boolean result = testEntityInternal(entity, isUpdate, spawnConditions);
        if(!isPropertyUpdatable())
            spawnConditions.put(getPropertyId(),result);
        return result;
    }

    /**
     * Internal abstract method functionality for {@link RandomProperty#testEntity(ETFEntity, boolean, Object2BooleanOpenHashMap)}
     */
    protected abstract boolean testEntityInternal(ETFEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<String> spawnConditions);


    /**
     * Flags whether this property can be considered to update over time.
     * If not then we can optimize checks for each entity after the first.
     *
     * @return the boolean
     */
    public boolean isPropertyUpdatable(){
        return true;
    }

    /**
     * Returns a String[] of all valid property id's these will be the text before the first full stop in the properties file.
     *
     * @return a string[]
     */
    @NotNull
    public abstract String[] getPropertyIds();

    /**
     * Returns the first index of {@link RandomProperty#getPropertyIds()}
     *
     * @return the string
     */
    @NotNull
    public String getPropertyId(){
        return getPropertyIds()[0];
    }

    /**
     * Reads the given property data from the properties file, allowing for multiple property names and throws an
     * exception to ensure that any actual returned String is always non-blank.
     *
     * @param properties the properties file being read
     * @param propertyNum the property index that is being read
     * @param propertyId the property id's to be read
     *
     * @return the String value of the desired property
     * @throws RandomPropertyException for any invalid, blank, or missing property.
     */
    @NotNull
    public static String readPropertiesOrThrow(Properties properties, int propertyNum, String... propertyId) throws RandomPropertyException{
        if(propertyId.length == 0) throw new IllegalArgumentException("[ETF] readPropertiesOrThrow() was given empty property id's");
        for (String id:
                propertyId) {
            if (properties.containsKey(id + "." + propertyNum)) {
                String dataFromProps = properties.getProperty(id + "." + propertyNum).strip();
                if(!dataFromProps.isBlank()) return dataFromProps;
            }
        }
        throw new RandomPropertyException("failed to read property ["+propertyId[0]+"]");
    }

    /**
     * This exception indicates that something has failed while creating an instance of {@link RandomProperty} from a
     * properties file.
     * <p>
     * This is thrown when the 'failure' is a safe or expected 'failure' with examples such as:<p>
     *  - The property does not exist in this file<p>
     *  - The property was written incorrectly<p>
     *  - the property is empty or blank<p>
     *
     *  This should always be caught and ignored, it acts as a method of filtering out other unexpected Exceptions and debugging.
     */
    public static class RandomPropertyException extends Exception {
        /**
         * Instantiates a new Random property exception.
         *
         * @param reason the reason
         */
        public RandomPropertyException(String reason) {
            super("[ETF] " + reason);
        }
    }
}
