package traben.entity_texture_features.texture_features.property_reading;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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


    public boolean doesEntityMeetConditionsOfThisCase(ETFEntity etfEntity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
        if (RULE_ALWAYS_APPROVED) return true;
        if (etfEntity == null) return false;

        UUID id = etfEntity.getUuid();

        Object2BooleanOpenHashMap<RandomProperty> spawnConditions;
        if (ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.containsKey(id)) {
            spawnConditions = (ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.get(id));
        } else {
            spawnConditions = new Object2BooleanOpenHashMap<>();
            ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.put(id, spawnConditions);
        }

        boolean wasEntityTestedByAnUpdatableProperty = false;
        boolean entityMetRequirements = true;
        try {
            for (RandomProperty property :
                    PROPERTIES_TO_TEST) {
                if (!entityMetRequirements) break;
                if (property.isPropertyUpdatable())
                    wasEntityTestedByAnUpdatableProperty = true;
                entityMetRequirements = property.testEntity(etfEntity, isUpdate, spawnConditions);
            }
        } catch (Exception e) {
            ETFUtils2.logWarn("Random Property file [" +
                    PROPERTY_FILE + "] rule # [" + RULE_NUMBER + "] failed with Exception:\n" + e.getMessage());
            //fail this test
            entityMetRequirements = false;
            wasEntityTestedByAnUpdatableProperty = false;
        }

        if (wasEntityTestedByAnUpdatableProperty && UUID_CaseHasUpdateablesCustom != null) {
            UUID_CaseHasUpdateablesCustom.put(etfEntity.getUuid(), true);
        }

        ETFManager.getInstance().LAST_MET_RULE_INDEX.put(id, entityMetRequirements ? RULE_NUMBER : 0);

        return entityMetRequirements;
    }

    public int getVariantSuffixFromThisCase(UUID uuid) {
        int randomSeededByUUID = Math.abs(uuid.hashCode());
        return SUFFIX_NUMBERS_WEIGHTED[randomSeededByUUID % SUFFIX_NUMBERS_WEIGHTED.length];
    }

}
