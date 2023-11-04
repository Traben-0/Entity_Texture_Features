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
import traben.entity_texture_features.config.screens.warnings.ETFConfigWarning;
import traben.entity_texture_features.config.screens.warnings.ETFConfigWarnings;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.property_reading.RandomPropertiesFileHandler;
import traben.entity_texture_features.texture_features.property_reading.RandomPropertyRule;
import traben.entity_texture_features.texture_features.property_reading.properties.RandomProperties;
import traben.entity_texture_features.texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.texture_features.property_reading.properties.optifine_properties.BabyProperty;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.entity_wrappers.ETFBlockEntityWrapper;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

/**
 * ETF's api for external mod access (primarily puzzle and EMF at this time)
 *
 * @API_Version {@link ETFApi#ETFApiVersion}
 * @config_handling These methods allow for retrieving, editing, replacing, and saving ETF's config<p>
 * {@link ETFApi#getETFConfigObject()}<p>
 * {@link ETFApi#getCopyOfETFConfigObject()} <p>
 * {@link ETFApi#getDefaultETFConfigObject()} <p>
 * {@link ETFApi#setETFConfigObject(ETFConfig)} <p>
 * {@link ETFApi#saveETFConfigChangesAndResetETF()}
 * @resetting_ETF API method that will reset ETF to recalculate after any changes<p>
 * {@link ETFApi#resetETF()}
 * @get_variant_textures API methods for returning valid variant textures given an Entity, and it's default texture<p>
 * {@link ETFApi#getCurrentETFVariantTextureOfEntity(Entity, Identifier)} <p>
 * {@link ETFApi#getCurrentETFVariantTextureOfBlockEntity(BlockEntity, Identifier, Integer)}
 * @get_emissive_textures API methods for returning valid emissive textures given an Entity, and it's default texture<p>
 * These methods will first find the variant of the texture and then find that variants emissive texture<p>
 * {@link ETFApi#getCurrentETFEmissiveTextureOfEntityOrNull(Entity, Identifier)} <p>
 * {@link ETFApi#getCurrentETFEmissiveTextureOfBlockEntityOrNull(BlockEntity, Identifier, Integer)}
 * @automatic_emissive_rendering These methods will automatically find an entities emissive texture and render it on a given Model | ModelPart <p>
 * These methods should be placed directly after the initial models render, and before the MatrixStack's modifications are popped or changed<p>
 * {@link ETFApi#renderETFEmissiveModel(Entity, Identifier, MatrixStack, VertexConsumerProvider, Model)} <p>
 * {@link ETFApi#renderETFEmissiveModel(BlockEntity, Integer, Identifier, MatrixStack, VertexConsumerProvider, Model)}<p>
 * {@link ETFApi#renderETFEmissiveModelPart(Entity, Identifier, MatrixStack, VertexConsumerProvider, ModelPart)} <p>
 * {@link ETFApi#renderETFEmissiveModelPart(BlockEntity, Integer, Identifier, MatrixStack, VertexConsumerProvider, ModelPart)}
 * @adding_custom_optifine_properties This method allows registering custom {@link RandomProperty} objects to be included in OptiFine property file testing.<p>
 * {@link ETFApi#registerCustomRandomPropertyFactory(String, RandomProperties.RandomPropertyFactory...)}
 * @adding_custom_warnings This method allows registering custom {@link ETFConfigWarning} objects to be included in the ETF config screens and mod compatibility fixes.<p>
 * {@link ETFApi#registerCustomETFConfigWarning(String, ETFConfigWarning...)}
 * @recent_rule_matches These methods allow retrieving the latest matched OptiFine property rule index of a given entity.<p>
 * {@link ETFApi#getLastMatchingRuleOfEntity(Entity)}<p>
 * {@link ETFApi#getLastMatchingRuleOfBlockEntity(BlockEntity, Integer)}
 * @optifine_property_reading This method allows an external mod to send in the path of an OptiFine random properties file and return an object<p>
 * that can test specific entities to discover their assigned random suffix<p>
 * {@link ETFApi#readRandomPropertiesFileAndReturnTestingObject3(Identifier, String...)} <p>
 * {@link ETFRandomTexturePropertyInstance}
 */
