package traben.entity_texture_features.property_reading;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.RandomProperties;
import traben.entity_texture_features.property_reading.properties.generic_properties.IntegerArrayProperty;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.*;

public abstract class ETFTexturePropertiesUtils {


    public static void processNewOptifinePropertiesFile(ETFEntity entity, Identifier vanillaIdentifier, Identifier properties) {
        ETFManager manager = ETFManager.getInstance();
        try {
            Properties props = ETFUtils2.readAndReturnPropertiesElseNull(properties);

            if (props != null) {
                //check for etf entity properties
                if (props.containsKey("vanillaBrightnessOverride")) {
                    String value = props.getProperty("vanillaBrightnessOverride").trim();
                    int tryNumber;
                    try {
                        tryNumber = Integer.parseInt(value.replaceAll("\\D", ""));
                    } catch (NumberFormatException e) {
                        tryNumber = 0;
                    }
                    if (tryNumber >= 16) tryNumber = 15;
                    if (tryNumber < 0) tryNumber = 0;
                    manager.ENTITY_TYPE_VANILLA_BRIGHTNESS_OVERRIDE_VALUE.put(entity.getType(), tryNumber);
                }
                if (entity.isZombiePiglin()
                        && props.containsKey("showHiddenModelParts")
                        && "true".equals(props.getProperty("showHiddenModelParts"))) {
                    manager.zombiePiglinRightEarEnabled = true;
                }
                if (props.containsKey("suppressParticles")
                        && "true".equals(props.getProperty("suppressParticles"))) {
                    manager.ENTITY_TYPE_IGNORE_PARTICLES.add(entity.getType());
                }

                if (props.containsKey("entityRenderLayerOverride")) {
                    String layer = props.getProperty("entityRenderLayerOverride");
                    //noinspection EnhancedSwitchMigration
                    switch (layer) {
                        case "translucent":
                            manager.ENTITY_TYPE_RENDER_LAYER.put(entity.getType(), 1);
                            break;
                        case "translucent_cull":
                            manager.ENTITY_TYPE_RENDER_LAYER.put(entity.getType(), 2);
                            break;
                        case "end_portal":
                            manager.ENTITY_TYPE_RENDER_LAYER.put(entity.getType(), 3);
                            break;
                        case "outline":
                            manager.ENTITY_TYPE_RENDER_LAYER.put(entity.getType(), 4);
                            break;
                    }
                }
                List<ETFTexturePropertyCase> allCasesForTexture = getAllValidPropertyObjects(props, vanillaIdentifier, "skins","textures");

                if (!allCasesForTexture.isEmpty()) {
                    //it all worked now just get the first texture called and everything is set for the next time the texture is called for fast processing
                    manager.OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, allCasesForTexture);
                } else {
                    ETFUtils2.logMessage("Ignoring properties file that failed to load any cases @ " + vanillaIdentifier, false);
                    manager.OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, null);
                }
            } else {//properties file is null
                ETFUtils2.logMessage("Ignoring properties file that was null @ " + vanillaIdentifier, false);
                manager.OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, null);
            }
        } catch (Exception e) {
            ETFUtils2.logWarn("Ignoring properties file that caused Exception @ " + vanillaIdentifier + "\n" + e, false);
            e.printStackTrace();
            manager.OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, null);
        }
    }

    public static List<ETFTexturePropertyCase> getAllValidPropertyObjects(Properties props, Identifier vanillaIdentifier, String... suffixToTest) {
        Set<String> propIds = props.stringPropertyNames();
        //set so only 1 of each
        Set<Integer> numbers = new HashSet<>();

        //get the numbers we are working with
        for (String str :
                propIds) {
            str = str.replaceAll("\\D", "");
            if (!str.isEmpty()) {
                try {
                    numbers.add(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    ETFUtils2.logWarn("properties file number error in start count");
                }
            }
        }
        //sort from lowest to largest
        List<Integer> numbersList = new ArrayList<>(numbers);
        Collections.sort(numbersList);
        List<ETFTexturePropertyCase> allCasesForTexture = new ArrayList<>();
        for (Integer num :
                numbersList) {
            //System.out.println("constructed as "+num);
            //loops through each known number in properties
            //all case.1 ect should be processed here
            Integer[] suffixes = getSuffixes(props, num, suffixToTest);


            //list easier to build
            if (suffixes != null && suffixes.length != 0) {
                allCasesForTexture.add(new ETFTexturePropertyCase(
                        vanillaIdentifier.toString(),
                        num,
                        suffixes,
                        getWeights(props, num),
                        RandomProperties.getAllRegisteredRandomPropertiesOfIndex(props,num)
                ));
            } else {
                ETFUtils2.logWarn("property number \"" + num + ". in file \"" + vanillaIdentifier + ". failed to read.");
            }
        }
        return allCasesForTexture;
    }


    @Nullable
    private static Integer[] getSuffixes(Properties props, int num, String... suffixToTest) {
        return IntegerArrayProperty.getGenericIntegerSplitWithRanges(props, num, suffixToTest);
    }

    @Nullable
    private static Integer[] getWeights(Properties props, int num) {
        if (props.containsKey("weights." + num)) {
            return IntegerArrayProperty.getGenericIntegerSplitWithRanges(props, num, "weights");
        }
        return null;
    }


}
