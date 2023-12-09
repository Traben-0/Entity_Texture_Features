package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class ColorProperty extends StringArrayOrRegexProperty {


    protected ColorProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "colors", "collarColors"));
    }

    public static ColorProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ColorProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }


    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    @Nullable
    protected String getValueFromEntity(ETFEntity entity) {
        if (entity != null) {
            if (entity instanceof WolfEntity wolf) {
                return wolf.getCollarColor().getName();
            } else if (entity instanceof SheepEntity sheep) {
                return sheep.getColor().getName();
            } else if (entity instanceof LlamaEntity llama) {
                DyeColor str = llama.getCarpetColor();
                if (str != null) {
                    return str.getName();
                }
            } else if (entity instanceof CatEntity cat) {
                return cat.getCollarColor().getName();
            } else if (entity instanceof ShulkerEntity shulker) {
                DyeColor str = shulker.getColor();
                if (str != null) {
                    return str.getName();
                }
            } else if (entity instanceof TropicalFishEntity fishy) {
                DyeColor str = TropicalFishEntity.getBaseDyeColor(fishy.getVariant().getId());
                if (str != null) {
                    return str.getName();
                }
            }
        }
        return null;
    }


    @Override
    public boolean isPropertyUpdatable() {
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"colors", "collarColors"};
    }

}
