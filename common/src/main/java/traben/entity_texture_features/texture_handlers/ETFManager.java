package traben.entity_texture_features.texture_handlers;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.ETFConfigScreenSkinTool;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.entity_handlers.ETFPlayerEntity;
import traben.entity_texture_features.entity_handlers.ETFPlayerEntityWrapper;
import traben.entity_texture_features.utils.*;

import java.util.*;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

// ETF re-write
//this class will ideally be where everything in vanilla interacts to get ETF stuff done
public class ETFManager {

    //public final ObjectOpenHashSet<String> EXCUSED_ILLEGAL_PATHS = new ObjectOpenHashSet<>();
    public static final UUID ETF_GENERIC_UUID = UUID.nameUUIDFromBytes(("GENERIC").getBytes());
    private static final ETFTexture ETF_ERROR_TEXTURE = getErrorETFTexture();
    public boolean zombiePiglinRightEarEnabled = false;
    private static ETFManager manager;
    /*
     * Storage reasoning
     *
     * for every storage map using an entity that cannot be stored in a fast-util primitive type
     * will utilise a cache that can clear contents after reaching certain sizes to prevent exceeding memory
     *
     * for every storage map keyed by a vanilla or confirmed existing texture they will remain as non clearing maps as they have an intrinsic upper size limit
     *
     *the rewrite relies heavily on minimizing processing time during play by
     *  setting up the textures once and then passing already calculated objects when required to speed up render time.
     * a big part of this is minimizing texture lookups and storing that info in fastUtil maps
     *
     */
    public final ObjectOpenHashSet<String> EMISSIVE_SUFFIX_LIST = new ObjectOpenHashSet<>();
    //trident entities do not send item name data to clients when thrown, this is to keep that name in memory so custom tridents can at least display until reloading
    public final Object2ReferenceOpenHashMap<UUID, String> UUID_TRIDENT_NAME = new Object2ReferenceOpenHashMap<>();
    public final ETFLruCache<ETFCacheKey, ETFTexture> ENTITY_TEXTURE_MAP = new ETFLruCache<>();
    public final ETFLruCache<UUID, ETFPlayerTexture> PLAYER_TEXTURE_MAP = new ETFLruCache<>();
    public final Object2LongOpenHashMap<UUID> ENTITY_BLINK_TIME = new Object2LongOpenHashMap<>();
    //private static final Object2ReferenceOpenHashMap<@NotNull UUID, @NotNull ETFTexture> ENTITY_TEXTURE_MAP = new Object2ReferenceOpenHashMap<>();
    public final Object2ObjectOpenHashMap<UUID, ETFCacheKey> UUID_TO_MOB_CACHE_KEY_MAP_FOR_FEATURE_USAGE = new Object2ObjectOpenHashMap<>();
    //todo extend this to as many isPresent() calls as possible to lower repeated resource manager calls, may need to consider LRUCache usage if this is expanded too greatly
    public final Object2BooleanOpenHashMap<Identifier> DOES_IDENTIFIER_EXIST_CACHED_RESULT = new Object2BooleanOpenHashMap<>();
    public final ArrayList<String> KNOWN_RESOURCEPACK_ORDER = new ArrayList<>();
    public final Object2IntOpenHashMap<EntityType<?>> ENTITY_TYPE_VANILLA_BRIGHTNESS_OVERRIDE_VALUE = new Object2IntOpenHashMap<>();
    public final ObjectOpenHashSet<EntityType<?>> ENTITY_TYPE_IGNORE_PARTICLES = new ObjectOpenHashSet<>();
    public final Object2IntOpenHashMap<EntityType<?>> ENTITY_TYPE_RENDER_LAYER = new Object2IntOpenHashMap<>();
    public final Object2ObjectOpenHashMap<Identifier, ETFTexture> TEXTURE_MAP_TO_OPPOSITE_ELYTRA = new Object2ObjectOpenHashMap<>();
    public final ETFLruCache<UUID, ObjectImmutableList<String>> ENTITY_SPAWN_CONDITIONS_CACHE = new ETFLruCache<>();
    //if false variant 1 will need to use vanilla texture otherwise vanilla texture has an override in other directory
    //private static final Object2BooleanOpenHashMap<Identifier> OPTIFINE_1_HAS_REPLACEMENT = new Object2BooleanOpenHashMap<>();
    //this is a cache of all known ETFTexture versions of any existing resource-pack texture, used to prevent remaking objects
    private final Object2ReferenceOpenHashMap<@NotNull Identifier, @Nullable ETFTexture> ETF_TEXTURE_CACHE = new Object2ReferenceOpenHashMap<>();
    //null means it is true random as in no properties
    public final Object2ReferenceOpenHashMap<Identifier, @Nullable List<ETFTexturePropertiesUtils.ETFTexturePropertyCase>> OPTIFINE_PROPERTY_CACHE = new Object2ReferenceOpenHashMap<>();
    private final Object2BooleanOpenHashMap<UUID> ENTITY_IS_UPDATABLE = new Object2BooleanOpenHashMap<>();
    private final ObjectOpenHashSet<UUID> ENTITY_UPDATE_QUEUE = new ObjectOpenHashSet<>();
    private final Object2ObjectOpenHashMap<UUID, ObjectOpenHashSet<ETFCacheKey>> ENTITY_KNOWN_FEATURES_LIST = new Object2ObjectOpenHashMap<>();
    private final ObjectOpenHashSet<UUID> ENTITY_DEBUG_QUEUE = new ObjectOpenHashSet<>();
    //contains the total number of variants for any given vanilla texture
    private final Object2IntOpenHashMap<Identifier> TRUE_RANDOM_COUNT_CACHE = new Object2IntOpenHashMap<>();
    //private final Object2LongOpenHashMap<UUID> LAST_PLAYER_CHECK_TIME = new Object2LongOpenHashMap<>();
    //private final Object2IntOpenHashMap<UUID> PLAYER_CHECK_COUNT = new Object2IntOpenHashMap<>();
    public int mooshroomBrownCustomShroom = 0;
    //marks whether mooshroom mushroom overrides exist
    public int mooshroomRedCustomShroom = 0;
    public Boolean lecternHasCustomTexture = null;
    public ETFTexture redMooshroomAlt = null;
    public ETFTexture brownMooshroomAlt = null;


