package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import net.minecraft.world.entity.monster.Phantom;
//#if MC >= 26.2
//$$ import net.minecraft.world.entity.monster.cubemob.Slime;
//#else
import net.minecraft.world.entity.monster.Slime;
//#endif

public class SizeProperty extends SimpleIntegerArrayProperty {


    protected SizeProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "sizes", "size"));
    }


    public static SizeProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new SizeProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"sizes", "size"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        if (entity != null && entity.entity() instanceof
                //#if MC >= 26.2
                //$$ net.minecraft.world.entity.monster.cubemob.AbstractCubeMob slime // Changes for sulfur cube, now all 3 slimes share new generic type
                //#else
                Slime slime // Magma cube extends this in these versions
                //#endif
        ) {
            //magma cube too
            return slime.getSize() - 1;
        } else if (entity != null && entity.entity() instanceof Phantom phantom) {
            return phantom.getPhantomSize();
        }
        return 0;
    }
}
