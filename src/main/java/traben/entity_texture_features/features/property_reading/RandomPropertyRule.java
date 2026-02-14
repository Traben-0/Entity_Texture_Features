package traben.entity_texture_features.features.property_reading;

import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.EntityBooleanLRU;

import java.util.*;

public class RandomPropertyRule {
    public final int ruleNumber;
    public final String propertyFile;
    private final Integer[] suffixNumbers;
    private final Integer[] weights;
    private final int weightTotal;
    private final int seedOffset;
    private final SeedSource seedSource;
    private final RandomProperty[] propertiesToTest;
    private final boolean ruleAlwaysApproved;
    private final boolean updates;

    static final RandomPropertyRule DEFAULT_RETURN = new RandomPropertyRule() {
        @SuppressWarnings("removal")
        @Override
        public int getVariantSuffixFromThisCase(final int seed) {
            return 1;
        }

        @Override
        public int getVariantSuffixFromThisCase(ETFEntityRenderState entity) {
            return 1;
        }

        @Override
        public boolean doesEntityMeetConditionsOfThisCase(final ETFEntityRenderState etfEntity, final boolean isUpdate, final EntityBooleanLRU UUID_CaseHasUpdateablesCustom) {
            return true;
        }
    };

    private RandomPropertyRule() {
        ruleNumber = 0; // used for rule matching settings
        propertyFile = "default setter";
        suffixNumbers = new Integer[]{1};
        propertiesToTest = new RandomProperty[]{};
        ruleAlwaysApproved = true;
        updates = false;
        weights = null;
        weightTotal = 0;
        seedOffset = 0;
        seedSource = SeedSource.entity;
    }

    public boolean isAlwaysMet(){
        return ruleAlwaysApproved;
    }


    public RandomPropertyRule(
            String propertiesFile,
            int ruleNumber,
            Integer[] suffixes,
            Integer[] weights,
            int seedOffset,
            @Nullable String seedSource,
            RandomProperty... properties
    ) {
        propertyFile = propertiesFile;
        this.ruleNumber = ruleNumber;
        propertiesToTest = properties;
        ruleAlwaysApproved = properties.length == 0;

        this.seedOffset = seedOffset;
        if (seedSource == null || seedSource.isBlank()) {
            this.seedSource = SeedSource.entity;
        } else {
            SeedSource source = SeedSource.entity;
            try {
                source = SeedSource.valueOf(seedSource);
            } catch (IllegalArgumentException e) {
                ETFUtils2.logWarn("Random Property file [" + propertyFile + "] rule # [" + ruleNumber + "] has invalid seed source [" + seedSource + "], ignoring'");
            }
            this.seedSource = source;
        }

        suffixNumbers = suffixes;

        if (weights == null || weights.length == 0) {
            this.weights = null;
            weightTotal = 0;
        } else /*if (weights.length == suffixes.length)*/ {

            if(weights.length != suffixes.length) {
                Integer[] weightsFinal = new Integer[suffixes.length];
                int smaller = Math.min(weights.length, suffixes.length);
                System.arraycopy(weights, 0, weightsFinal, 0, smaller);

                if (weights.length >= suffixes.length) {
                    ETFUtils2.logWarn("Random Property file [" + propertyFile + "] rule # [" + this.ruleNumber + "] has more weights than suffixes, trimming to match");
                } else {
                    ETFUtils2.logWarn("Random Property file [" + propertyFile + "] rule # [" + this.ruleNumber + "] has more suffixes than weights, expanding to match");
                    int avgWeight = Arrays.stream(weights).mapToInt(Integer::intValue).sum() / weights.length;
                    for (int i = weights.length; i < weightsFinal.length; i++) {
                        weightsFinal[i] = avgWeight;
                    }
                }
                weights = weightsFinal;
            }

            int total = 0;
            this.weights = new Integer[weights.length];
            for (int i = 0; i < weights.length; i++) {
                Integer weight = weights[i];
                if (weight < 0) {
                    total = 0;
                    break;
                }
                total += weight;
                this.weights[i] = total;
            }
            weightTotal = total;
        }
        updates = Arrays.stream(propertiesToTest).anyMatch(RandomProperty::canPropertyUpdate);
    }

    public Set<Integer> getSuffixSet() {
        return new HashSet<>(List.of(suffixNumbers));
    }

    public boolean doesEntityMeetConditionsOfThisCase(ETFEntityRenderState etfEntity, boolean isUpdate, EntityBooleanLRU UUID_CaseHasUpdateablesCustom) {
        if (ruleAlwaysApproved) return true;
        if (etfEntity == null) return false;
        if (updates && UUID_CaseHasUpdateablesCustom != null) {
            UUID_CaseHasUpdateablesCustom.put(etfEntity.uuid(), true);
        }

        try {
            for (RandomProperty property : propertiesToTest) {
                if (!property.testEntity(etfEntity, isUpdate)) return false;
            }
            return true;
        } catch (Exception e) {
            ETFUtils2.logWarn("Random Property file [" + propertyFile + "] rule # [" + ruleNumber + "] failed with Exception:\n" + e.getMessage());
            // fail this test
            return false;
        }
    }

    @Deprecated(forRemoval = true) // Old non seed modified version, don't think i use it anywhere anymore
    public int getVariantSuffixFromThisCase(int seed) {
        if (weightTotal == 0){
            return suffixNumbers[Math.abs(seed) % suffixNumbers.length];
        }else{
            int seedValue = Math.abs(seed) % weightTotal;
            for (int i = 0; i < weights.length; i++) {
                if (seedValue < weights[i]) {
                    return suffixNumbers[i];
                }
            }
            return 0;
        }
    }

    public int getVariantSuffixFromThisCase(ETFEntityRenderState entity) {
        int seed = getSeedFrom(entity);
        if (weightTotal == 0){
            return suffixNumbers[Math.abs(seed) % suffixNumbers.length];
        }else{
            int seedValue = Math.abs(seed) % weightTotal;
            for (int i = 0; i < weights.length; i++) {
                if (seedValue < weights[i]) {
                    return suffixNumbers[i];
                }
            }
            return 0;
        }
    }

    private int getSeedFrom(ETFEntityRenderState entity) {
        int seed = seedSource == SeedSource.entity
                ? entity.optifineId()
                : entity.optifineVehicleId();
        if (seedOffset != 0) seed ^= ETFUtils2.optifineHashing(seedOffset);
        return seed;
    }

    public void cacheEntityInitialResultsOfNonUpdatingProperties(ETFEntityRenderState entity) {
        for (RandomProperty property : propertiesToTest) {
            if (!property.canPropertyUpdate()) {
                try {
                    property.cacheEntityInitialResult(entity);
                } catch (Exception ignored) { }
            }
        }
    }

    private enum SeedSource {
        entity, vehicle
    }

}