    private ETFManager() {


        for (ResourcePack pack :
                MinecraftClient.getInstance().getResourceManager().streamResourcePacks().toList()) {
            KNOWN_RESOURCEPACK_ORDER.add(pack.getName());
        }

        try {
            List<Properties> props = new ArrayList<>();
            String[] paths = {"optifine/emissive.properties", "textures/emissive.properties", "etf/emissive.properties"};
            for (String path :
                    paths) {
                Properties prop = ETFUtils2.readAndReturnPropertiesElseNull(new Identifier(path));
                if (prop != null)
                    props.add(prop);
            }
            for (Properties prop :
                    props) {
                //not an optifine property that I know of but this has come up in a few packs, so I am supporting it
                if (prop.containsKey("entities.suffix.emissive")) {
                    if (prop.getProperty("entities.suffix.emissive") != null)
                        EMISSIVE_SUFFIX_LIST.add(prop.getProperty("entities.suffix.emissive"));
                }
                if (prop.containsKey("suffix.emissive")) {
                    if (prop.getProperty("suffix.emissive") != null)
                        EMISSIVE_SUFFIX_LIST.add(prop.getProperty("suffix.emissive"));
                }
            }
            if (ETFConfigData.alwaysCheckVanillaEmissiveSuffix) {
                EMISSIVE_SUFFIX_LIST.add("_e");
            }

            if (EMISSIVE_SUFFIX_LIST.isEmpty()) {
                ETFUtils2.logMessage("no emissive suffixes found: default emissive suffix '_e' used");
                EMISSIVE_SUFFIX_LIST.add("_e");
            } else {
                ETFUtils2.logMessage("emissive suffixes loaded: " + EMISSIVE_SUFFIX_LIST);
            }
        } catch (Exception e) {
            ETFUtils2.logError("emissive suffixes could not be read: default emissive suffix '_e' used");
            EMISSIVE_SUFFIX_LIST.add("_e");
        }
        ENTITY_TYPE_VANILLA_BRIGHTNESS_OVERRIDE_VALUE.defaultReturnValue(0);
        ENTITY_TYPE_RENDER_LAYER.defaultReturnValue(0);
    }

    public static ETFManager getInstance() {
        if (manager == null)
            manager = new ETFManager();
        return manager;
    }