@SuppressWarnings({"unused", "ConstantValue"})
public final class ETFApi {

    /**
     * The current ETF API version.
     */
    final public static int ETFApiVersion = 8;
    @Deprecated
    public static ETFConfig getETFConfigObject = null;

    /**
     * provides access to the live ETF config object to read AND modify its values
     * please be sure to run the save config method below after any changes
     *
     * @return the etf config object
     */
    public static ETFConfig getETFConfigObject() {
        return ETFClientCommon.ETFConfigData;
    }

    /**
     * sets the current config object used by etf, this will also save the config and reset etf
     *
     * @param newETFConfig the new ETF config to be saved and used by ETF
     */
    public static void setETFConfigObject(ETFConfig newETFConfig) {
        ETFClientCommon.ETFConfigData = newETFConfig;
        saveETFConfigChangesAndResetETF();
    }

    /**
     * returns a copy of the ETF config object that can be freely modified without affecting ETF in runtime
     *
     * @return the copy of ETF's current config object
     */
    public static ETFConfig getCopyOfETFConfigObject() {
        return ETFConfig.copyFrom(ETFClientCommon.ETFConfigData);
    }

    /**
     * returns a new ETF config object with default settings
     *
     * @return a default ETF config object
     */
    public static ETFConfig getDefaultETFConfigObject() {
        return new ETFConfig();
    }

    /**
     * saves any config changes to the live ETF config to file and resets ETF to function with the new settings
     */
    public static void saveETFConfigChangesAndResetETF() {
        ETFUtils2.saveConfig();
        ETFManager.resetInstance();
    }

    /**
     * resets ETF in its entirety, ETF will re asses all textures and properties files and recalculate all variants
     */
    public static void resetETF() {
        ETFManager.resetInstance();
    }


    /**
     * pass in an entity, and its default texture and receive the current variant of that texture or the default if no variant exists
     *
     * @param entity         the entity
     * @param defaultTexture the default texture
     * @return the variant texture or the defaultTexture if no variant exists
     */
    @NotNull
    public static Identifier getCurrentETFVariantTextureOfEntity(@NotNull Entity entity, @NotNull Identifier defaultTexture) {
        if (entity != null) {
            ETFEntityWrapper etfEntity = new ETFEntityWrapper(entity);
            ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTexture, etfEntity, ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
            if (etfTexture != null) {// just in case
                Identifier etfIdentifier = etfTexture.getTextureIdentifier(etfEntity);
                if (etfIdentifier != null) {// just in case
                    return etfIdentifier;
                }
            }
        }
        return defaultTexture;
    }

    /**
     * pass in a block entity, and its default texture and receive the current variant of that texture or the default if no variant exists
     *
     * @param entity          the entity
     * @param defaultTexture  the default texture
     * @param hashToAddToUUID Nullable hash value that will be added to ETF's auto generated block entity uuid.
     *                        (send the hash of any extra identifying type parameter your block entity might have, e.g. facing direction)
     * @return the variant texture or the defaultTexture if no variant exists
     */
    public static Identifier getCurrentETFVariantTextureOfBlockEntity(@NotNull BlockEntity entity, @NotNull Identifier defaultTexture, @Nullable Integer hashToAddToUUID) {
        if (entity != null) {
            ETFBlockEntityWrapper etfEntity = new ETFBlockEntityWrapper(entity, hashToAddToUUID);
            return getCurrentETFVariantTextureOfBlockEntityInternal(etfEntity,defaultTexture);
        }
        return defaultTexture;
    }

    public static Identifier getCurrentETFVariantTextureOfBlockEntity(@NotNull BlockEntity entity, @NotNull Identifier defaultTexture, @NotNull UUID specifiedUUID) {
        if (entity != null) {
            ETFBlockEntityWrapper etfEntity = new ETFBlockEntityWrapper(entity, specifiedUUID);
            return getCurrentETFVariantTextureOfBlockEntityInternal(etfEntity,defaultTexture);
        }
        return defaultTexture;
    }

