package traben.entity_texture_features;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.entity_handlers.ETFBlockEntityWrapper;
import traben.entity_texture_features.entity_handlers.ETFEntityWrapper;
import traben.entity_texture_features.property_reading.ETFTexturePropertyCase;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.property_reading.ETFTexturePropertiesUtils;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

// an api that will remain unchanged for external mod access (primarily puzzle and EMF at this time)
@SuppressWarnings("unused")
public class ETFApi {

    final public static int ETFApiVersion = 6;
    //provides access to the live ETF config object to read AND modify its values
    //please be sure to run the save config method below after any changes

    public static ETFConfig getETFConfigObject() {
        return ETFClientCommon.ETFConfigData;
    }

    //returns a copy of the ETF config object that can be freely modified without affecting ETF
    public static ETFConfig getCopyOfETFConfigObject() {
        return ETFConfig.copyFrom( ETFClientCommon.ETFConfigData);
    }

    //returns a new ETF config object with default settings
    public static ETFConfig getDefaultETFConfigObject() {
        return new ETFConfig();
    }

    //sets the current config object used by etf, this will also save the config and reset etf
    public static void setETFConfigObject(ETFConfig newETFConfig) {
        ETFClientCommon.ETFConfigData = newETFConfig;
        saveETFConfigChangesAndResetETF();
    }

    //static getter that simply provided an object pointer, doesn't work with newer config resetting method
    @Deprecated
    public static ETFConfig getETFConfigObject = new ETFConfig();

    //saves any config changes to file and resets ETF to function with the new settings
    public static void saveETFConfigChangesAndResetETF() {
        ETFUtils2.saveConfig();
        ETFManager.resetInstance();
    }

    //resets ETF in its entirety, ETF will re asses all textures and properties files and recalculate all variants
    public static void resetETF() {
        ETFManager.resetInstance();
    }


    // pass in an entity, and its default texture and receive the current variant of that texture or the default if no variant exists
    @NotNull
    public static Identifier getCurrentETFVariantTextureOfEntity(@NotNull Entity entity, @NotNull Identifier defaultTexture){
        ETFEntityWrapper etfEntity = new ETFEntityWrapper(entity);
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTexture, etfEntity, ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if(etfTexture != null) {// just in case
            Identifier etfIdentifier = etfTexture.getTextureIdentifier(etfEntity);
            if(etfIdentifier != null){// just in case
                return etfIdentifier;
            }
        }
        return defaultTexture;
    }

    // pass in a BlockEntity, and its default texture and receive the current variant of that texture or the default if no variant exists
    // block entities require a UUID to be handled correctly you can always generate a UUID from a string with
    // UUID.nameUUIDFromBytes("STRING".getBytes())
    // I recommend adding the BlockPos and facing direction values to the uuid STRING as well as any other identifiable data unique to that BlockEntity
    @NotNull
    public static Identifier getCurrentETFVariantTextureOfEntity(@NotNull BlockEntity entity, @NotNull Identifier defaultTexture, UUID uuidForBlockEntity){
        ETFBlockEntityWrapper etfEntity =new ETFBlockEntityWrapper(entity,uuidForBlockEntity);
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTexture, etfEntity, ETFManager.TextureSource.BLOCK_ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if(etfTexture != null) {// just in case
            Identifier etfIdentifier = etfTexture.getTextureIdentifier(etfEntity);
            if(etfIdentifier != null){// just in case
                return etfIdentifier;
            }
        }
        return defaultTexture;
    }

    // pass in an entity, and it's default texture and receive it's current emissive texture if it exists else returns null
    @Nullable
    public static Identifier getCurrentETFEmissiveTextureOfEntityOrNull(@NotNull Entity entity, @NotNull Identifier defaultTexture){
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTexture, new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if(etfTexture != null) {// just in case
            return etfTexture.getEmissiveIdentifierOfCurrentState();
        }
        return null;
    }

    // alternatively to render your entity using its emissive textures you can simply call this method sometime after
    // your entity is rendered, but before you pop or modify the matrix stack, NOTE: defaultTexture must be the default non variant texture
    public static void renderETFEmissiveModel(
            @NotNull Entity entity,
            @NotNull Identifier defaultTextureOfEntity,
            @NotNull MatrixStack matrixStack,
            @NotNull VertexConsumerProvider vertexConsumerProvider,
            @NotNull Model model
    ){
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTextureOfEntity, new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if(etfTexture != null) {// just in case
            etfTexture.renderEmissive(matrixStack,vertexConsumerProvider,model);
        }
    }