    public static void resetInstance() {
        ETFUtils2.KNOWN_NATIVE_IMAGES = new ETFLruCache<>();
        ETFClientCommon.etf$loadConfig();
        ETFDirectory.resetCache();

        //instance based format solves the issue of hashmaps and arrays being clearing while also being accessed
        //as now those rare transitional (reading during clearing) occurrences will simply read from the previous instance of manager
        manager = new ETFManager();
    }



    public static ETFTexture getErrorETFTexture() {
        ETFUtils2.registerNativeImageToIdentifier(ETFUtils2.emptyNativeImage(), new Identifier("etf:error.png"));
        return new ETFTexture(new Identifier("etf:error.png"), false);//, ETFTexture.TextureSource.GENERIC_DEBUG);
    }

    public static EmissiveRenderModes getEmissiveMode() {
        if (ETFConfigData.emissiveRenderMode == EmissiveRenderModes.DULL) {
            return EmissiveRenderModes.DULL;
        } else {
            if (ETFConfigData.emissiveRenderMode == EmissiveRenderModes.COMPATIBLE && ETFVersionDifferenceHandler.areShadersInUse()) {
                return EmissiveRenderModes.DULL;
            } else {
                return EmissiveRenderModes.BRIGHT;
            }
        }
    }

    public void removeThisEntityDataFromAllStorage(ETFCacheKey ETFId) {
        ENTITY_TEXTURE_MAP.removeEntryOnly(ETFId);
        //ENTITY_FEATURE_MAP.clear();


        UUID uuid = ETFId.getMobUUID();
        ENTITY_SPAWN_CONDITIONS_CACHE.removeEntryOnly(uuid);
        ENTITY_IS_UPDATABLE.removeBoolean(uuid);
        ENTITY_UPDATE_QUEUE.remove(uuid);
        ENTITY_DEBUG_QUEUE.remove(uuid);
        ENTITY_BLINK_TIME.removeLong(uuid);
        UUID_TO_MOB_CACHE_KEY_MAP_FOR_FEATURE_USAGE.remove(uuid);
    }

    public void checkIfShouldTriggerUpdate(UUID id) {
        //type safe check as returns false if missing

        if (ENTITY_IS_UPDATABLE.getBoolean(id)
                && ETFConfigData.enableCustomTextures
                && ETFConfigData.textureUpdateFrequency_V2 != ETFConfig.UpdateFrequency.Never) {
            if (ENTITY_UPDATE_QUEUE.size() > 2000)
                ENTITY_UPDATE_QUEUE.clear();
            int delay = ETFConfigData.textureUpdateFrequency_V2.getDelay();
            long randomizer = delay * 20L;
            if (System.currentTimeMillis() % randomizer == Math.abs(id.hashCode()) % randomizer
            ) {
                //marks texture to update next render if a certain delay time is reached
                ENTITY_UPDATE_QUEUE.add(id);
            }
        }
    }

    public void markEntityForDebugPrint(UUID id) {
        if (ETFConfigData.debugLoggingMode != ETFConfig.DebugLogMode.None) {
            ENTITY_DEBUG_QUEUE.add(id);
        }
    }

    @NotNull
    public ETFTexture getETFDefaultTexture(Identifier vanillaIdentifier, boolean canBePatched) {

        return getOrCreateETFTexture(vanillaIdentifier, vanillaIdentifier, canBePatched);
    }

