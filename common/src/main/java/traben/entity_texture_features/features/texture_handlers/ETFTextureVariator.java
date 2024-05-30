package traben.entity_texture_features.features.texture_handlers;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.ETF;
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

import net.minecraft.resources.ResourceLocation;

public abstract class ETFTextureVariator {


    public static @NotNull ETFTextureVariator of(@NotNull final ResourceLocation vanillaIdentifier) {
        //if (ETF.config().getConfig().enableCustomTextures) {
        ETFApi.ETFVariantSuffixProvider variantProvider = ETFApi.ETFVariantSuffixProvider.getVariantProviderOrNull(
                ETFUtils2.replaceIdentifier(vanillaIdentifier, ".png", ".properties"),
                vanillaIdentifier,
                "skins", "textures"
        );
        if (variantProvider != null) {
            return new ETFTextureMultiple(vanillaIdentifier, variantProvider);
        }
        //}
        return new ETFTextureSingleton(vanillaIdentifier);
    }

    public ETFTexture getVariantOf(@NotNull ETFEntity entity) {


        if (ETFManager.getInstance().ENTITY_DEBUG != null
                && ETFManager.getInstance().ENTITY_DEBUG.equals(entity.etf$getUuid())) {
            boolean inChat = ETF.config().getConfig().debugLoggingMode == ETFConfig.DebugLogMode.Chat;

            ETFTexture output = getVariantOfInternal(entity);

            //noinspection DataFlowIssue,TextBlockMigration
            ETFUtils2.logMessage(
                    "\n§e-----------ETF Debug Printout-------------§r" +
                            "\n" + ETFManager.getInstance().getGeneralPrintout() +
                            "\n§eEntity:§r" +
                            "\n§6 - type:§r " + (entity.etf$getType() != null ? entity.etf$getType().getDescriptionId() : null) +
                            "\n§6 - texture:§r " + output +
                            "\n§6 - can_update_variant:§r " + (this instanceof ETFTextureMultiple multi && multi.suffixProvider.entityCanUpdate(entity.etf$getUuid())) +
//                            "\n§6 - last matching rule:§r " + ETFManager.getInstance().LAST_MET_RULE_INDEX.getInt(entity.etf$getUuid()) +
                            "\n" + getVanillaVariantDetails() +
                            "\n" + getPrintout() +
                            "\n§e----------------------------------------§r"
                    , inChat);
            ETFManager.getInstance().ENTITY_DEBUG = null;

            return output;
        }
        return getVariantOfInternal(entity);
    }

    private String getVanillaVariantDetails() {
        ResourceLocation vanilla = getVanillaIdentifier();
        if (vanilla == null) return "§aProperty locations: NULL§r";
        ResourceLocation property = ETFUtils2.replaceIdentifier(vanilla, ".png", ".properties");
        if (property == null) return "§aProperty locations: NULL§r";

        ResourceLocation optifine = ETFDirectory.getIdentifierAsDirectory(property, ETFDirectory.OPTIFINE);
        ResourceLocation optifineOld = ETFDirectory.getIdentifierAsDirectory(property, ETFDirectory.OLD_OPTIFINE);
        ResourceLocation etf = ETFDirectory.getIdentifierAsDirectory(property, ETFDirectory.ETF);

        return "§aProperty locations:§r" +
                "\n§2 - regular:§r " + property +
                "\n§2 - etf:§r " + etf +
                "\n§2 - optifine:§r " + optifine +
                "\n§2 - optifine_old:§r " + optifineOld;
    }


    public abstract String getPrintout();

    protected abstract @NotNull ETFTexture getVariantOfInternal(@NotNull ETFEntity entity);

    protected abstract ResourceLocation getVanillaIdentifier();

    public static class ETFTextureSingleton extends ETFTextureVariator {
        private final ETFTexture self;
        private final ResourceLocation vanilla;

        public ETFTextureSingleton(ResourceLocation singletonId) {
            vanilla = singletonId;
            self = ETFManager.getInstance().getETFTextureNoVariation(singletonId);

            if (ETF.config().getConfig().logTextureDataInitialization) {
                ETFUtils2.logMessage("Initializing texture for the first time: " + singletonId);
                ETFUtils2.logMessage(" - no variants for: " + self);

            }
        }


        @Override
        protected @NotNull ETFTexture getVariantOfInternal(@NotNull ETFEntity entity) {
            return self;
        }

