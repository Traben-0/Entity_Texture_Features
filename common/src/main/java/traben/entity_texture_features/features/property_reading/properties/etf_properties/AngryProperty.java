package traben.entity_texture_features.features.property_reading.properties.etf_properties;


import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vindicator;
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
            if (etfEntity instanceof EnderMan enderman) return enderman.isCreepy();
            if (etfEntity instanceof Blaze blaze) return blaze.isOnFire();
            if (etfEntity instanceof Guardian guardian) return guardian.getActiveAttackTarget() != null;
            if (etfEntity instanceof Vindicator vindicator) return vindicator.isAggressive();
            if (etfEntity instanceof SpellcasterIllager caster) return caster.isCastingSpell();
            if (etfEntity instanceof NeutralMob angry) return angry.isAngry();
        }
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"angry", "isAngry", "is_angry", "aggressive", "is_aggressive"};
    }

}