    @NotNull
    public ETFTexture getETFTexture(@NotNull Identifier vanillaIdentifier, @Nullable ETFEntity entity, @NotNull TextureSource source, boolean canBePatched) {
        try {
            if (entity == null) {
                //this should only purposefully call for features like armor or elytra that append to players and have no ETF customizing
                return getETFDefaultTexture(vanillaIdentifier, canBePatched);
            }
            UUID id = entity.getUuid();
            //use custom cache id this differentiates feature renderer calls here and makes the base feature still identifiable by uuid only when features are called
            ETFCacheKey cacheKey = new ETFCacheKey(id, vanillaIdentifier); //source == TextureSource.ENTITY_FEATURE ? vanillaIdentifier : null);
            if (source == TextureSource.ENTITY) {
                //this is so feature renderers can find the 'base texture' of the mob to test it's variant if required
                UUID_TO_MOB_CACHE_KEY_MAP_FOR_FEATURE_USAGE.put(id, cacheKey);
            }

            //fastest in subsequent runs
            if (id == ETF_GENERIC_UUID || entity.getBlockPos().equals(Vec3i.ZERO)) {
                return getETFDefaultTexture(vanillaIdentifier, canBePatched);
            }
            if (ENTITY_TEXTURE_MAP.containsKey(cacheKey)) {
                ETFTexture quickReturn = ENTITY_TEXTURE_MAP.get(cacheKey);
                if (quickReturn == null) {
                    ETFTexture vanillaETF = getETFDefaultTexture(vanillaIdentifier, canBePatched);
                    ENTITY_TEXTURE_MAP.put(cacheKey, vanillaETF);
                    quickReturn = vanillaETF;

                }
                if (source == TextureSource.ENTITY) {
                    if (ENTITY_DEBUG_QUEUE.contains(id)) {
                        boolean inChat = ETFConfigData.debugLoggingMode == ETFConfig.DebugLogMode.Chat;
                        ETFUtils2.logMessage(
                                "\nGeneral ETF:" +
                                        "\n - Texture cache size: " + ETF_TEXTURE_CACHE.size() +
                                        "\nThis " + entity.getType().toString() + ":" +
                                        "\n - Texture: " + quickReturn + "\nEntity cache size: " + ENTITY_TEXTURE_MAP.size() +
                                        "\n - Original spawn state: " + ENTITY_SPAWN_CONDITIONS_CACHE.get(id) +
                                        "\n - OptiFine property count: " + (OPTIFINE_PROPERTY_CACHE.containsKey(vanillaIdentifier) ? Objects.requireNonNullElse(OPTIFINE_PROPERTY_CACHE.get(vanillaIdentifier), new ArrayList<>()).size() : 0) +
                                        "\n - Non property random total: " + TRUE_RANDOM_COUNT_CACHE.getInt(vanillaIdentifier), inChat);

                        ENTITY_DEBUG_QUEUE.remove(id);
                    }
                    if (ENTITY_UPDATE_QUEUE.contains(id)) {//&& source != TextureSource.ENTITY_FEATURE) {
                        Identifier newVariantIdentifier = returnNewAlreadyConfirmedOptifineTexture(entity, vanillaIdentifier, true);
                        ENTITY_TEXTURE_MAP.put(cacheKey, Objects.requireNonNullElse(getOrCreateETFTexture(vanillaIdentifier, Objects.requireNonNullElse(newVariantIdentifier, vanillaIdentifier), canBePatched), getETFDefaultTexture(vanillaIdentifier, canBePatched)));

                        //only if changed
                        if (!quickReturn.thisIdentifier.equals(newVariantIdentifier)) {
                            //iterate over list of all known features and update them
                            ObjectOpenHashSet<ETFCacheKey> featureSet = ENTITY_KNOWN_FEATURES_LIST.getOrDefault(id, new ObjectOpenHashSet<>());
                            //possible concurrent editing of hashmap issues but simplest way to perform this
                            featureSet.forEach((forKey) -> {
                                Identifier forVariantIdentifier = getPossibleVariantIdentifierRedirectForFeatures(entity, forKey.identifier(), TextureSource.ENTITY_FEATURE); //  returnNewAlreadyConfirmedOptifineTexture(entity, forKey.identifier(), true);
                                ENTITY_TEXTURE_MAP.put(forKey, Objects.requireNonNullElse(getOrCreateETFTexture(forKey.identifier(), Objects.requireNonNullElse(forVariantIdentifier, forKey.identifier()), canBePatched), getETFDefaultTexture(forKey.identifier(), canBePatched)));

                            });
                        }

                        ENTITY_UPDATE_QUEUE.remove(id);
                    } else {
                        checkIfShouldTriggerUpdate(id);
                    }
                }
                //this is where 99.99% of calls here will end only the very first call to this method by an entity goes further
                //the first call by any entity of a type will go the furthest and be the slowest as it triggers the initial setup, this makes all future calls by the same entity type faster
                //this is as close as possible to method start I can move this without losing update and debug functionality
                //this is the focal point of the rewrite where all the optimization is expected
                return quickReturn;
            }
            //need to create or find an ETFTexture object for entity and find or add to cache and entity map
            //firstly just going to check if this mob is some sort of gui element or not a real mod


            Identifier possibleIdentifier;
            if (source == TextureSource.ENTITY_FEATURE) {
                possibleIdentifier = getPossibleVariantIdentifierRedirectForFeatures(entity, vanillaIdentifier, source);
            } else {
                possibleIdentifier = getPossibleVariantIdentifier(entity, vanillaIdentifier, source);
            }

            ETFTexture foundTexture;
            foundTexture = Objects.requireNonNullElse(getOrCreateETFTexture(vanillaIdentifier, possibleIdentifier == null ? vanillaIdentifier : possibleIdentifier, canBePatched), getETFDefaultTexture(vanillaIdentifier, canBePatched));
            //if(!(source == TextureSource.ENTITY_FEATURE && possibleIdentifier == null))

            // replace with vanilla non-variant texture if it is a variant and the path is vanilla and this has been disabled in config
            if (ETFConfigData.disableVanillaDirectoryVariantTextures
                    && !foundTexture.thisIdentifier.equals(vanillaIdentifier)
                    && ETFDirectory.getDirectoryOf(foundTexture.thisIdentifier) == ETFDirectory.VANILLA) {
                foundTexture = getETFDefaultTexture(vanillaIdentifier, canBePatched);
            }
            ENTITY_TEXTURE_MAP.put(cacheKey, foundTexture);
            if (source == TextureSource.ENTITY_FEATURE) {
                ObjectOpenHashSet<ETFCacheKey> knownFeatures = ENTITY_KNOWN_FEATURES_LIST.getOrDefault(entity.getUuid(), new ObjectOpenHashSet<>());
                knownFeatures.add(cacheKey);
                ENTITY_KNOWN_FEATURES_LIST.put(entity.getUuid(), knownFeatures);
            }
            return foundTexture;

        } catch (Exception e) {
            ETFUtils2.logWarn("ETF Texture error! if this happens more than a couple times, then something is wrong");
            return getETFDefaultTexture(vanillaIdentifier, canBePatched);
        }
    }