        @Override
        protected ResourceLocation getVanillaIdentifier() {
            return vanilla;
        }

        public String getPrintout() {
            return "§bTexture: §r" +
                    "\n§3 - base texture:§r " + self.toString() +
                    "\n§3 - variates:§r no"
                    ;
        }
    }

    private static class ETFTextureMultiple extends ETFTextureVariator {

        public final @NotNull EntityIntLRU entitySuffixMap = new EntityIntLRU(500);
        final @NotNull ETFApi.ETFVariantSuffixProvider suffixProvider;
        private final @NotNull Int2ObjectArrayMap<ETFTexture> variantMap = new Int2ObjectArrayMap<>();
        private final @NotNull ResourceLocation vanillaId;

        ETFTextureMultiple(@NotNull ResourceLocation vanillaId, @NotNull ETFApi.ETFVariantSuffixProvider suffixProvider) {
            this.vanillaId = vanillaId;
            entitySuffixMap.defaultReturnValue(-1);
            this.suffixProvider = suffixProvider;
            if (suffixProvider instanceof PropertiesRandomProvider) {
                ((PropertiesRandomProvider) suffixProvider).setOnMeetsRuleHook((entity, rule) -> {
                    if (rule == null) {
                        ETFManager.getInstance().LAST_RULE_INDEX_OF_ENTITY.removeInt(entity.etf$getUuid());
                    } else {
                        ETFManager.getInstance().LAST_RULE_INDEX_OF_ENTITY.put(entity.etf$getUuid(), rule.RULE_NUMBER);
                    }
                });
            }
            ResourceLocation directorized = ETFDirectory.getDirectoryVersionOf(vanillaId);

            ETFTexture vanilla = ETFManager.getInstance().getETFTextureNoVariation(directorized == null ? vanillaId : directorized);

            variantMap.put(1, vanilla);
//            variantMap.put(0, vanilla);
            variantMap.defaultReturnValue(vanilla);

            boolean logging = ETF.config().getConfig().logTextureDataInitialization;
            if (logging) ETFUtils2.logMessage("Initializing texture for the first time: " + vanillaId);

            IntOpenHashSet suffixes = suffixProvider.getAllSuffixes();
            suffixes.remove(0);
            suffixes.remove(1);
            for (int suffix :
                    suffixes) {
                ResourceLocation variant = ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaId, suffix));
                if (logging) ETFUtils2.logMessage(" - looked for variant: " + variant);
                if (variant != null) {
                    variantMap.put(suffix, ETFManager.getInstance().getETFTextureNoVariation(variant));
                } else {
                    if (logging) ETFUtils2.logMessage("   - failed to find variant: " + suffix);
                    variantMap.put(suffix, vanilla);
                }
            }
            if (logging) {
                ETFUtils2.logMessage("Final variant map for: " + vanillaId);
                variantMap.forEach((k, v) -> ETFUtils2.logMessage(" - " + k + " = " + v));
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
                    "\n§3 - base texture:§r " + vanillaId +
                    "\n§3 - variates:§r yes" +
                    "\n§3 - set by properties:§r " + (suffixProvider instanceof PropertiesRandomProvider) +
                    "\n§3 - variant count:§r " + variantMap.size() +
                    "\n§3 - all suffixes:§r " + variantMap.keySet()
                    ;
        }

        public void checkIfShouldExpireEntity(UUID id) {
            if (suffixProvider.entityCanUpdate(id)) {
                switch (ETF.config().getConfig().textureUpdateFrequency_V2) {
                    case Never -> {
                    }
                    case Instant -> this.entitySuffixMap.removeInt(id);
                    default -> {
                        int delay = ETF.config().getConfig().textureUpdateFrequency_V2.getDelay();
                        assert ETFRenderContext.getCurrentEntity() != null;
                        int time = (int) (ETFRenderContext.getCurrentEntity().etf$getWorld().getGameTime() % delay);
                        if (time == Math.abs(id.hashCode()) % delay) {
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
                    int baseEntitySuffix = ETFRenderContext.getCurrentEntity() == null ? -1 :
                            ETFManager.getInstance().LAST_SUFFIX_OF_ENTITY.getInt(ETFRenderContext.getCurrentEntity().etf$getUuid());
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

        @Override
        protected ResourceLocation getVanillaIdentifier() {
            return vanillaId;
        }
    }
}