    private static Identifier getCurrentETFVariantTextureOfBlockEntityInternal(@NotNull ETFBlockEntityWrapper etfEntity, @NotNull Identifier defaultTexture) {
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTexture, etfEntity, ETFManager.TextureSource.BLOCK_ENTITY, ETFConfigData.removePixelsUnderEmissiveBlockEntity);
        if (etfTexture != null) {// just in case
            Identifier etfIdentifier = etfTexture.getTextureIdentifier(etfEntity);
            if (etfIdentifier != null) {// just in case
                return etfIdentifier;
            }
        }
        return defaultTexture;
    }

    @NotNull
    @Deprecated
    public static Identifier getCurrentETFVariantTextureOfEntity(@NotNull BlockEntity entity, @NotNull Identifier defaultTexture, UUID ignore) {
        return getCurrentETFVariantTextureOfBlockEntity(entity, defaultTexture, ignore.hashCode());
    }

    /**
     * pass in an entity, and it's default texture and receive it's current emissive texture if it exists else returns null
     *
     * @param entity         the entity
     * @param defaultTexture the default texture
     * @return the emissive texture or null if it doesn't exist
     */
    @Nullable
    public static Identifier getCurrentETFEmissiveTextureOfEntityOrNull(@NotNull Entity entity, @NotNull Identifier defaultTexture) {
        if (entity != null) {
            ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTexture, new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
            if (etfTexture != null) {// just in case
                return etfTexture.getEmissiveIdentifierOfCurrentState();
            }
        }
        return null;
    }

    /**
     * pass in an entity, and it's default texture and receive it's current emissive texture if it exists else returns null
     *
     * @param entity          the entity
     * @param defaultTexture  the default texture
     * @param hashToAddToUUID Nullable hash value that will be added to ETF's auto generated block entity uuid.
     * @return the emissive texture or null if it doesn't exist
     */
    @Nullable
    public static Identifier getCurrentETFEmissiveTextureOfBlockEntityOrNull(@NotNull BlockEntity entity, @NotNull Identifier defaultTexture, @Nullable Integer hashToAddToUUID) {
        if (entity != null) {
            ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTexture, new ETFBlockEntityWrapper(entity, hashToAddToUUID), ETFManager.TextureSource.BLOCK_ENTITY, ETFConfigData.removePixelsUnderEmissiveBlockEntity);
            if (etfTexture != null) {// just in case
                return etfTexture.getEmissiveIdentifierOfCurrentState();
            }
        }
        return null;
    }

    /**
     * To render your entity using its emissive textures you can simply call this method sometime after
     * your entity model is rendered, but before you pop or modify the matrix stack
     *
     * @param entity                 the entity
     * @param defaultTextureOfEntity the default (non-variant) texture of entity
     * @param matrixStack            the matrix stack
     * @param vertexConsumerProvider the vertex consumer provider
     * @param model                  the model to render
     */
    public static void renderETFEmissiveModel(
            @NotNull Entity entity,
            @NotNull Identifier defaultTextureOfEntity,
            @NotNull MatrixStack matrixStack,
            @NotNull VertexConsumerProvider vertexConsumerProvider,
            @NotNull Model model
    ) {
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTextureOfEntity, new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if (etfTexture != null) {// just in case
            etfTexture.renderEmissive(matrixStack, vertexConsumerProvider, model);
        }
    }

    /**
     * same method as {@link ETFApi#renderETFEmissiveModel(Entity, Identifier, MatrixStack, VertexConsumerProvider, Model)} but for an individual ModelPart
     *
     * @param entity                 the entity
     * @param defaultTextureOfEntity the default (non-variant) texture of entity
     * @param matrixStack            the matrix stack
     * @param vertexConsumerProvider the vertex consumer provider
     * @param modelPart              the model part to render
     */
    public static void renderETFEmissiveModelPart(
            @NotNull Entity entity,
            @NotNull Identifier defaultTextureOfEntity,
            @NotNull MatrixStack matrixStack,
            @NotNull VertexConsumerProvider vertexConsumerProvider,
            @NotNull ModelPart modelPart
    ) {
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTextureOfEntity, new ETFEntityWrapper(entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if (etfTexture != null) {// just in case
            etfTexture.renderEmissive(matrixStack, vertexConsumerProvider, modelPart);
        }
    }

    /**
     * To render your entity using its emissive textures you can simply call this method sometime after
     * your entity model is rendered, but before you pop or modify the matrix stack
     *
     * @param entity                 the entity
     * @param hashToAddToUUID        Nullable hash value that will be added to ETF's auto generated block entity uuid.
     * @param defaultTextureOfEntity the default (non-variant) texture of entity
     * @param matrixStack            the matrix stack
     * @param vertexConsumerProvider the vertex consumer provider
     * @param model                  the model to render
     */
    public static void renderETFEmissiveModel(
            @NotNull BlockEntity entity,
            @Nullable Integer hashToAddToUUID,
            @NotNull Identifier defaultTextureOfEntity,
            @NotNull MatrixStack matrixStack,
            @NotNull VertexConsumerProvider vertexConsumerProvider,
            @NotNull Model model
    ) {
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTextureOfEntity, new ETFBlockEntityWrapper(entity, hashToAddToUUID), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if (etfTexture != null) {// just in case
            etfTexture.renderEmissive(matrixStack, vertexConsumerProvider, model);
        }
    }

    /**
     * same method as {@link ETFApi#renderETFEmissiveModel(Entity, Identifier, MatrixStack, VertexConsumerProvider, Model)} but for an individual ModelPart
     *
     * @param entity                 the entity
     * @param hashToAddToUUID        Nullable hash value that will be added to ETF's auto generated block entity uuid.
     * @param defaultTextureOfEntity the default (non-variant) texture of entity
     * @param matrixStack            the matrix stack
     * @param vertexConsumerProvider the vertex consumer provider
     * @param modelPart              the model part to render
     */
    public static void renderETFEmissiveModelPart(
            @NotNull BlockEntity entity,
            @Nullable Integer hashToAddToUUID,
            @NotNull Identifier defaultTextureOfEntity,
            @NotNull MatrixStack matrixStack,
            @NotNull VertexConsumerProvider vertexConsumerProvider,
            @NotNull ModelPart modelPart
    ) {
        ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(defaultTextureOfEntity, new ETFBlockEntityWrapper(entity, hashToAddToUUID), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        if (etfTexture != null) {// just in case
            etfTexture.renderEmissive(matrixStack, vertexConsumerProvider, modelPart);
        }
    }

    /**
     * This should only be used if you want to handle your own variation code.
     * This is used by EMF for random model variations.
     * <p>.<p>
     * creates an instance of {@link ETFRandomTexturePropertyInstance}.
     * this method will return null for any failure and should print some relevant information on the failure reason.
     * the return from this method only requires
     * {@link ETFRandomTexturePropertyInstance#getSuffixForEntity(Entity, boolean, Object2BooleanOpenHashMap)}
     * to be called to retrieve a suffix integer,
     * the suffix may or may not exist, it is up to you to test if the file with that suffix actually exists.
     * see the JavaDocs within the object itself for further info.
     *
     * @param propertiesFileIdentifier the properties file identifier
     * @param suffixKeys               the suffix keys to use. These would be {"skins","textures"} for regular OptiFine random textures and "models" for OptiFine random entity models.
     * @return a valid {@link ETFRandomTexturePropertyInstance} or null.
     */
    public static ETFRandomTexturePropertyInstance readRandomPropertiesFileAndReturnTestingObject3(Identifier propertiesFileIdentifier, String... suffixKeys) {
        return ETFRandomTexturePropertyInstance.getInstance(propertiesFileIdentifier, suffixKeys);
    }

    @Deprecated //remove once EMF is updated
    public static ETFRandomTexturePropertyInstance readRandomPropertiesFileAndReturnTestingObject2(Identifier propertiesFileIdentifier, String suffixKey) {
        return readRandomPropertiesFileAndReturnTestingObject3(propertiesFileIdentifier, suffixKey);
    }

    /**
     * Used by EMF.
     *
     * @param entity entity to get the latest rule matching for.
     * @return Integer index of the most recent random property rule to be matched.<p>
     * default value = 0
     */
    public static int getLastMatchingRuleOfEntity(Entity entity) {
        Integer ruleIndex = ETFManager.getInstance().LAST_MET_RULE_INDEX.get(entity.getUuid());
        return ruleIndex == null ? 0 : ruleIndex;
    }

    /**
     * Used by EMF.
     *
     * @param entity          block entity to get the latest rule matching for.
     * @param hashToAddToUUID Nullable hash value that will be added to ETF's auto generated block entity uuid.
     * @return Integer index of the most recent random property rule to be matched.<p>
     * default value = 0
     */
    private static int getLastMatchingRuleOfBlockEntity(BlockEntity entity, @Nullable Integer hashToAddToUUID) {
        Integer ruleIndex = ETFManager.getInstance().LAST_MET_RULE_INDEX.get(ETFBlockEntityWrapper.getUUIDForBlockEntity(entity, hashToAddToUUID));
        return ruleIndex == null ? 0 : ruleIndex;
    }

    /**
     * Register new {@link RandomProperty} objects to be included in OptiFine property file testing.
     * <p>For example {@link BabyProperty#getPropertyOrNull(Properties, int)}
     * is the factory sent here for the object {@link BabyProperty}
     * representing the "baby" OptiFine property.
     *
     * @param yourModId your Mod's ID
     * @param factories the {@link RandomProperties.RandomPropertyFactory} of your custom {@link RandomProperty} object to be registered.
     * @usage_examples {@link RandomProperties}
     */
    public static void registerCustomRandomPropertyFactory(String yourModId, RandomProperties.RandomPropertyFactory... factories) {
        if (factories != null && factories.length != 0) {
            RandomProperties.register(factories);
            ETFUtils2.logMessage(factories.length + " new ETF Random Properties registered by " + yourModId);
        }
    }

    /**
     * Pass in custom warnings to be displayed to ETF users, given certain predicates.
     *
     * @param yourModId your Mod's ID
     * @param warnings  one or more instance of {@link ETFConfigWarning} to be listed on the ETF config warning screen
     */
    public static void registerCustomETFConfigWarning(String yourModId, ETFConfigWarning... warnings) {
        if (warnings != null && warnings.length != 0) {
            ETFConfigWarnings.registerConfigWarning(warnings);
            ETFUtils2.logMessage(warnings.length + " new ETF Config Warnings registered by " + yourModId);
        }
    }

    /**
     * This should only be used if you want to handle your own variation code.
     * This is used by EMF for random model variations.
     * <p>.<p>
     * provides functionality to input an entity and output a suffix integer as defined in
     * a valid OptiFine random entity properties file.
     * <p>
     * Should only be built via {@link ETFApi#readRandomPropertiesFileAndReturnTestingObject3(Identifier, String...)}
     */
    public static class ETFRandomTexturePropertyInstance {
        protected final List<RandomPropertyRule> propertyCases;

        protected ETFRandomTexturePropertyInstance(List<RandomPropertyRule> etfs) {
            propertyCases = etfs;
        }

        @Nullable
        private static ETFRandomTexturePropertyInstance getInstance(Identifier propertiesFileIdentifier, String... suffixKeyName) {
            Properties props = ETFUtils2.readAndReturnPropertiesElseNull(propertiesFileIdentifier);
            if (props == null) return null;
            List<RandomPropertyRule> etfs = RandomPropertiesFileHandler.getAllValidPropertyObjects(props, propertiesFileIdentifier, suffixKeyName);
            if (etfs.isEmpty()) return null;
            return new ETFRandomTexturePropertyInstance(etfs);

        }

        /**
         * This method will accept an entity and some additional args and will output a variant suffix integer that matches
         * the OptiFine cases outlined in the properties file, it ONLY outputs an integer, testing whether that
         * variant number exists or not is up to you.
         *
         * @param entityToBeTested                              the entity to be tested
         * @param isThisTheFirstTestForEntity                   provides context to allow for faster iterations when an entity needs to be
         *                                                      repeatedly tested, as is the case with the health property, since it can change over time you must retest the
         *                                                      entity occasionally, the boolean should be true the first time a specific entity is sent to this method,
         *                                                      and false every time thereafter.
         * @param cacheToMarkEntitiesWhoseVariantCanChangeAgain a FastUtil type of Map<UUID,boolean>. this map is for your own optimization usage as
         *                                                      the method will put a boolean into the map to mark whether an entity ever needs to be updated again.
         *                                                      if the entity has no update-able properties (like health) it will never need to be tested again, so you can
         *                                                      check this map to skip testing it if it's not needed.
         *                                                      if the map returns [true] then that entity can possibly update, and you should retest it periodically, if the
         *                                                      map returns [false] then the entity will never change its suffix, and you can skip testing it.
         *                                                      the map can simply be hard coded [null] if you do not care.
         * @return the suffix number for this entity. An output of 0 ALWAYS means you need to use the vanilla variant,
         * usually due to finding no match.
         * <p> An output of 1, can be handled in 2 ways, usually it is used to refer to the vanilla suffix, but you might
         * also choose to check for a #1 suffix, I would recommend using 1 to mean the vanilla/default variant.
         */
        public int getSuffixForEntity(Entity entityToBeTested, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
            if (entityToBeTested == null) return 0;
            boolean isAnUpdate = !isThisTheFirstTestForEntity;
            for (RandomPropertyRule testCase : propertyCases) {
                ETFEntity entity = new ETFEntityWrapper(entityToBeTested);
                if (testCase.doesEntityMeetConditionsOfThisCase(entity, isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain)) {
                    return testCase.getVariantSuffixFromThisCase(entity.getUuid());
                }
            }
            return 0;
        }

        /**
         * Same as {@link ETFRandomTexturePropertyInstance#getSuffixForEntity(Entity, boolean, Object2BooleanOpenHashMap)} but for block entities
         *
         * @param entityToBeTested                              the block entity to be tested
         * @param hashToAddToUUID                               Nullable hash value that will be added to ETF's auto generated block entity uuid.
         * @param isThisTheFirstTestForEntity                   boolean that is true for every entities first test
         * @param cacheToMarkEntitiesWhoseVariantCanChangeAgain the cache to mark entities whose variant can change again
         * @return the suffix number for the block entity
         */
        public int getSuffixForBlockEntity(BlockEntity entityToBeTested, @Nullable Integer hashToAddToUUID, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
            if (entityToBeTested == null) return 0;
            return getBlockEntityLogicInternal(new ETFBlockEntityWrapper(entityToBeTested,hashToAddToUUID), isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain);

        }

        public int getSuffixForBlockEntity(BlockEntity entityToBeTested, UUID specifiedUUID, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
            if (entityToBeTested == null || specifiedUUID == null) return 0;
            return getBlockEntityLogicInternal(new ETFBlockEntityWrapper(entityToBeTested,specifiedUUID), isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain);
        }

        private int getBlockEntityLogicInternal(ETFEntity entity, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain){
            boolean isAnUpdate = !isThisTheFirstTestForEntity;
            for (RandomPropertyRule testCase : propertyCases) {
                if (testCase.doesEntityMeetConditionsOfThisCase(entity, isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain)) {
                    return testCase.getVariantSuffixFromThisCase(entity.getUuid());
                }
            }
            return 0;
        }



    }


}