    @Nullable //when vanilla
    private Identifier getPossibleVariantIdentifierRedirectForFeatures(ETFEntity entity, Identifier vanillaIdentifier, TextureSource source) {


        Identifier regularReturnIdentifier = getPossibleVariantIdentifier(entity, vanillaIdentifier, source);
        //if the feature does not have a .properties file and returns the vanilla file or null check if we can copy the base texture's variant
        if (OPTIFINE_PROPERTY_CACHE.get(vanillaIdentifier) == null &&
                (regularReturnIdentifier == null || vanillaIdentifier.equals(regularReturnIdentifier))
        ) {
            //random assignment either failed or returned texture1
            //as this is a feature we will also try one last time to match it to a possible variant of the base texture

            ETFCacheKey baseCacheId = UUID_TO_MOB_CACHE_KEY_MAP_FOR_FEATURE_USAGE.get(entity.getUuid()); //new ETFCacheKey(entity.getUuid(), null);

            if (baseCacheId != null && ENTITY_TEXTURE_MAP.containsKey(baseCacheId)) {
                ETFTexture baseETFTexture = ENTITY_TEXTURE_MAP.get(baseCacheId);
                if (baseETFTexture != null) {
                    return baseETFTexture.getFeatureTexture(vanillaIdentifier);
                }
            }

        } else {
            return regularReturnIdentifier;
        }
        return null;
    }

