package traben.entity_texture_features.features.property_reading;

import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.EntityBooleanLRU;

import java.util.*;

public class RandomPropertyRule {
    public final int RULE_NUMBER;
    public final String PROPERTY_FILE;
    private final Integer[] SUFFIX_NUMBERS_WEIGHTED;
    private final RandomProperty[] PROPERTIES_TO_TEST;
    private final boolean RULE_ALWAYS_APPROVED;


    public RandomPropertyRule(
            String propertiesFile,
            int ruleNumber,
            Integer[] suffixes,
            Integer[] weights,
            RandomProperty... properties

    ) {
        PROPERTY_FILE = propertiesFile;
        RULE_NUMBER = ruleNumber;
        PROPERTIES_TO_TEST = properties;
        RULE_ALWAYS_APPROVED = properties.length == 0;

        if (weights == null || weights.length == 0) {
            SUFFIX_NUMBERS_WEIGHTED = suffixes;
        } else {
            if (weights.length == suffixes.length) {
                LinkedList<Integer> weightedSuffixArray = new LinkedList<>();
                try {
                    for (int index = 0; index < suffixes.length; index++) {
                        int suffixValue = suffixes[index];
                        int weightValue = weights[index];
                        for (int i = 0; i < weightValue; i++) {
                            //adds the suffix as many times as it is weighted
                            weightedSuffixArray.add(suffixValue);
                        }
                    }
                } catch (Exception e) {
                    weightedSuffixArray.clear();
                    weightedSuffixArray.addAll(List.of(suffixes));
                }
                SUFFIX_NUMBERS_WEIGHTED = weightedSuffixArray.toArray(new Integer[0]);
            } else {
                ETFUtils2.logWarn("random texture weights don't match for [" +
                        PROPERTY_FILE + "] rule # [" + RULE_NUMBER + "] :\n suffixes: " + Arrays.toString(suffixes) + "\n weights: " + Arrays.toString(weights), false);
                SUFFIX_NUMBERS_WEIGHTED = suffixes;
            }
        }
    }

    public Set<Integer> getSuffixSet() {
        return new HashSet<>(List.of(SUFFIX_NUMBERS_WEIGHTED));
    }

    public boolean doesEntityMeetConditionsOfThisCase(ETFEntity etfEntity, boolean isUpdate, EntityBooleanLRU UUID_CaseHasUpdateablesCustom) {
        if (RULE_ALWAYS_APPROVED) return true;
        if (etfEntity == null) return false;

        boolean wasEntityTestedByAnUpdatableProperty = false;
        boolean entityMetRequirements = true;
        try {
            for (RandomProperty property :
                    PROPERTIES_TO_TEST) {
                if (!entityMetRequirements) break;
                if (property.canPropertyUpdate())
                    wasEntityTestedByAnUpdatableProperty = true;
                entityMetRequirements = property.testEntity(etfEntity, isUpdate);
            }
        } catch (Exception e) {
            ETFUtils2.logWarn("Random Property file [" +
                    PROPERTY_FILE + "] rule # [" + RULE_NUMBER + "] failed with Exception:\n" + e.getMessage());
            //fail this test
            entityMetRequirements = false;
            wasEntityTestedByAnUpdatableProperty = false;
        }

        if (wasEntityTestedByAnUpdatableProperty && UUID_CaseHasUpdateablesCustom != null) {
            UUID_CaseHasUpdateablesCustom.put(etfEntity.etf$getUuid(), true);
        }


        return entityMetRequirements;
    }

    public int getVariantSuffixFromThisCase(int seed) {
        return SUFFIX_NUMBERS_WEIGHTED[Math.abs(seed) % SUFFIX_NUMBERS_WEIGHTED.length];
    }


    public void cacheEntityInitialResultsOfNonUpdatingProperties(ETFEntity entity) {
        try {
            for (RandomProperty property :
                    PROPERTIES_TO_TEST) {
                if (!property.canPropertyUpdate()) {
                    property.cacheEntityInitialResult(entity);
                }
            }
        } catch (Exception ignored) {
        }
    }

}
