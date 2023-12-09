package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import net.minecraft.entity.passive.VillagerEntity;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class ProfessionProperty extends StringArrayOrRegexProperty {


    protected ProfessionProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "professions"));
    }


    public static ProfessionProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ProfessionProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    public boolean testEntityInternal(ETFEntity entity) {
        if (entity instanceof VillagerEntity villagerEntity) {
            String entityProfession = villagerEntity.getVillagerData().getProfession().toString().toLowerCase().replace("minecraft:", "");
            int entityProfessionLevel = villagerEntity.getVillagerData().getLevel();
            boolean check = false;
            for (String str :
                    ARRAY) {
                if (str != null) {
                    //str could be   librarian:1,3-4
                    str = str.toLowerCase().replaceAll("\\s*", "").replace("minecraft:", "");
                    //could be   "minecraft:cleric:1-4
                    if (str.contains(":")) {
                        //splits at seperator for profession level check only
                        String[] data = str.split(":\\d");
                        if (entityProfession.contains(data[0]) || data[0].contains(entityProfession)) {
                            //has profession now check level
                            if (data.length == 2) {
                                String[] levels = data[1].split(",");
                                ArrayList<Integer> levelData = new ArrayList<>();
                                for (String lvls :
                                        levels) {
                                    if (lvls.contains("-")) {
                                        levelData.addAll(Arrays.asList(SimpleIntegerArrayProperty.getIntRange(lvls).getAllWithinRangeAsList()));
                                    } else {
                                        levelData.add(Integer.parseInt(lvls.replaceAll("\\D", "")));
                                    }
                                }
                                //now check levels
                                for (Integer i :
                                        levelData) {
                                    if (i == entityProfessionLevel) {
                                        check = true;
                                        break;
                                    }
                                }
                            } else {
                                //no levels just send profession match confirmation
                                check = true;
                                break;
                            }
                        }
                    } else {
                        if (entityProfession.contains(str) || str.contains(entityProfession)) {
                            check = true;
                            break;
                        }
                    }
                }
            }
            return check;
        }
        return false;
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }

    @Override
    protected String getValueFromEntity(ETFEntity entity) {
        return null;
    }


    @Override
    public boolean isPropertyUpdatable() {
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"professions"};
    }

}