    @Nullable //when vanilla
    private Identifier getPossibleVariantIdentifier(ETFEntity entity, Identifier vanillaIdentifier, TextureSource source) {

        if (ETFConfigData.enableCustomTextures) {
            //has this been checked before?
            if (TRUE_RANDOM_COUNT_CACHE.containsKey(vanillaIdentifier) || OPTIFINE_PROPERTY_CACHE.containsKey(vanillaIdentifier)) {
                //has optifine checked before?
                if (OPTIFINE_PROPERTY_CACHE.containsKey(vanillaIdentifier)) {
                    List<ETFTexturePropertiesUtils.ETFTexturePropertyCase> optifineProperties = OPTIFINE_PROPERTY_CACHE.get(vanillaIdentifier);
                    if (optifineProperties != null) {
                        return returnNewAlreadyConfirmedOptifineTexture(entity, vanillaIdentifier, false, optifineProperties);
                    }
                }
                //has true random checked before?
                if (TRUE_RANDOM_COUNT_CACHE.containsKey(vanillaIdentifier) && source != TextureSource.ENTITY_FEATURE) {
                    int randomCount = TRUE_RANDOM_COUNT_CACHE.getInt(vanillaIdentifier);
                    if (randomCount != TRUE_RANDOM_COUNT_CACHE.defaultReturnValue()) {
                        return returnNewAlreadyConfirmedTrueRandomTexture(entity, vanillaIdentifier, randomCount);
                    }
                }
                //if we got here the texture is NOT random after having already checked before so return null
                return null;
            }


            //this is a new texture, we need to find what kind of random it is

            //if not null the below two represent the highest version of said files
            Identifier possibleProperty = ETFDirectory.getDirectoryVersionOf(ETFUtils2.replaceIdentifier(vanillaIdentifier, ".png", ".properties"));
            Identifier possible2PNG = ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, 2));
            //try fallback properties
            if(possibleProperty == null && "minecraft".equals(vanillaIdentifier.getNamespace()) && vanillaIdentifier.getPath().contains("_")){
                String vanId =vanillaIdentifier.getPath().replaceAll("(_tame|_angry|_nectar|_shooting|_cold)","");
                possibleProperty = ETFDirectory.getDirectoryVersionOf(new Identifier(vanId.replace( ".png", ".properties")));
            }

