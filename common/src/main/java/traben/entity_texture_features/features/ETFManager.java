package traben.entity_texture_features.features;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.player.ETFPlayerEntity;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.entity_texture_features.features.texture_handlers.ETFDirectory;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.features.texture_handlers.ETFTextureVariator;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFLruCache;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.ETFClientCommon.MOD_ID;


public class ETFManager {

    public static final UUID ETF_GENERIC_UUID = UUID.nameUUIDFromBytes(("GENERIC").getBytes());
    private static final ETFTexture ETF_ERROR_TEXTURE = getErrorETFTexture();
    private static ETFManager manager;
    public final ETFLruCache<UUID, Integer> LAST_MET_RULE_INDEX = new ETFLruCache<>();
    public final ObjectOpenHashSet<String> EMISSIVE_SUFFIX_LIST = new ObjectOpenHashSet<>();
    public final ETFLruCache<UUID, ETFPlayerTexture> PLAYER_TEXTURE_MAP = new ETFLruCache<>();
    public final Object2LongOpenHashMap<UUID> ENTITY_BLINK_TIME = new Object2LongOpenHashMap<>();
    public final ArrayList<String> KNOWN_RESOURCEPACK_ORDER = new ArrayList<>();
    public final Object2IntOpenHashMap<EntityType<?>> ENTITY_TYPE_VANILLA_BRIGHTNESS_OVERRIDE_VALUE = new Object2IntOpenHashMap<>();
    public final ObjectOpenHashSet<EntityType<?>> ENTITY_TYPE_IGNORE_PARTICLES = new ObjectOpenHashSet<>();
    public final Object2IntOpenHashMap<EntityType<?>> ENTITY_TYPE_RENDER_LAYER = new Object2IntOpenHashMap<>();
//    public final ETFLruCache<UUID, Object2BooleanOpenHashMap<RandomProperty>> ENTITY_SPAWN_CONDITIONS_CACHE = new ETFLruCache<>();
    //this is a cache of all known ETFTexture versions of any existing resource-pack texture, used to prevent remaking objects
    public final Object2ReferenceOpenHashMap<@NotNull Identifier, @Nullable ETFTexture> ETF_TEXTURE_CACHE = new Object2ReferenceOpenHashMap<>();
//    public final Object2BooleanOpenHashMap<UUID> ENTITY_IS_UPDATABLE = new Object2BooleanOpenHashMap<>();
    public final ObjectOpenHashSet<UUID> ENTITY_DEBUG_QUEUE = new ObjectOpenHashSet<>();
    public final Object2IntLinkedOpenHashMap<UUID> LAST_SUFFIX_OF_ENTITY = new EntitySuffixLRU();
    private final Object2ObjectOpenHashMap<Identifier, ETFTextureVariator> VARIATOR_MAP = new Object2ObjectOpenHashMap<>();
    public Boolean mooshroomBrownCustomShroomExists = null;
    //marks whether mooshroom mushroom overrides exist
    public Boolean mooshroomRedCustomShroomExists = null;
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
        ETFUtils2.registerNativeImageToIdentifier(ETFUtils2.emptyNativeImage(), new Identifier(MOD_ID, "error.png"));
        return new ETFTexture(new Identifier(MOD_ID, "error.png")/*, false*/);//, ETFTexture.TextureSource.GENERIC_DEBUG);
    }

    public static EmissiveRenderModes getEmissiveMode() {
        if (ETFConfigData.emissiveRenderMode == EmissiveRenderModes.BRIGHT
                && ETFRenderContext.getCurrentEntity() != null
                && !ETFRenderContext.getCurrentEntity().etf$canBeBright()) {
            return EmissiveRenderModes.DULL;
        }
        return ETFConfigData.emissiveRenderMode;
    }

    public void grabSpecialProperties(Properties props, ETFEntity entity) {
        if (entity == null) return;

        if (props.containsKey("vanillaBrightnessOverride")) {
            String value = props.getProperty("vanillaBrightnessOverride").trim();
            int tryNumber;
            try {
                tryNumber = Integer.parseInt(value.replaceAll("\\D", ""));
            } catch (NumberFormatException e) {
                tryNumber = 0;
            }
            if (tryNumber >= 16) tryNumber = 15;
            if (tryNumber < 0) tryNumber = 0;
            ENTITY_TYPE_VANILLA_BRIGHTNESS_OVERRIDE_VALUE.put(entity.etf$getType(), tryNumber);
        }

        if (props.containsKey("suppressParticles")
                && "true".equals(props.getProperty("suppressParticles"))) {
            ENTITY_TYPE_IGNORE_PARTICLES.add(entity.etf$getType());
        }

        if (props.containsKey("entityRenderLayerOverride")) {
            String layer = props.getProperty("entityRenderLayerOverride");
            //noinspection EnhancedSwitchMigration
            switch (layer) {
                case "translucent":
                    ENTITY_TYPE_RENDER_LAYER.put(entity.etf$getType(), 1);
                    break;
                case "translucent_cull":
                    ENTITY_TYPE_RENDER_LAYER.put(entity.etf$getType(), 2);
                    break;
                case "end_portal":
                    ENTITY_TYPE_RENDER_LAYER.put(entity.etf$getType(), 3);
                    break;
                case "outline":
                    ENTITY_TYPE_RENDER_LAYER.put(entity.etf$getType(), 4);
                    break;
            }
        }
    }

    public void removeThisEntityDataFromAllStorage(UUID uuid) {
        //todo still needed? expand?
//        ENTITY_SPAWN_CONDITIONS_CACHE.removeEntryOnly(uuid);
//        ENTITY_IS_UPDATABLE.removeBoolean(uuid);
        ENTITY_DEBUG_QUEUE.remove(uuid);
        ENTITY_BLINK_TIME.removeLong(uuid);
    }

    public void markEntityForDebugPrint(UUID id) {
        if (ETFConfigData.debugLoggingMode != ETFConfig.DebugLogMode.None) {
            ENTITY_DEBUG_QUEUE.add(id);
        }
    }

    @NotNull
    public ETFTexture getETFTextureNoVariation(Identifier vanillaIdentifier) {
        return getOrCreateETFTexture(vanillaIdentifier);
    }

    @NotNull
    public ETFTexture getETFTextureVariant(@NotNull Identifier vanillaIdentifier, @Nullable ETFEntity entity, @NotNull TextureSource source) {
        if (entity == null
                || entity.etf$getUuid() == ETF_GENERIC_UUID
                || entity.etf$getBlockPos().equals(Vec3i.ZERO)) {
            return getETFTextureNoVariation(vanillaIdentifier);
        }
        if (!VARIATOR_MAP.containsKey(vanillaIdentifier)) {
            VARIATOR_MAP.put(vanillaIdentifier, ETFTextureVariator.of(vanillaIdentifier));
        }
        return VARIATOR_MAP.get(vanillaIdentifier).getVariantOf(entity, source);
    }


    @NotNull
    private ETFTexture getOrCreateETFTexture(Identifier ofIdentifier) {
        if (ETF_TEXTURE_CACHE.containsKey(ofIdentifier)) {
            //use cached ETFTexture
            ETFTexture cached = ETF_TEXTURE_CACHE.get(ofIdentifier);
            if (cached != null) {
                return cached;
            }
        } else {
            //create new ETFTexture and cache it
            ETFTexture newTexture = new ETFTexture(ofIdentifier);
            ETF_TEXTURE_CACHE.put(ofIdentifier, newTexture);
            return newTexture;
        }
        ETFUtils2.logError("getOrCreateETFTexture reached the end and should not have");
        return ETF_ERROR_TEXTURE;
    }

    @Nullable
    public ETFPlayerTexture getPlayerTexture(PlayerEntity player, Identifier rendererGivenSkin) {
        return getPlayerTexture((ETFPlayerEntity) player, rendererGivenSkin);
    }

    @Nullable
    public ETFPlayerTexture getPlayerTexture(ETFPlayerEntity player, Identifier rendererGivenSkin) {
        try {
            UUID id = player.etf$getUuid();
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
        BRIGHT//,
        //COMPATIBLE
        ;


        @Override
        public String toString() {
            return switch (this) {
                case DULL -> ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config.entity_texture_features.emissive_mode.dull").getString();
                case BRIGHT -> ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config.entity_texture_features.emissive_mode.bright").getString();
//                default -> ETFVersionDifferenceHandler.getTextFromTranslation(
//                        "config.entity_texture_features.emissive_mode.compatible").getString();
            };
        }

        public EmissiveRenderModes next() {
            if (this == DULL)
                return BRIGHT;
            return DULL;
        }
    }

    private static class EntitySuffixLRU extends Object2IntLinkedOpenHashMap<UUID> {
        {
            defaultReturnValue(-1);
        }

        @Override
        public int put(UUID uuid, int v) {
            if (size() >= 3000) {
                UUID lastKey = lastKey();
                if (!lastKey.equals(uuid)) {
                    removeInt(lastKey);
                }
            }
            return this.putAndMoveToFirst(uuid, v);
        }
    }

}
