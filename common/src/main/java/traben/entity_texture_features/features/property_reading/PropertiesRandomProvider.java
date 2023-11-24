package traben.entity_texture_features.features.property_reading;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.property_reading.properties.RandomProperties;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.texture_handlers.ETFDirectory;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.*;

public class PropertiesRandomProvider implements ETFApi.ETFVariantSuffixProvider {


    protected final List<RandomPropertyRule> propertyRules;

    protected final Object2BooleanOpenHashMap<UUID> entityCanUpdate = new Object2BooleanOpenHashMap<>();

    protected final String packname;

    private PropertiesRandomProvider(Identifier propertiesFileIdentifier, List<RandomPropertyRule> propertyRules) {
        this.propertyRules = propertyRules;
        this.packname = MinecraftClient.getInstance().getResourceManager().getResource(propertiesFileIdentifier)
                .map(Resource::getResourcePackName)
                .orElse("vanilla");
    }

    @Nullable
    public static PropertiesRandomProvider of(Identifier initialPropertiesFileIdentifier, Identifier vanillaIdentifier, String... suffixKeyName) {
        Identifier propertiesFileIdentifier = ETFDirectory.getDirectoryVersionOf(initialPropertiesFileIdentifier);
        if (propertiesFileIdentifier == null) return null;

        try {
            Properties props = ETFUtils2.readAndReturnPropertiesElseNull(propertiesFileIdentifier);
            if (props == null) {
                ETFUtils2.logMessage("Ignoring properties file that was null @ " + propertiesFileIdentifier, false);
                return null;
            }
            if (vanillaIdentifier.getPath().endsWith(".png")) {
                ETFManager.getInstance().grabSpecialProperties(props, ETFRenderContext.getCurrentEntity());
            }

            List<RandomPropertyRule> propertyRules = PropertiesRandomProvider.getAllValidPropertyObjects(props, propertiesFileIdentifier, suffixKeyName);
            if (propertyRules.isEmpty()) {
                ETFUtils2.logMessage("Ignoring properties file that failed to load any cases @ " + propertiesFileIdentifier, false);
                return null;
            }

            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
            String properties = resourceManager.getResource(propertiesFileIdentifier).map(Resource::getResourcePackName).orElse(null);
            String vanillaPack = resourceManager.getResource(vanillaIdentifier).map(Resource::getResourcePackName).orElse(null);

            if (properties != null
                    && properties.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(properties, vanillaPack))) {
                return new PropertiesRandomProvider(propertiesFileIdentifier, propertyRules);
            }
        } catch (Exception e) {
            ETFUtils2.logWarn("Ignoring properties file that caused Exception @ " + propertiesFileIdentifier + "\n" + e, false);
            e.printStackTrace();
        }
        return null;
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

    public String getPackName() {
        return packname;
    }

    @Override
    public boolean entityCanUpdate(UUID uuid) {
        return entityCanUpdate.getBoolean(uuid);
    }

    @SuppressWarnings("unused")
    @Override
    public IntOpenHashSet getAllSuffixes() {
        IntOpenHashSet allSuffixes = new IntOpenHashSet();
        for (RandomPropertyRule rule :
                propertyRules) {
            allSuffixes.addAll(rule.getSuffixSet());
        }
        return allSuffixes;
    }

    @Override
    public int size() {
        return propertyRules.size();
    }


    @Override
    public int getSuffixForETFEntity(ETFEntity entityToBeTested) {
        if (entityToBeTested == null) return 0;
        UUID id = entityToBeTested.etf$getUuid();
        boolean entityTestedBefore = entityCanUpdate.containsKey(id);
        for (RandomPropertyRule testCase : propertyRules) {
            if (testCase.doesEntityMeetConditionsOfThisCase(entityToBeTested, entityTestedBefore, entityCanUpdate)) {
                return testCase.getVariantSuffixFromThisCase(id);
            }
        }
        return 0;
    }


}
