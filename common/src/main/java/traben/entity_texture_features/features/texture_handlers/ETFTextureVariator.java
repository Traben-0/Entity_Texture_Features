package traben.entity_texture_features.features.texture_handlers;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.property_reading.PropertiesRandomProvider;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.EntityIntLRU;

import java.util.Objects;
import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public abstract class ETFTextureVariator{


    public static @NotNull ETFTextureVariator of(@NotNull Identifier vanillaIdentifier) {
        if (ETFConfigData.enableCustomTextures) {
            ETFApi.ETFVariantSuffixProvider variantProvider = ETFApi.ETFVariantSuffixProvider.getVariantProviderOrNull(
                    ETFUtils2.replaceIdentifier(vanillaIdentifier, ".png", ".properties"),
                    vanillaIdentifier,
                    "skins", "textures"
            );
            if (variantProvider != null) {
                return new ETFTextureMultiple(vanillaIdentifier, variantProvider);
            }
        }
        return new ETFTextureSingleton(vanillaIdentifier);
    }

    public ETFTexture getVariantOf(@NotNull ETFEntity entity) {


        if (ETFManager.getInstance().ENTITY_DEBUG != null
                && ETFManager.getInstance().ENTITY_DEBUG.equals(entity.etf$getUuid())) {
                boolean inChat = ETFConfigData.debugLoggingMode == ETFConfig.DebugLogMode.Chat;

                ETFTexture output = getVariantOfInternal(entity);

                ETFUtils2.logMessage(
                            "\n§e-----------ETF Debug Printout-------------§r" +
                                "\n" + ETFManager.getInstance().getGeneralPrintout() +
                                "\n§eEntity:§r" +
                                "\n§6 - type:§r " + entity.etf$getType().getTranslationKey() +
                                "\n§6 - texture:§r " + output +
                                "\n§6 - can_update_variant:§r "+ (this instanceof ETFTextureMultiple multi && multi.suffixProvider.entityCanUpdate(entity.etf$getUuid()))+
                                "\n§6 - last matching rule:§r " + ETFManager.getInstance().LAST_MET_RULE_INDEX.getInt(entity.etf$getUuid()) +
                                "\n" + getPrintout() +
                                "\n§e----------------------------------------§r"
                        , inChat);
                ETFManager.getInstance().ENTITY_DEBUG = null;

                return output;
        }
        return getVariantOfInternal(entity);
    }



    public abstract String getPrintout();

    protected abstract @NotNull ETFTexture getVariantOfInternal(@NotNull ETFEntity entity);

    public static class ETFTextureSingleton extends ETFTextureVariator {
        private final ETFTexture self;

        public ETFTextureSingleton(Identifier singletonId) {
            self = ETFManager.getInstance().getETFTextureNoVariation(singletonId);

            if(ETFConfigData.logTextureDataInitialization) {
                ETFUtils2.logMessage("Initializing texture for the first time: " + singletonId);
                ETFUtils2.logMessage(" - no variants for: " + self);

            }
        }


        @Override
        protected @NotNull ETFTexture getVariantOfInternal(@NotNull ETFEntity entity) {
            return self;
        }

        public String getPrintout() {
            return "§bTexture: §r"+
                    "\n§3 - base texture:§r " + self.toString() +
                    "\n§3 - variates:§r no"
                    ;
        }
    }

    private static class ETFTextureMultiple extends ETFTextureVariator {

        public final @NotNull EntityIntLRU entitySuffixMap = new EntityIntLRU(500);
        private final @NotNull Int2ObjectArrayMap<ETFTexture> variantMap = new Int2ObjectArrayMap<>();
        final @NotNull ETFApi.ETFVariantSuffixProvider suffixProvider;
        private final @NotNull Identifier vanillaId;

        ETFTextureMultiple(@NotNull Identifier vanillaId, @NotNull ETFApi.ETFVariantSuffixProvider suffixProvider) {
            this.vanillaId = vanillaId;
            entitySuffixMap.defaultReturnValue(-1);
            this.suffixProvider = suffixProvider;
            Identifier directorized = ETFDirectory.getDirectoryVersionOf(vanillaId);

            ETFTexture vanilla = ETFManager.getInstance().getETFTextureNoVariation(directorized == null ? vanillaId : directorized);

            variantMap.put(1, vanilla);
//            variantMap.put(0, vanilla);
            variantMap.defaultReturnValue(vanilla);

            boolean logging = ETFConfigData.logTextureDataInitialization;
            if(logging) ETFUtils2.logMessage("Initializing texture for the first time: " + vanillaId);

            IntOpenHashSet suffixes = suffixProvider.getAllSuffixes();
            suffixes.remove(0);
            suffixes.remove(1);
            for (int suffix :
                    suffixes) {
                Identifier variant = ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaId, suffix));
                if(logging) ETFUtils2.logMessage(" - looked for variant: " + variant);
                if (variant != null) {
                    variantMap.put(suffix, ETFManager.getInstance().getETFTextureNoVariation(variant));
                } else {
                    if(logging) ETFUtils2.logMessage("   - failed to find variant: "+suffix);
                    variantMap.put(suffix, vanilla);
                }
            }
            if(logging) {
                ETFUtils2.logMessage("Final variant map for: " + vanillaId);
                variantMap.forEach((k, v) -> ETFUtils2.logMessage(" - "+k + " = " + v));
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ETFTextureMultiple that = (ETFTextureMultiple) o;
            return vanillaId.equals(that.vanillaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vanillaId);
        }

        public String getPrintout() {
            return "§bTexture: §r" +
                    "\n§3 - base texture:§r "  + vanillaId +
                    "\n§3 - variates:§r yes"  +
                    "\n§3 - set by properties:§r " + (suffixProvider instanceof PropertiesRandomProvider) +
                    "\n§3 - variant count:§r " + variantMap.size() +
                    "\n§3 - all suffixes:§r " + variantMap.keySet()
                    ;
        }

        public void checkIfShouldExpireEntity(UUID id) {
            if (suffixProvider.entityCanUpdate(id)) {
                switch (ETFConfigData.textureUpdateFrequency_V2){
                    case Never -> {}
                    case Instant -> this.entitySuffixMap.removeInt(id);
                    default -> {
                        int delay = ETFConfigData.textureUpdateFrequency_V2.getDelay();
                        int time = (int) (ETFRenderContext.getCurrentEntity().etf$getWorld().getTime() % delay);
                        if (time ==  Math.abs(id.hashCode()) % delay) {
                            this.entitySuffixMap.removeInt(id);
                        }
                    }
                }
            }
        }

        @Override
        protected @NotNull ETFTexture getVariantOfInternal(@NotNull ETFEntity entity) {

            ETFManager.TextureSource source;
            if (ETFRenderContext.isRenderingFeatures()) {
                source = ETFManager.TextureSource.ENTITY_FEATURE;//this is still needed to speed up some feature renderers
            } else if (entity.etf$isBlockEntity()) {
                source = ETFManager.TextureSource.BLOCK_ENTITY;//todo still needed in rewrite?
            } else {
                source = ETFManager.TextureSource.ENTITY;
            }

            UUID id = entity.etf$getUuid();
            int knownSuffix = entitySuffixMap.getInt(id);
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
                if (suffixProvider instanceof PropertiesRandomProvider) {
                    newSuffix = suffixProvider.getSuffixForETFEntity(entity);
                } else {
                    //try to use the base entities suffix first
                    int baseEntitySuffix = ETFManager.getInstance().LAST_SUFFIX_OF_ENTITY.getInt(ETFRenderContext.getCurrentEntity().etf$getUuid());
                    if (baseEntitySuffix != -1 && variantMap.containsKey(baseEntitySuffix)) {
                        newSuffix = baseEntitySuffix;
                    } else {
                        newSuffix = suffixProvider.getSuffixForETFEntity(entity);
                    }
                }
            } else {
                newSuffix = suffixProvider.getSuffixForETFEntity(entity);
                ETFManager.getInstance().LAST_SUFFIX_OF_ENTITY.put(id, newSuffix);
            }

            //System.out.println("new = "+newSuffix);
            entitySuffixMap.put(id, newSuffix);
            return variantMap.get(newSuffix);
        }
    }
}
