package traben.entity_texture_features.property_reading;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFBlockEntityWrapper;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.entity_handlers.ETFEntityWrapper;
import traben.entity_texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ETFTexturePropertyCase {
    public final int PROPERTY_NUMBER;
    public final String PROPERTY_FILE;
    private final Integer[] SUFFIX_NUMBERS_WEIGHTED;

    private final RandomProperty[] PROPERTIES;
    private final boolean NO_PROPERTIES;

    public ETFTexturePropertyCase(
            String propertiesFile,
            int caseNumber,
            Integer[] suffixes,
            @Nullable Integer[] weights,
            RandomProperty... properties

    ) {
        PROPERTY_FILE = propertiesFile;
        PROPERTY_NUMBER = caseNumber;
        PROPERTIES = properties;

        if (weights == null || weights.length == 0) {
            SUFFIX_NUMBERS_WEIGHTED = suffixes;
        } else {
            if (weights.length == suffixes.length) {
                ArrayList<Integer> buildWeighted = new ArrayList<>();
                int index = 0;
                for (int suffix :
                        suffixes) {
                    Integer weightValue = weights[index];
                    if (weightValue != null) {
                        for (int i = 0; i < weightValue; i++) {
                            //adds the suffix as many times as it is weighted
                            buildWeighted.add(suffix);
                        }
                    }
                    index++;
                }
                SUFFIX_NUMBERS_WEIGHTED = buildWeighted.toArray(new Integer[0]);

            } else {
                ETFUtils2.logWarn("random texture weights don't match for [" +
                                            PROPERTY_FILE+"] case # ["+PROPERTY_NUMBER+"] :\n suffixes: " + Arrays.toString(suffixes) + "\n weights: " + Arrays.toString(weights), false);
                SUFFIX_NUMBERS_WEIGHTED = suffixes;
            }
        }
        NO_PROPERTIES = properties.length == 0;
    }

    public boolean doesEntityMeetConditionsOfThisCase(Entity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
        if(NO_PROPERTIES) return true;
        if(entity == null) return false;
        return doesEntityMeetConditionsOfThisCase(new ETFEntityWrapper(entity), isUpdate, UUID_CaseHasUpdateablesCustom);
    }

    public boolean doesEntityMeetConditionsOfThisCase(BlockEntity entity, UUID uuid, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
        if(NO_PROPERTIES) return true;
        if(entity == null) return false;
        return doesEntityMeetConditionsOfThisCase(new ETFBlockEntityWrapper(entity, uuid), isUpdate, UUID_CaseHasUpdateablesCustom);
    }

    public boolean doesEntityMeetConditionsOfThisCase(ETFEntity etfEntity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
        if (NO_PROPERTIES || etfEntity == null) {
            return true;
        }

        UUID id = etfEntity.getUuid();

        Object2BooleanOpenHashMap<String> spawnConditions;
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
                    PROPERTIES) {
                if (!entityMetRequirements) break;
                if (property.isPropertyUpdatable())
                    wasEntityTestedByAnUpdatableProperty = true;
                entityMetRequirements = property.testEntity(etfEntity, isUpdate, spawnConditions);
            }
        }catch (Exception e){
            ETFUtils2.logWarn("Random Property file ["+
                    PROPERTY_FILE+"] case # ["+PROPERTY_NUMBER+"] failed with Exception:\n"+e.getMessage());
            //fail this test
            entityMetRequirements = false;
            wasEntityTestedByAnUpdatableProperty = false;
        }

        if (wasEntityTestedByAnUpdatableProperty && UUID_CaseHasUpdateablesCustom != null) {
            UUID_CaseHasUpdateablesCustom.put(etfEntity.getUuid(), true);
        }
        return entityMetRequirements;
    }

    public int getVariantSuffixFromThisCase(UUID uuid) {
        int randomSeededByUUID = Math.abs(uuid.hashCode());
        return SUFFIX_NUMBERS_WEIGHTED[randomSeededByUUID % SUFFIX_NUMBERS_WEIGHTED.length];
    }

}