    //same methods as above but for an individual ModelPart
    public static void renderETFEmissiveModelPart(
            @NotNull Entity entity,
            @NotNull Identifier defaultTextureOfEntity,
            @NotNull MatrixStack matrixStack,
            @NotNull VertexConsumerProvider vertexConsumerProvider,
            @NotNull ModelPart modelPart
    ){
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTextureOfEntity, new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if(etfTexture != null) {// just in case
            etfTexture.renderEmissive(matrixStack,vertexConsumerProvider,modelPart);
        }
    }


    // returns the object below that provides functionality to input an entity and output a suffix integer as defined in
    // a valid OptiFine random entity properties file given in this method.
    // this method will return null for any failure and should print some relevant information on the failure reason.
    // the return from this method only requires object.getSuffixForEntity() to be called to retrieve a suffix integer,
    // the suffix may or may not be valid, it is up to you to test if the file with that suffix actually exists.
    // see the comments below within the object itself for further info.
    //
    //suffixKeyName would be "skins" for regular OptiFine random textures and "models" for OptiFine random entity models
    public static ETFRandomTexturePropertyInstance readRandomPropertiesFileAndReturnTestingObject2(Identifier propertiesFileIdentifier, String suffixKeyName) {
        return ETFRandomTexturePropertyInstance.getInstance(propertiesFileIdentifier, suffixKeyName);
    }

    public static class ETFRandomTexturePropertyInstance {
        @Nullable
        private static ETFRandomTexturePropertyInstance getInstance(Identifier propertiesFileIdentifier, String suffixKeyName) {
            Properties props = ETFUtils2.readAndReturnPropertiesElseNull(propertiesFileIdentifier);
            if (props == null) return null;
            List<ETFTexturePropertyCase> etfs = ETFTexturePropertiesUtils.getAllValidPropertyObjects(props, suffixKeyName, propertiesFileIdentifier);
            if (etfs.isEmpty()) return null;
            return new ETFRandomTexturePropertyInstance(etfs);

        }

        private ETFRandomTexturePropertyInstance(List<ETFTexturePropertyCase> etfs) {
            propertyCases = etfs;
        }

        private final List<ETFTexturePropertyCase> propertyCases;

        // this is the primary method of the object,
        // it will accept an entity and some additional args and will output a variant suffix integer that matches
        // the OptiFine cases outlined in the properties file, it ONLY outputs an integer, testing whether that
        // variant number exists or not is up to you.
        //
        // the boolean second arg provides the algorithm context to allow faster iterations when an entity needs to be
        // repeatedly tested, as is the case with the health property, since it can change over time you must retest the
        // entity occasionally, the boolean should be true the first time an entity is sent to this method,
        // and false every time thereafter, if you don't care about this just hard code it to [true].
        //
        // the third arg is an optimized type of Map<UUID,boolean>. this map is for your own optimization usage as
        // the method will put a boolean into the map to mark whether an entity ever needs to be updated again.
        // if the entity has no update-able properties (like health) it will never need to be tested again, so you can
        // check this map to skip testing it if it's not needed.
        // if the map returns [true] then that entity can possibly update, and you should retest it periodically, if the
        // map returns [false] then the entity will never change its suffix, and you can skip testing it.
        // the map can simply be hard coded [null] if you do not care.
        //
        // note an output of 0 ALWAYS means you need to use the vanilla variant, usually due to finding no match
        // an output of 1, can be handled in 2 ways, usually it is used to refer to the vanilla suffix, but you might
        // also choose to check for a #1 suffix, I would recommend using 1 to mean the vanilla/default variant.
        public int getSuffixForEntity(Entity entityToBeTested, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
            boolean isAnUpdate = !isThisTheFirstTestForEntity;
            for (ETFTexturePropertyCase testCase : propertyCases) {
                if (testCase.doesEntityMeetConditionsOfThisCase(entityToBeTested, isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain)){
                    return testCase.getAnEntityVariantSuffixFromThisCase(entityToBeTested.getUuid());
                }
            }
            return 0;
        }

        // same as above but valid for Block Entities, you must supply a UUID,
        // you can always generate a UUID from a string with UUID.nameUUIDFromBytes("STRING".getBytes())
        public int getSuffixForBlockEntity(BlockEntity entityToBeTested, UUID uuidForBlockEntity, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
            boolean isAnUpdate = !isThisTheFirstTestForEntity;
            for (ETFTexturePropertyCase testCase : propertyCases) {
                if (testCase.doesEntityMeetConditionsOfThisCase(entityToBeTested, uuidForBlockEntity, isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain)){
                    return testCase.getAnEntityVariantSuffixFromThisCase(uuidForBlockEntity);
                }
            }
            return 0;
        }
    }
}
