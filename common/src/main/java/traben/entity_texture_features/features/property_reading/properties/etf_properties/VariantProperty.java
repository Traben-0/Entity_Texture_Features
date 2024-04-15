package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Properties;

public class VariantProperty extends StringArrayOrRegexProperty {

    private final boolean doPrint;

    protected VariantProperty(String string) throws RandomPropertyException {
        super(string.replace("print:", ""));
        doPrint = string.startsWith("print:");
    }

    public static VariantProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new VariantProperty(readPropertiesOrThrow(properties, propertyNum, "variant", "variants"));
        } catch (RandomProperty.RandomPropertyException var3) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }

    @Override
    protected @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        String value = getValueFromEntityInternal(etfEntity);
        if (doPrint) {
            ETFUtils2.logMessage("[variant property print] = " + (value == null ? "//VARIANT CHECK FAILED AND WILL RETURN FALSE//" : value));
        }
        return value;
    }

    private @Nullable String getValueFromEntityInternal(ETFEntity etfEntity) {
        if (etfEntity instanceof Entity) {
            if (etfEntity instanceof CatEntity catEntity) {
                return catEntity.getVariant().toString();
            }
            //frog
            if (etfEntity instanceof FrogEntity frogEntity) {
                return frogEntity.getVariant().toString();
            }
            if (etfEntity instanceof PaintingEntity paintingEntity) {
                return paintingEntity.getVariant().toString();
            }
            if (etfEntity instanceof ShulkerEntity shulker) {
                if (shulker.getColor() == null) {
                    return null;
                }
                return shulker.getColor().toString();
            }
            if (etfEntity instanceof VillagerEntity villagerType) {
                return villagerType.getVillagerData().getType().toString();
            }


            return Registry.ENTITY_TYPE.getKey(((Entity) etfEntity).getType()).map(key -> key.getValue().getPath()).orElse(null);

        } else if (etfEntity instanceof BlockEntity) {
            if (etfEntity instanceof SignBlockEntity signBlockEntity
                    && signBlockEntity.getCachedState().getBlock() instanceof AbstractSignBlock abstractSignBlock) {
                return abstractSignBlock.getSignType().getName();
            }
            //todo move colors to color property in etf maybe?
            //it is actually useless in etf though, as they can already derive colour
            if (etfEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity
                    && shulkerBoxBlockEntity.getCachedState().getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock) {
                return String.valueOf(shulkerBoxBlock.getColor());
            }
            if (etfEntity instanceof BedBlockEntity bedBlockEntity
                    && bedBlockEntity.getCachedState().getBlock() instanceof BedBlock bedBlock) {
                return String.valueOf(bedBlock.getColor());
            }
//            if (etfEntity instanceof DecoratedPotBlockEntity pot) {
//                DecoratedPotBlockEntity.Sherds sherds = pot.getSherds();
//                return sherds.back().getTranslationKey() + "," +
//                        sherds.left().getTranslationKey() + "," +
//                        sherds.right().getTranslationKey() + "," +
//                        sherds.front().getTranslationKey();
//            }
            String suffix = "";
            if (etfEntity instanceof SkullBlockEntity skull) {
                suffix = "_direction_" + skull.getCachedState().get(SkullBlock.ROTATION).toString();
            }

            return Registry.BLOCK_ENTITY_TYPE.getKey(((BlockEntity) etfEntity).getType()).map(key -> key.getValue().getPath()).orElse(null) + suffix;
        }
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"variant", "variants"};
    }
}