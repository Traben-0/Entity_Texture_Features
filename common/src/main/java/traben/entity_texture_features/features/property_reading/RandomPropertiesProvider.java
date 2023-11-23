package traben.entity_texture_features.features.property_reading;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
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

public class RandomPropertiesProvider implements ETFApi.ETFVariantSuffixProvider {


    protected final List<RandomPropertyRule> propertyRules;

    protected final String packname;

    private RandomPropertiesProvider(Identifier propertiesFileIdentifier, List<RandomPropertyRule> propertyRules) {
        this.propertyRules = propertyRules;
        this.packname = MinecraftClient.getInstance().getResourceManager().getResource(propertiesFileIdentifier)
                .map(Resource::getResourcePackName)
                .orElse("vanilla");
    }

    @Nullable
    public static RandomPropertiesProvider of(Identifier initialPropertiesFileIdentifier, Identifier vanillaIdentifier, String... suffixKeyName) {
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

            List<RandomPropertyRule> propertyRules = RandomPropertiesProvider.getAllValidPropertyObjects(props, propertiesFileIdentifier, suffixKeyName);
            if (propertyRules.isEmpty()) {
                ETFUtils2.logMessage("Ignoring properties file that failed to load any cases @ " + propertiesFileIdentifier, false);
                return null;
            }

            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
            String properties = resourceManager.getResource(propertiesFileIdentifier).map(Resource::getResourcePackName).orElse(null);
            String vanillaPack = resourceManager.getResource(vanillaIdentifier).map(Resource::getResourcePackName).orElse(null);

            if (properties != null
                    && properties.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{properties, vanillaPack}))) {
                return new RandomPropertiesProvider(propertiesFileIdentifier, propertyRules);
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

    @SuppressWarnings("unused")
    @Override
    public int getSuffixForEntity(Entity entityToBeTested, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
        return getSuffixForETFEntity((ETFEntity) entityToBeTested, isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain);

    }

    @SuppressWarnings("unused")
    @Override
    public int getSuffixForBlockEntity(BlockEntity entityToBeTested, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
        return getSuffixForETFEntity((ETFEntity) entityToBeTested, isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain);
    }

    @Override
    public int getSuffixForETFEntity(ETFEntity entityToBeTested, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
        if (entityToBeTested == null) return 0;
        boolean isAnUpdate = !isThisTheFirstTestForEntity;
        for (RandomPropertyRule testCase : propertyRules) {
            if (testCase.doesEntityMeetConditionsOfThisCase(entityToBeTested, isAnUpdate, cacheToMarkEntitiesWhoseVariantCanChangeAgain)) {
                return testCase.getVariantSuffixFromThisCase(entityToBeTested.etf$getUuid());
            }
        }
        return 0;
    }


}
