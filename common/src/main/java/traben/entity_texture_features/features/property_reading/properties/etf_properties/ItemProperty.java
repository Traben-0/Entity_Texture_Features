package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import net.minecraft.world.item.ItemStack;

public class ItemProperty extends StringArrayOrRegexProperty {


    protected ItemProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "items", "item").replaceAll("(?<=(^| ))minecraft:", ""));


    }

    public static ItemProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ItemProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    public boolean testEntityInternal(ETFEntity entity) {
        if (ARRAY.size() == 1
                && (ARRAY.stream().anyMatch((string) ->
                "none".equals(string) || "any".equals(string) || "holding".equals(string) || "wearing".equals(string)))) {
            if (ARRAY.contains("none")) {
                Iterable<ItemStack> equipped = entity.etf$getItemsEquipped();
                for (ItemStack item :
                        equipped) {
                    if (item != null && !item.isEmpty()) {
                        //found a valid item break and deny
                        return false;
                    }
                }
                return true;
            } else {
                Iterable<ItemStack> items;
                if (ARRAY.contains("any")) {//any
                    items = entity.etf$getItemsEquipped();
                } else if (ARRAY.contains("holding")) {
                    items = entity.etf$getHandItems();
                } else {//wearing
                    items = entity.etf$getArmorItems();
                }
                boolean found = false;
                for (ItemStack item :
                        items) {
                    if (item != null && !item.isEmpty()) {
                        //found a valid item break and resolve
                        found = true;
                        break;
                    }
                }
                return found;
            }
        } else {
            //specifically named item

            //both armour and hand held
            Iterable<ItemStack> equipped = entity.etf$getItemsEquipped();
            boolean found = false;
            for (ItemStack item :
                    equipped) {
                String itemString = item.getItem().toString().replaceFirst("^minecraft:", "");
                found = MATCHER.testString(itemString);
                if (found) break;
            }
            return found;
        }
    }

    @Override
    public @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"items", "item"};
    }

}
