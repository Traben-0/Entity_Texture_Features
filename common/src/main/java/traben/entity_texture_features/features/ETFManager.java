package traben.entity_texture_features.features;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
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
import traben.entity_texture_features.utils.EntityIntLRU;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.ETFClientCommon.MOD_ID;
import static traben.entity_texture_features.features.player.ETFPlayerTexture.SKIN_NAMESPACE;


public class ETFManager {

    private static ETFManager instance;
    private static final ETFTexture ETF_ERROR_TEXTURE = getErrorETFTexture();
    public final EntityIntLRU LAST_MET_RULE_INDEX = new EntityIntLRU();
    public final ObjectOpenHashSet<String> EMISSIVE_SUFFIX_LIST = new ObjectOpenHashSet<>();
    public final ETFLruCache<UUID, ETFPlayerTexture> PLAYER_TEXTURE_MAP = new ETFLruCache<>();
    public final ArrayList<String> KNOWN_RESOURCEPACK_ORDER = new ArrayList<>();
    public final Object2IntOpenHashMap<EntityType<?>> ENTITY_TYPE_VANILLA_BRIGHTNESS_OVERRIDE_VALUE = new Object2IntOpenHashMap<>();
    public final ObjectOpenHashSet<EntityType<?>> ENTITY_TYPE_IGNORE_PARTICLES = new ObjectOpenHashSet<>();
    public final Object2IntOpenHashMap<EntityType<?>> ENTITY_TYPE_RENDER_LAYER = new Object2IntOpenHashMap<>();
    //this is a cache of all known ETFTexture versions of any existing resource-pack texture, used to prevent remaking objects
    public final Object2ReferenceOpenHashMap<@NotNull Identifier, @Nullable ETFTexture> ETF_TEXTURE_CACHE = new Object2ReferenceOpenHashMap<>();
    public UUID ENTITY_DEBUG = null;
    public final EntityIntLRU LAST_SUFFIX_OF_ENTITY = new EntityIntLRU();
    public final ETFLruCache<Identifier, NativeImage> KNOWN_NATIVE_IMAGES = new ETFLruCache<>();
    private final Object2ObjectOpenHashMap<Identifier, ETFTextureVariator> VARIATOR_MAP = new Object2ObjectOpenHashMap<>();
    public Object2ReferenceOpenHashMap<@NotNull Identifier, @NotNull ETFDirectory> ETF_DIRECTORY_CACHE = new Object2ReferenceOpenHashMap<>();// = new Object2ReferenceOpenHashMap<>();
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
        if (instance == null)
            instance = new ETFManager();
        return instance;
    }

    public static void resetInstance() {
        ETFClientCommon.etf$loadConfig();

        //instance based format solves the issue of hashmaps and arrays being clearing while also being accessed
        //as now those rare transitional (reading during clearing) occurrences will simply read from the previous instance of manager
        instance = new ETFManager();
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

    public String getGeneralPrintout() {
        return "§aGeneral: §r" +
                "\n§2 - amount of 'base' textures: §r" + VARIATOR_MAP.size() +
                "\n§2 - total textures including variants: §r" + ETF_TEXTURE_CACHE.size()
                ;
    }

    public void doTheBigBoyPrintoutKronk(){
        StringBuilder out = new StringBuilder();

        out.append("\n||||||||||||||-ETF EVERYTHING LOG START-|||||||||||||||")
                .append("\n----------------------------------------")
                .append("\n-----------General stats-------------")
                .append("\n----------------------------------------")
                .append("\n known emissive suffixes: \n - ").append(EMISSIVE_SUFFIX_LIST)
                .append("\n player textures: \n - ").append(PLAYER_TEXTURE_MAP.size())
                .append("\n image files read: \n - ").append(KNOWN_NATIVE_IMAGES.size())
//                .append("\n - known resource-pack order: §r\n   ").append(KNOWN_RESOURCEPACK_ORDER.size())
                .append("\n----------------------------------------")
                .append("\n----------Texture totals----------------")
                .append("\n----------------------------------------")
                .append("\n amount of textures that have or can variate: \n - ").append(VARIATOR_MAP.size())
                .append("\n amount of textures seen by ETF total (not including emissives): \n - ").append(ETF_TEXTURE_CACHE.size());

        StringBuilder textureLoopVariates = new StringBuilder();
        StringBuilder textureLoopNormal = new StringBuilder();
        int totalEmissive = 0;
        int totalEnchant = 0;
        for (ETFTexture texture:
                ETF_TEXTURE_CACHE.values()) {
            if(texture != null) {
                if (texture.isEmissive()) totalEmissive++;
                if (texture.isEnchanted()) totalEnchant++;
                if(VARIATOR_MAP.containsKey(texture.thisIdentifier)){
                    textureLoopVariates.append("\n - ").append(
                            VARIATOR_MAP.get(texture.thisIdentifier).getPrintout().replaceAll("\n","\n      "));
                }
                textureLoopNormal.append("\n - ").append(texture);

            }
        }
        out.append("\n total emissives: \n - ").append(totalEmissive)
                .append("\n total enchanted: \n - ").append(totalEnchant)
                .append("\n----------------------------------------")
                .append("\n----------ALL texture groups-------------")
                .append("\n----------------------------------------")
                .append("\n (Note: all of these can be varied via random entity rules)")
                .append(textureLoopVariates.toString().replaceAll("§.",""))
                .append("\n----------------------------------------")
                .append("\n----------ALL Textures Seen-------------")
                .append("\n----------------------------------------")
                .append("\n (Note: these are not all variable by random entity rules, but can usually be emissive)")
                .append(textureLoopNormal)
                .append("\n----------------------------------------");


        out.append("\n----------------------------------------")
        .append("\n||||||||||||||-ETF EVERYTHING LOG END-|||||||||||||||");

        ETFUtils2.logMessage(out.toString());
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


    public void markEntityForDebugPrint(UUID uuid) {
        if (ETFConfigData.debugLoggingMode != ETFConfig.DebugLogMode.None) {
            ENTITY_DEBUG = uuid;
        }
    }

    @NotNull
    public ETFTexture getETFTextureNoVariation(Identifier vanillaIdentifier) {
        return getOrCreateETFTexture(vanillaIdentifier);
    }

    @NotNull
    public ETFTexture getETFTextureVariant(@NotNull Identifier vanillaIdentifier, @Nullable ETFEntity entity) {
        if (entity == null
                || entity.etf$getUuid() == ETFApi.ETF_GENERIC_UUID
                || entity.etf$getBlockPos().equals(Vec3i.ZERO)) {
            return getETFTextureNoVariation(vanillaIdentifier);
        }
        if (!VARIATOR_MAP.containsKey(vanillaIdentifier)) {
                if(SKIN_NAMESPACE.equals(vanillaIdentifier.getNamespace())){
                    return getETFTextureNoVariation(vanillaIdentifier);
                }else {
                    VARIATOR_MAP.put(vanillaIdentifier, ETFTextureVariator.of(vanillaIdentifier));
                    if (ETFConfigData.logTextureDataInitialization) {
                        ETFUtils2.logMessage("Amount of 'base' textures: " + VARIATOR_MAP.size());
                        ETFUtils2.logMessage("Total textures including variants: " + ETF_TEXTURE_CACHE.size());
                    }
                }
        }
        return VARIATOR_MAP.get(vanillaIdentifier).getVariantOf(entity);
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
                    //ETFRenderContext.preventRenderLayerTextureModify();
                    return possibleSkin;
                }

            }
            PLAYER_TEXTURE_MAP.put(id, null);
            ETFPlayerTexture etfPlayerTexture = new ETFPlayerTexture(player, rendererGivenSkin);
            PLAYER_TEXTURE_MAP.put(id, etfPlayerTexture);
            //ETFRenderContext.preventRenderLayerTextureModify();
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


}
