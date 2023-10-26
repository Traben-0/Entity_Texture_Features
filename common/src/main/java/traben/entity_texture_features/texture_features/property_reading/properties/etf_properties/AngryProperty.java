package traben.entity_texture_features.texture_features.property_reading.properties.etf_properties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Properties;

public class AngryProperty extends BooleanProperty {


    protected AngryProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "angry", "isAngry", "is_angry", "aggressive", "is_aggressive"));
    }

    public static AngryProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new AngryProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity etfEntity) {
        Entity entity = etfEntity.getEntity();
        if (entity != null) {
            if (entity instanceof EndermanEntity) {
                return ((EndermanEntity) entity).isAngry();
            } else if (entity instanceof BlazeEntity) {
                return entity.isOnFire();
            } else if (entity instanceof GuardianEntity) {
                return (((GuardianEntity) entity).getBeamTarget() != null);
            } else if (entity instanceof VindicatorEntity) {
                return (((VindicatorEntity) entity).isAttacking());
            } else if (entity instanceof SpellcastingIllagerEntity) {
                return (((SpellcastingIllagerEntity) entity).isSpellcasting());
            } else if (entity instanceof Angerable) {
                return (((Angerable) entity).hasAngerTime());
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
        return new String[]{"angry", "isAngry", "is_angry", "aggressive", "is_aggressive"};
    }

}