            //if both null vanilla fallback as no randoms
            if (possible2PNG == null && possibleProperty == null) {
                //this will tell next call with this texture that these have been checked already
                OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, null);
                return null;
            } else if (/*only*/possibleProperty == null) {
                if (source != TextureSource.ENTITY_FEATURE) {
                    newTrueRandomTextureFound(vanillaIdentifier, possible2PNG);
                    return returnNewAlreadyConfirmedTrueRandomTexture(entity, vanillaIdentifier);
                }
            } else if (/*only*/possible2PNG == null) {
                //optifine random confirmed
                ETFTexturePropertiesUtils.processNewOptifinePropertiesFile(entity, vanillaIdentifier, possibleProperty);
                return returnNewAlreadyConfirmedOptifineTexture(entity, vanillaIdentifier, false);
            } else //noinspection CommentedOutCode
            {//neither null this will be annoying
                //if 2.png is higher it MUST be treated as true random confirmed
                ResourceManager resources = MinecraftClient.getInstance().getResourceManager();
                String p2pngPackName = resources.getResource(possible2PNG).isPresent() ? resources.getResource(possible2PNG).get().getResourcePackName() : null;
                String propertiesPackName = resources.getResource(possibleProperty).isPresent() ? resources.getResource(possibleProperty).get().getResourcePackName() : null;
                //ObjectOpenHashSet<String> packs = new ObjectOpenHashSet<>();
                //if (p2pngPackName != null)
                //packs.add(p2pngPackName);
                //if (propertiesPackName != null)
                //packs.add(propertiesPackName);

                // System.out.println("debug6534="+p2pngPackName+","+propertiesPackName+","+ETFUtils2.returnNameOfHighestPackFrom(packs));
                if (propertiesPackName != null && propertiesPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{p2pngPackName, propertiesPackName}))) {
                    ETFTexturePropertiesUtils.processNewOptifinePropertiesFile(entity, vanillaIdentifier, possibleProperty);
                    return returnNewAlreadyConfirmedOptifineTexture(entity, vanillaIdentifier, false);
                } else {
                    if (source != TextureSource.ENTITY_FEATURE) {
                        newTrueRandomTextureFound(vanillaIdentifier, possible2PNG);
                        return returnNewAlreadyConfirmedTrueRandomTexture(entity, vanillaIdentifier);
                    }
                }
            }
        }
        //marker to signify code has run before and is not random or true random
        OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, null);
        //use vanilla as fallback
        return null;
    }

    private void newTrueRandomTextureFound(Identifier vanillaIdentifier, Identifier variant2PNG) {
        //here 2.png is confirmed to exist and has its directory already applied
        //I'm going to ignore 1.png that will be hardcoded as vanilla or optifine replaced
        ResourceManager resources = MinecraftClient.getInstance().getResourceManager();
        int totalTextureCount = 2;
        while (resources.getResource(ETFUtils2.replaceIdentifier(variant2PNG, "[0-9]+(?=\\.png)", String.valueOf((totalTextureCount + 1)))).isPresent()) {
            totalTextureCount++;
        }
        //here totalTextureCount == the confirmed last value of the random order
        //System.out.println("total true random was="+totalTextureCount);
        TRUE_RANDOM_COUNT_CACHE.put(vanillaIdentifier, totalTextureCount);

        //make sure to return first check
        //return returnAlreadyConfirmedTrueRandomTexture(entity,vanillaIdentifier,totalTextureCount);
        //can't return null as 2.png confirmed exists
    }

    @Nullable
    private Identifier returnNewAlreadyConfirmedOptifineTexture(ETFEntity entity, Identifier vanillaIdentifier, boolean isThisAnUpdate) {
        return returnNewAlreadyConfirmedOptifineTexture(entity, vanillaIdentifier, isThisAnUpdate, OPTIFINE_PROPERTY_CACHE.get(vanillaIdentifier));
    }

    @Nullable
    private Identifier returnNewAlreadyConfirmedOptifineTexture(ETFEntity entity, Identifier vanillaIdentifier, boolean isThisAnUpdate, List<ETFTexturePropertiesUtils.ETFTexturePropertyCase> optifineProperties) {

        int variantNumber = testAndGetVariantNumberFromOptiFineCases(entity, isThisAnUpdate, optifineProperties);

        Identifier variantIdentifier = returnNewAlreadyNumberedRandomTexture(vanillaIdentifier, variantNumber);
        if (variantIdentifier == null) {
            return null;
        }
        //must test these exist
        if (ETF_TEXTURE_CACHE.containsKey(variantIdentifier)) {
            if (ETF_TEXTURE_CACHE.get(variantIdentifier) == null) {
                return null;
            }
            //then we know it exists
            return variantIdentifier;
        }
        Optional<Resource> variantResource = MinecraftClient.getInstance().getResourceManager().getResource(variantIdentifier);
        if (variantResource.isPresent()) {
            return variantIdentifier;
            //it will be added to cache for future checks later
        } else {
            ETF_TEXTURE_CACHE.put(variantIdentifier, null);
        }
        //ETFUtils.logError("texture assign has failed, vanilla texture has been used as fallback");

        return null;
    }

    private int testAndGetVariantNumberFromOptiFineCases(ETFEntity entity, boolean isThisAnUpdate, List<ETFTexturePropertiesUtils.ETFTexturePropertyCase> optifineProperties) {
        try {
            for (ETFTexturePropertiesUtils.ETFTexturePropertyCase property :
                    optifineProperties) {
                if (property.doesEntityMeetConditionsOfThisCase(entity, isThisAnUpdate, ENTITY_IS_UPDATABLE)) {
                    return property.getAnEntityVariantSuffixFromThisCase(entity.getUuid());
                }
            }
        } catch (Exception e) {
            return 1;
        }

        //ETFUtils.logError("optifine property checks found no match using vanilla");
        return 1;
    }

    @NotNull
    private Identifier returnNewAlreadyConfirmedTrueRandomTexture(ETFEntity entity, Identifier vanillaIdentifier) {
        return returnNewAlreadyConfirmedTrueRandomTexture(entity, vanillaIdentifier, TRUE_RANDOM_COUNT_CACHE.getInt(vanillaIdentifier));
    }

    @NotNull
    private Identifier returnNewAlreadyConfirmedTrueRandomTexture(ETFEntity entity, Identifier vanillaIdentifier, int totalCount) {
        int randomReliable = Math.abs(entity.getUuid().hashCode());
        randomReliable %= totalCount;
        randomReliable++;
        //no need to test as they have already all been confirmed existing by code
        Identifier toReturn = returnNewAlreadyNumberedRandomTexture(vanillaIdentifier, randomReliable);
        return toReturn == null ? vanillaIdentifier : toReturn;
    }

    @Nullable
    private Identifier returnNewAlreadyNumberedRandomTexture(Identifier vanillaIdentifier, int variantNumber) {
        //1.png logic not required as expected optifine behaviour is already present

        return ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, variantNumber));
        //return ETFDirectory.getDirectoryVersionOf(ETFUtils2.replaceIdentifier(vanillaIdentifier, ".png", variantNumber + ".png"));
    }

    @NotNull
    private ETFTexture getOrCreateETFTexture(Identifier vanillaIdentifier, Identifier variantIdentifier, boolean canBePatched) {
        if (ETF_TEXTURE_CACHE.containsKey(variantIdentifier)) {
            //use cached ETFTexture
            ETFTexture cached = ETF_TEXTURE_CACHE.get(variantIdentifier);
            if (cached != null) {
                return cached;
            } else {
                ETFUtils2.logWarn("getOrCreateETFTexture found a null, this probably should not be happening");
                //texture doesn't exist
                cached = ETF_TEXTURE_CACHE.get(vanillaIdentifier);
                if (cached != null) {
                    return cached;
                }
            }
        } else {
            if(vanillaIdentifier == null){
                ETFUtils2.logError("getOrCreateETFTexture identifier was null and should not have been");
                return ETF_ERROR_TEXTURE;
            }
            //create new ETFTexture and cache it
            ETFTexture foundTexture = new ETFTexture(variantIdentifier, canBePatched);
            ETF_TEXTURE_CACHE.put(variantIdentifier, foundTexture);
            return foundTexture;
        }
        ETFUtils2.logError("getOrCreateETFTexture reached the end and should not have");
        return ETF_ERROR_TEXTURE;
    }
