package traben.entity_texture_features.property_reading.properties.optifine_properties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;

import java.util.Properties;

public class ColorProperty extends StringArrayOrRegexProperty {


    protected ColorProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "colors","collarColors"));
    }

    public static ColorProperty getPropertyOrNull(Properties properties, int propertyNum){
        try {
            return new ColorProperty(properties, propertyNum);
        }catch(RandomPropertyException e){
            return null;
        }
    }


    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    @Nullable
    protected String getValueFromEntity(ETFEntity entityETF) {
        Entity entity = entityETF.entity();
        if(entity != null) {
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
    public boolean isPropertyUpdatable(){
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"colors","collarColors"};
    }

}
