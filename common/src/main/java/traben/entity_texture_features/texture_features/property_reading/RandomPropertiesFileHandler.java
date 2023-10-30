package traben.entity_texture_features.texture_features.property_reading;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.property_reading.properties.RandomProperties;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.*;

public abstract class RandomPropertiesFileHandler {


    public static void processNewOptiFinePropertiesFile(ETFEntity entity, Identifier vanillaIdentifier, Identifier properties) {
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
                List<RandomPropertyRule> allCasesForTexture = getAllValidPropertyObjects(props, vanillaIdentifier, "skins", "textures");

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

    public static List<RandomPropertyRule> getAllValidPropertyObjects(Properties properties, Identifier propertiesFilePath, String... suffixToTest) {
        Set<String> propIds = properties.stringPropertyNames();
        //set so only 1 of each
        Set<Integer> foundRuleNumbers = new HashSet<>();

        //get the foundRuleNumbers we are working with
        for (String str :
                propIds) {
            String[] split = str.split("\\.");
            if (split.length >= 2 && !split[1].isBlank()) {
                String possibleRuleNumber = split[1].replaceAll("\\D", "");
                if (!possibleRuleNumber.isBlank()) {
                    try {
                        foundRuleNumbers.add(Integer.parseInt(possibleRuleNumber));
                    } catch (NumberFormatException e) {
                        //ETFUtils2.logWarn("properties file number error in start count");
                    }
                }
            }
        }
        //sort from lowest to largest
        List<Integer> numbersList = new ArrayList<>(foundRuleNumbers);
        Collections.sort(numbersList);
        List<RandomPropertyRule> allRulesOfProperty = new ArrayList<>();
        for (Integer ruleNumber :
                numbersList) {
            //System.out.println("constructed as "+ruleNumber);
            //loops through each known number in properties
            //all rule.1 ect should be processed here
            Integer[] suffixesOfRule = getSuffixes(properties, ruleNumber, suffixToTest);


            //list easier to build
            if (suffixesOfRule != null && suffixesOfRule.length != 0) {
                allRulesOfProperty.add(new RandomPropertyRule(
                        propertiesFilePath.toString(),
                        ruleNumber,
                        suffixesOfRule,
                        getWeights(properties, ruleNumber),
                        RandomProperties.getAllRegisteredRandomPropertiesOfIndex(properties, ruleNumber)
                ));
            } else {
                ETFUtils2.logWarn("property number \"" + ruleNumber + ". in file \"" + propertiesFilePath + ". failed to read.");
            }
        }
        return allRulesOfProperty;
    }


    @Nullable
    private static Integer[] getSuffixes(Properties props, int num, String... suffixToTest) {
        return SimpleIntegerArrayProperty.getGenericIntegerSplitWithRanges(props, num, suffixToTest);
    }

    @Nullable
    private static Integer[] getWeights(Properties props, int num) {
        return SimpleIntegerArrayProperty.getGenericIntegerSplitWithRanges(props, num, "weights");
    }


}
