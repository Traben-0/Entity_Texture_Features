package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import net.minecraft.entity.mob.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.ETFEntity;

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

        if (etfEntity != null) {
            if (etfEntity instanceof EndermanEntity enderman) {
                return enderman.isAngry();
            } else if (etfEntity instanceof BlazeEntity blaze) {
                return blaze.isOnFire();
            } else if (etfEntity instanceof GuardianEntity guardian) {
                return guardian.getBeamTarget() != null;
            } else if (etfEntity instanceof VindicatorEntity vindicator) {
                return vindicator.isAttacking();
            } else if (etfEntity instanceof SpellcastingIllagerEntity caster) {
                return caster.isSpellcasting();
            } else if (etfEntity instanceof Angerable angry) {
                return angry.hasAngerTime();
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