//    @Nullable
//    public ETFPlayerTexture getPlayerHeadTexture(ETFEntity playerHead) {
//        if (PLAYER_TEXTURE_MAP.containsKey(playerHead.getUuid())) {
//            return PLAYER_TEXTURE_MAP.get(playerHead.getUuid());
//        }
//        return null;
//    }

    @Nullable
    public ETFPlayerTexture getPlayerTexture(PlayerEntity player, Identifier rendererGivenSkin) {
        return getPlayerTexture(new ETFPlayerEntityWrapper(player),rendererGivenSkin);
    }
    @Nullable
    public ETFPlayerTexture getPlayerTexture(ETFPlayerEntity player, Identifier rendererGivenSkin) {
        try {
            UUID id = player.getUuid();
            if (PLAYER_TEXTURE_MAP.containsKey(id)) {
                ETFPlayerTexture possibleSkin = PLAYER_TEXTURE_MAP.get(id);
                if (possibleSkin == null ||
                        (possibleSkin.player == null && possibleSkin.isCorrectObjectForThisSkin(rendererGivenSkin))) {
                    return null;
                } else if (possibleSkin.isCorrectObjectForThisSkin(rendererGivenSkin)
                        || MinecraftClient.getInstance().currentScreen instanceof ETFConfigScreenSkinTool) {
                    return possibleSkin;
                }

            }
            PLAYER_TEXTURE_MAP.put(id, null);
            ETFPlayerTexture etfPlayerTexture = new ETFPlayerTexture(player, rendererGivenSkin);
            PLAYER_TEXTURE_MAP.put(id, etfPlayerTexture);
            return etfPlayerTexture;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public enum TextureSource {
        ENTITY,
        BLOCK_ENTITY,
        ENTITY_FEATURE
    }

    public enum EmissiveRenderModes {
        DULL,
        BRIGHT,
        COMPATIBLE;

        public static EmissiveRenderModes blockEntityMode() {
            //iris has fixes for bright mode which is otherwise broken on block entities, does not require enabled shaders
            if (ETFConfigData.emissiveRenderMode == DULL) {
                return DULL;
            } else {
                if (ETFVersionDifferenceHandler.isThisModLoaded("iris")) {
                    if (ETFConfigData.emissiveRenderMode == COMPATIBLE && ETFVersionDifferenceHandler.areShadersInUse()) {
                        return DULL;
                    } else {
                        return BRIGHT;
                    }
                } else {
                    return DULL;
                }
            }
        }

        @Override
        public String toString() {
            //noinspection EnhancedSwitchMigration
            switch (this) {
                case DULL:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.emissive_mode.dull").getString();
                case BRIGHT:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.emissive_mode.bright").getString();
                default:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.emissive_mode.compatible").getString();
            }
        }

        public EmissiveRenderModes next() {
            //noinspection EnhancedSwitchMigration
            switch (this) {
                case DULL:
                    return BRIGHT;
                case BRIGHT:
                    return COMPATIBLE;
                default:
                    return DULL;
            }
        }
    }

}
