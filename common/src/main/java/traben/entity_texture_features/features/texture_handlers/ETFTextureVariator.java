package traben.entity_texture_features.features.texture_handlers;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.property_reading.RandomPropertiesProvider;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public abstract class ETFTextureVariator {

    public static @NotNull ETFTextureVariator of(@NotNull Identifier vanillaIdentifier) {
        ETFApi.ETFVariantSuffixProvider variantProvider = ETFApi.ETFVariantSuffixProvider.getInstance(
                ETFUtils2.replaceIdentifier(vanillaIdentifier, ".png", ".properties"),
                vanillaIdentifier,
                "skins", "textures"
        );
        System.out.println("variates=" + (variantProvider != null));
        if (variantProvider != null) {
            return new ETFTextureMultiple(vanillaIdentifier, variantProvider);
        }
        return new ETFTextureSingleton(vanillaIdentifier);
    }

    public ETFTexture getVariantOf(@NotNull ETFEntity entity, @NotNull ETFManager.TextureSource source) {
        if (source == ETFManager.TextureSource.ENTITY) {
            if (ETFManager.getInstance().ENTITY_DEBUG_QUEUE.contains(entity.etf$getUuid())) {
                boolean inChat = ETFConfigData.debugLoggingMode == ETFConfig.DebugLogMode.Chat;
                ETFUtils2.logMessage(
                        "\nPrintout:", inChat);//todo
                ETFManager.getInstance().ENTITY_DEBUG_QUEUE.remove(entity.etf$getUuid());
            }
        }
        return getVariantOfInternal(entity, source);
    }

    protected abstract @NotNull ETFTexture getVariantOfInternal(@NotNull ETFEntity entity, @NotNull ETFManager.TextureSource source);

    private static class ETFTextureSingleton extends ETFTextureVariator {
        private final ETFTexture self;

        ETFTextureSingleton(Identifier singletonId) {
            self = ETFManager.getInstance().getETFTextureNoVariation(singletonId);
        }

        @Override
        protected @NotNull ETFTexture getVariantOfInternal(@NotNull ETFEntity entity, ETFManager.@NotNull TextureSource source) {
            return self;
        }
    }

    private static class ETFTextureMultiple extends ETFTextureVariator {

        public final @NotNull Object2IntLinkedOpenHashMap<UUID> ENTITY_SUFFIX_MAP = new Object2IntLinkedOpenHashMap<>();
        private final @NotNull Int2ObjectArrayMap<ETFTexture> variantMap = new Int2ObjectArrayMap<>();
        private final @NotNull ETFApi.ETFVariantSuffixProvider suffixProvider;


        ETFTextureMultiple(@NotNull Identifier vanillaId, @NotNull ETFApi.ETFVariantSuffixProvider suffixProvider) {
            ENTITY_SUFFIX_MAP.defaultReturnValue(-1);
            this.suffixProvider = suffixProvider;
            Identifier directorized = ETFDirectory.getDirectoryVersionOf(vanillaId);

            ETFTexture vanilla = ETFManager.getInstance().getETFTextureNoVariation(directorized == null ? vanillaId : directorized);

            variantMap.put(1, vanilla);
            variantMap.put(0, vanilla);
            variantMap.defaultReturnValue(vanilla);


            IntOpenHashSet suffixes = suffixProvider.getAllSuffixes();
            suffixes.remove(0);
            suffixes.remove(1);
            for (int suffix :
                    suffixes) {
                Identifier variant = ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaId, suffix));
                System.out.println("tried=" + variant);
                if (variant != null) {
                    variantMap.put(suffix, ETFManager.getInstance().getETFTextureNoVariation(variant));
                } else {
                    variantMap.put(suffix, vanilla);
                }
            }
            System.out.println("keys=");
            variantMap.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        public void checkIfShouldExpireEntity(UUID id) {
            //type safe check as returns false if missing

            if (ETFManager.getInstance().ENTITY_IS_UPDATABLE.getBoolean(id)
                    && ETFConfigData.enableCustomTextures
                    && ETFConfigData.textureUpdateFrequency_V2 != ETFConfig.UpdateFrequency.Never) {

                int delay = ETFConfigData.textureUpdateFrequency_V2.getDelay();
                long randomizer = delay * 20L;
                if (System.currentTimeMillis() % randomizer == Math.abs(id.hashCode()) % randomizer
                ) {
                    //marks texture to update next render if a certain delay time is reached
                    ENTITY_SUFFIX_MAP.removeInt(id);
                }
            }
            if (ENTITY_SUFFIX_MAP.size() > 500) {
                UUID lastId = ENTITY_SUFFIX_MAP.lastKey();
                if (!lastId.equals(id)) {
                    ETFManager.getInstance().removeThisEntityDataFromAllStorage(lastId);
                    ENTITY_SUFFIX_MAP.removeInt(lastId);
                }
            }
        }

        @Override
        protected @NotNull ETFTexture getVariantOfInternal(@NotNull ETFEntity entity, ETFManager.@NotNull TextureSource source) {
            UUID id = entity.etf$getUuid();
            int knownSuffix = ENTITY_SUFFIX_MAP.getInt(id);
            if (knownSuffix != -1) {
                if (source != ETFManager.TextureSource.BLOCK_ENTITY) {
                    checkIfShouldExpireEntity(id);
                }
                //System.out.println("known = "+knownSuffix);
                return variantMap.get(knownSuffix);
            }
            //else needs new suffix
            int newSuffix;
            if (source == ETFManager.TextureSource.ENTITY_FEATURE) {
                if (suffixProvider instanceof RandomPropertiesProvider) {
                    newSuffix = suffixProvider.getSuffixForETFEntity(entity,
                            !ETFManager.getInstance().ENTITY_IS_UPDATABLE.containsKey(entity.etf$getUuid()),
                            ETFManager.getInstance().ENTITY_IS_UPDATABLE);
                } else {
                    //try to use the base entities suffix first
                    int baseEntitySuffix = ETFManager.getInstance().LAST_SUFFIX_OF_ENTITY.getInt(ETFRenderContext.getCurrentEntity().etf$getUuid());
                    if (baseEntitySuffix != -1 && variantMap.containsKey(baseEntitySuffix)) {
                        newSuffix = baseEntitySuffix;
                    } else {
                        newSuffix = suffixProvider.getSuffixForETFEntity(entity,
                                !ETFManager.getInstance().ENTITY_IS_UPDATABLE.containsKey(entity.etf$getUuid()),
                                ETFManager.getInstance().ENTITY_IS_UPDATABLE);
                    }
                }
            } else {
                newSuffix = suffixProvider.getSuffixForETFEntity(entity,
                        !ETFManager.getInstance().ENTITY_IS_UPDATABLE.containsKey(entity.etf$getUuid()),
                        ETFManager.getInstance().ENTITY_IS_UPDATABLE);
                ETFManager.getInstance().LAST_SUFFIX_OF_ENTITY.put(id, newSuffix);
            }

            //System.out.println("new = "+newSuffix);
            ENTITY_SUFFIX_MAP.put(id, newSuffix);
            return variantMap.get(newSuffix);
        }
    }
}
