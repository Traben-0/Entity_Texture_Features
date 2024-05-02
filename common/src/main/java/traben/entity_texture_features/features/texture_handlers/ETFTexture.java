package traben.entity_texture_features.features.texture_handlers;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//can either refer to a vanilla identifier or a variant
public class ETFTexture {


    public final static String PATCH_NAMESPACE_PREFIX = "etf_patched_";

    //this variants id , might be vanilla
    public final ResourceLocation thisIdentifier;
    private final int variantNumber;
    public TextureReturnState currentTextureState = TextureReturnState.NORMAL;
    public String eSuffix = null;
    //a variation of thisIdentifier but with emissive texture pixels removed for z-fighting solution
    private ResourceLocation thisIdentifier_Patched = null;
    //the emissive version of this texture
    private ResourceLocation emissiveIdentifier = null;
    private ResourceLocation emissiveBlinkIdentifier = null;
    private ResourceLocation emissiveBlink2Identifier = null;

    private ResourceLocation enchantIdentifier = null;
    private ResourceLocation enchantBlinkIdentifier = null;
    private ResourceLocation enchantBlink2Identifier = null;
    private ResourceLocation blinkIdentifier = null;
    private ResourceLocation blink2Identifier = null;
    private ResourceLocation blinkIdentifier_Patched = null;
    private ResourceLocation blink2Identifier_Patched = null;
    private Integer blinkLength = ETF.config().getConfig().blinkLength;
    private Integer blinkFrequency = ETF.config().getConfig().blinkFrequency;

    // private final TextureSource source;

    private boolean isBuilt = false;
    private ETFSprite atlasSprite = null;
    private boolean hasBeenReRegistered = false;
    private Boolean resourceExists = null;
    private boolean guiBlink = false;
    private boolean hasPatched = false;

    public ETFTexture(ResourceLocation variantIdentifier) {

        if (variantIdentifier == null) {
            ETFUtils2.logError("ETFTexture had a null identifier this MUST never happen");
            //throw new IllegalArgumentException("ETFTexture had null identifier");
            thisIdentifier = null;
            variantNumber = 0;
            return;
        }

        this.thisIdentifier = variantIdentifier;
        Pattern pattern = Pattern.compile("\\d+(?=\\.png)");
        Matcher matcher = pattern.matcher(variantIdentifier.getPath());
        int intFound = 0;
        try {
            if (matcher.find()) {
                intFound = Integer.parseInt(matcher.group());

            }
        } catch (NumberFormatException ignored) {
            // this.variantNumber = 0;
        }
        this.variantNumber = intFound;
//        canPatch = allowedToPatch;

        setupBlinking();
        setupEmissives();
        setupEnchants();
    }

    //alternative initiator for already known textures used for players
    public ETFTexture(@NotNull ResourceLocation modifiedSkinIdentifier,
                      @Nullable ResourceLocation blinkIdentifier,
                      @Nullable ResourceLocation blink2Identifier,
                      @Nullable ResourceLocation emissiveIdentifier,
                      @Nullable ResourceLocation blinkEmissiveIdentifier,
                      @Nullable ResourceLocation blink2EmissiveIdentifier,
                      @Nullable ResourceLocation enchantIdentifier,
                      @Nullable ResourceLocation blinkenchantIdentifier,
                      @Nullable ResourceLocation blink2enchantIdentifier,
                      @Nullable ResourceLocation patchIdentifier,
                      @Nullable ResourceLocation blinkpatchIdentifier,
                      @Nullable ResourceLocation blink2patchIdentifier) {
        //ALL input already tested and confirmed existing
        this.variantNumber = 0;
        this.thisIdentifier = modifiedSkinIdentifier;
        this.blinkIdentifier = blinkIdentifier;
        this.blink2Identifier = blink2Identifier;
        this.emissiveIdentifier = emissiveIdentifier;
        this.emissiveBlinkIdentifier = blinkEmissiveIdentifier;
        this.emissiveBlink2Identifier = blink2EmissiveIdentifier;
        this.thisIdentifier_Patched = patchIdentifier;
        this.blinkIdentifier_Patched = blinkpatchIdentifier;
        this.blink2Identifier_Patched = blink2patchIdentifier;
        this.enchantIdentifier = enchantIdentifier;
        this.enchantBlinkIdentifier = blinkenchantIdentifier;
        this.enchantBlink2Identifier = blink2enchantIdentifier;

        hasPatched = thisIdentifier_Patched != null;
        if (hasPatched) {
            ETFManager.getInstance().ETF_TEXTURE_CACHE.put(thisIdentifier_Patched, this);
            if (blinkIdentifier_Patched != null)
                ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blinkIdentifier_Patched, this);
            if (blink2Identifier_Patched != null)
                ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blink2Identifier_Patched, this);
        }


        //register this etf texture
        ETFManager.getInstance().ETF_TEXTURE_CACHE.put(thisIdentifier, this);
        if (blinkIdentifier != null)
            ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blinkIdentifier, this);
        if (blink2Identifier != null)
            ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blink2Identifier, this);
    }


    //alternative initiator for already known textures used for MooShroom's mushrooms
    private ETFTexture(@NotNull ResourceLocation modifiedSkinIdentifier,
                       @Nullable ResourceLocation emissiveIdentifier) {

        //ALL input already tested and confirmed existing
        this.variantNumber = 0;
        this.thisIdentifier = modifiedSkinIdentifier;
        this.emissiveIdentifier = emissiveIdentifier;

//        ETFManager.getInstance().registerStaticallyCreatedTexture(thisIdentifier,this);
    }

    public static ETFTexture ofUnmodifiable(@NotNull ResourceLocation identifier, @Nullable ResourceLocation emissiveIdentifier) {
        return new ETFTexture(identifier, emissiveIdentifier);
    }

    public static void patchTextureToRemoveZFightingWithOtherTexture(NativeImage baseImage, NativeImage otherImage) throws IndexOutOfBoundsException {
        //here we alter the first image removing all pixels that are present in the second image to prevent z fighting
        //this does not support transparency and is a hard counter to f-fighting
        try {
            if (otherImage.getWidth() == baseImage.getWidth() && otherImage.getHeight() == baseImage.getHeight()) {
                //float widthMultipleEmissive  = originalCopy.getWidth()  / (float)emissive.getWidth();
                //float heightMultipleEmissive = originalCopy.getHeight() / (float)emissive.getHeight();

                for (int x = 0; x < baseImage.getWidth(); x++) {
                    for (int y = 0; y < baseImage.getHeight(); y++) {
                        //int newX = Math.min((int)(x*widthMultipleEmissive),originalCopy.getWidth()-1);
                        //int newY = Math.min((int)(y*heightMultipleEmissive),originalCopy.getHeight()-1);
                        if (otherImage.getLuminanceOrAlpha(x, y) != 0) {
                            baseImage.setPixelRGBA(x, y, 0);
                        }
                    }
                }
            }
            //return baseImage;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IndexOutOfBoundsException("additional texture is not the correct size, ETF has crashed in the patching stage");
        }
    }

    private static boolean doesAnimaticaVersionExist(ResourceLocation identifier) {
        if (identifier == null) return false;
        String idString = identifier.toString();
        //check if its already an anim and animatica has already gotten to replacing it before etf sees it
        if (idString.endsWith("-anim")) return true;
        //check if animatica has registered its animated version of this texture
        return Minecraft.getInstance().getTextureManager().getTexture(new ResourceLocation(idString + "-anim"), null) != null;
    }

    public int getVariantNumber() {
        return variantNumber;
    }

    private void setupBlinking() {
        if (ETF.config().getConfig().enableBlinking) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Optional<Resource> vanillaR1 = resourceManager.getResource(thisIdentifier);
            if (vanillaR1.isPresent()) {
                ResourceLocation possibleBlinkIdentifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink.png");
                Optional<Resource> blinkR1 = resourceManager.getResource(possibleBlinkIdentifier);
                if (blinkR1.isPresent()) {

                    String blink1PackName = blinkR1.get().sourcePackId();
                    //ObjectSet<String> packs = new ObjectOpenHashSet<>();
                    // packs.add(blink1PackName);
                    // packs.add(vanillaR1.get().getPackId());

                    if (blink1PackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(blink1PackName, vanillaR1.get().sourcePackId()))) {
                        //is higher or same pack
                        blinkIdentifier = possibleBlinkIdentifier;


                        ResourceLocation possibleBlink2Identifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink2.png");
                        Optional<Resource> blinkR2 = resourceManager.getResource(possibleBlink2Identifier);
                        if (blinkR2.isPresent()) {
                            String blink2PackName = blinkR2.get().sourcePackId();
                            if (blink1PackName.equals(blink2PackName)) {
                                blink2Identifier = possibleBlink2Identifier;
                            }
                        }

                        //read possible blinking properties
                        ResourceLocation propertyIdentifier = ETFUtils2.replaceIdentifier(possibleBlinkIdentifier, ".png", ".properties");
                        Properties blinkingProps = ETFUtils2.readAndReturnPropertiesElseNull(propertyIdentifier);
                        if (blinkingProps != null) {
                            Optional<Resource> propertyResource = resourceManager.getResource(propertyIdentifier);
                            if (propertyResource.isPresent()) {
                                String propertyResourcePackName = propertyResource.get().sourcePackId();
                                //packs.clear();
                                //packs.add(propertyResourcePackName);
                                //packs.add(blink1PackName);

                                if (propertyResourcePackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(propertyResourcePackName, blink1PackName))) {
                                    blinkLength = blinkingProps.containsKey("blinkLength") ?
                                            Integer.parseInt(blinkingProps.getProperty("blinkLength").replaceAll("\\D", "")) :
                                            ETF.config().getConfig().blinkLength;
                                    blinkFrequency = blinkingProps.containsKey("blinkFrequency") ?
                                            Integer.parseInt(blinkingProps.getProperty("blinkFrequency").replaceAll("\\D", "")) :
                                            ETF.config().getConfig().blinkFrequency;

                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public boolean exists() {
        if (resourceExists == null) {
            resourceExists = Minecraft.getInstance().getResourceManager().getResource(thisIdentifier).isPresent();
        }
        return isBuilt || resourceExists;
    }

    public void buildTrimTexture(ArmorTrim trim, boolean leggings) {
//        trim=minecraft:trims/models/armor/rib_gold
//        trim2=minecraft:trims/models/armor/rib_leggings_gold
        try {
            String mat = trim.material().value().assetName();
            String namespace = trim.pattern().value().assetId().getNamespace();
            String pattern = trim.pattern().value().assetId().getPath() + (leggings ? "_leggings" : "");

            NativeImage patternImg = ETFUtils2.getNativeImageElseNull(new ResourceLocation(namespace, "textures/trims/models/armor/" + pattern + ".png"));

            NativeImage matImg = ETFUtils2.getNativeImageElseNull(new ResourceLocation(namespace, "textures/trims/color_palettes/" + mat + ".png"));
            NativeImage palletteImg = ETFUtils2.getNativeImageElseNull(new ResourceLocation(namespace, "textures/trims/color_palettes/trim_palette.png"));

            if (matImg != null && palletteImg != null && patternImg != null) {
                Int2IntOpenHashMap palletteMap = new Int2IntOpenHashMap();
                for (int i = 0; i < palletteImg.getWidth(); i++) {
                    for (int j = 0; j < palletteImg.getHeight(); j++) {
                        palletteMap.put(palletteImg.getPixelRGBA(i, j), matImg.getPixelRGBA(i, j));
                    }
                }
                try (NativeImage newImage = ETFUtils2.emptyNativeImage(patternImg.getWidth(), patternImg.getHeight())) {
                    for (int i = 0; i < patternImg.getWidth(); i++) {
                        for (int j = 0; j < patternImg.getHeight(); j++) {
                            int colour = patternImg.getPixelRGBA(i, j);
                            if (palletteMap.containsKey(colour)) {
                                newImage.setPixelRGBA(i, j, palletteMap.get(colour));
                            } else {
                                newImage.setPixelRGBA(i, j, colour);
                            }
                        }
                    }
                    ETFUtils2.registerNativeImageToIdentifier(newImage, thisIdentifier);
                } catch (Exception b) {
                    // make empty
                    thisIdentifier_Patched = ETFManager.getErrorETFTexture().thisIdentifier;
                }
            } else {
                // make empty
                thisIdentifier_Patched = ETFManager.getErrorETFTexture().thisIdentifier;
            }
        } catch (Exception e) {
            // make empty
            thisIdentifier_Patched = ETFManager.getErrorETFTexture().thisIdentifier;

        }
        isBuilt = true;
    }

    private void setupEmissives() {

        //if (ETF.config().getConfig().enableEmissiveTextures) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        for (String possibleEmissiveSuffix :
                ETFManager.getInstance().EMISSIVE_SUFFIX_LIST) {
            var vanillaR1 = getResourceOrModifyForTrims(resourceManager);
            if (vanillaR1.isPresent()) {
                ResourceLocation possibleEmissiveIdentifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", possibleEmissiveSuffix + ".png");
                Optional<Resource> emissiveR1 = resourceManager.getResource(possibleEmissiveIdentifier);
                if (emissiveR1.isPresent()) {

                    String emissivePackName = emissiveR1.get().sourcePackId();
//                        ObjectSet<String> packs = new ObjectOpenHashSet<>();
//                        packs.add(emissivePackName);
//                        packs.add(vanillaR1.get().getPackId());
                    if (emissivePackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(emissivePackName, vanillaR1.get().sourcePackId()))) {
                        //is higher or same pack
                        emissiveIdentifier = possibleEmissiveIdentifier;
                        ResourceLocation possibleEmissiveBlinkIdentifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink" + possibleEmissiveSuffix + ".png");
                        Optional<Resource> emissiveBlinkR1 = resourceManager.getResource(possibleEmissiveBlinkIdentifier);
                        if (emissiveBlinkR1.isPresent()) {

                            String emissiveBlinkPackName = emissiveBlinkR1.get().sourcePackId();
                            //packs.clear();
                            //packs.add(emissiveBlinkPackName);
                            //packs.add(vanillaR1.get().getPackId());
                            if (emissiveBlinkPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(emissiveBlinkPackName, vanillaR1.get().sourcePackId()))) {
                                //is higher or same pack
                                emissiveBlinkIdentifier = possibleEmissiveBlinkIdentifier;
                                ResourceLocation possibleEmissiveBlink2Identifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink2" + possibleEmissiveSuffix + ".png");
                                Optional<Resource> emissiveBlink2R1 = resourceManager.getResource(possibleEmissiveBlink2Identifier);
                                if (emissiveBlink2R1.isPresent()) {
                                    String emissiveBlink2PackName = emissiveBlink2R1.get().sourcePackId();
                                    // packs.clear();
                                    // packs.add(emissiveBlink2PackName);
                                    // packs.add(vanillaR1.get().getPackId());
                                    if (emissiveBlink2PackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(emissiveBlink2PackName, vanillaR1.get().sourcePackId()))) {
                                        //is higher or same pack
                                        emissiveBlink2Identifier = possibleEmissiveBlink2Identifier;
                                    }
                                }
                            }
                        }
                        //emissive found and is valid
                        eSuffix = possibleEmissiveSuffix;
                        break;
                    }
                }
            }
        }
//            if (isEmissive())
//                createPatchedTextures();
        //}
    }

    private void setupEnchants() {

        if (ETF.config().getConfig().enableEnchantedTextures) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

            String enchantSuffix = "_enchant";
            Optional<Resource> vanillaR1 = getResourceOrModifyForTrims(resourceManager);
            if (vanillaR1.isPresent()) {
                ResourceLocation possibleEnchantIdentifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", enchantSuffix + ".png");
                Optional<Resource> enchantR1 = resourceManager.getResource(possibleEnchantIdentifier);
                if (enchantR1.isPresent()) {

                    String enchantPackName = enchantR1.get().sourcePackId();
//                        ObjectSet<String> packs = new ObjectOpenHashSet<>();
//                        packs.add(emissivePackName);
//                        packs.add(vanillaR1.get().getPackId());
                    if (enchantPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantPackName, vanillaR1.get().sourcePackId()))) {
                        //is higher or same pack
                        enchantIdentifier = possibleEnchantIdentifier;
                        ResourceLocation possibleEnchantBlinkIdentifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink" + enchantSuffix + ".png");
                        Optional<Resource> enchantBlinkR1 = resourceManager.getResource(possibleEnchantBlinkIdentifier);
                        if (enchantBlinkR1.isPresent()) {

                            String enchantBlinkPackName = enchantBlinkR1.get().sourcePackId();
                            //packs.clear();
                            //packs.add(emissiveBlinkPackName);
                            //packs.add(vanillaR1.get().getPackId());
                            if (enchantBlinkPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantBlinkPackName, vanillaR1.get().sourcePackId()))) {
                                //is higher or same pack
                                enchantBlinkIdentifier = possibleEnchantBlinkIdentifier;
                                ResourceLocation possibleEnchantBlink2Identifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink2" + enchantSuffix + ".png");
                                Optional<Resource> enchantBlink2R1 = resourceManager.getResource(possibleEnchantBlink2Identifier);
                                if (enchantBlink2R1.isPresent()) {
                                    String enchantBlink2PackName = enchantBlink2R1.get().sourcePackId();
                                    // packs.clear();
                                    // packs.add(emissiveBlink2PackName);
                                    // packs.add(vanillaR1.get().getPackId());
                                    if (enchantBlink2PackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(enchantBlink2PackName, vanillaR1.get().sourcePackId()))) {
                                        //is higher or same pack
                                        enchantBlink2Identifier = possibleEnchantBlink2Identifier;
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            if (isEnchanted())
//                createPatchedTextures();
        }
    }

    private Optional<Resource> getResourceOrModifyForTrims(final ResourceManager resourceManager) {
        Optional<Resource> vanillaR1 = resourceManager.getResource(thisIdentifier);
        if (vanillaR1.isEmpty() && thisIdentifier.getPath().contains("textures/trims/models/armor/")) {
            //create this armor trim as an identifier because fuck Sprites, all my homies hate Sprites

            //try get an armor trims base texture just to match what texture pack level it is
            PackResources pack;
            vanillaR1 = resourceManager.getResource(new ResourceLocation(thisIdentifier.getNamespace(), thisIdentifier.getPath().replaceAll("_(.*?)(?=\\.png)", "")));
            if (vanillaR1.isPresent()) {
                pack = vanillaR1.get().source();
            } else {
                pack = Minecraft.getInstance().getVanillaPackResources();
            }
            //create resource object sufficient for following code
            vanillaR1 = Optional.of(new Resource(pack, null));
        }
        return vanillaR1;
    }

    /**
     * Re registers the base texture to a new identifier, this fixes some iris stuff with armor.
     */
    public void reRegisterBaseTexture() {
        if (hasBeenReRegistered) return;
        hasPatched = true;
        hasBeenReRegistered = true;
        NativeImage newBaseTexture = ETFUtils2.getNativeImageElseNull(thisIdentifier);
        if (newBaseTexture != null) {
            var newPatchIdentifier = new ResourceLocation(PATCH_NAMESPACE_PREFIX + thisIdentifier.getNamespace(), thisIdentifier.getPath());
            if (ETFUtils2.registerNativeImageToIdentifier(newBaseTexture, newPatchIdentifier)) {
                thisIdentifier_Patched = newPatchIdentifier;
                ETFManager.getInstance().ETF_TEXTURE_CACHE.put(thisIdentifier_Patched, this);
            } else {
                //assert
                thisIdentifier_Patched = null;
            }
        }

    }

    @NotNull
    public ResourceLocation getTextureIdentifier(@Nullable ETFEntity entity) {
        if (canPatch()) {
            //patched required
            currentTextureState = TextureReturnState.NORMAL_PATCHED;
        } else {
            currentTextureState = TextureReturnState.NORMAL;
        }
        return getBlinkingIdentifier(entity);
    }

    @NotNull
    private ResourceLocation getBlinkingIdentifier(@Nullable ETFEntity entity) {
        if (!doesBlink() || !(entity instanceof LivingEntity)) {
            return identifierOfCurrentState();
        }


        if (guiBlink) {
            setBlink(Math.abs((int) System.currentTimeMillis() / 20 % 50000), 0);
        } else if (((LivingEntity) entity).hasPose(Pose.SLEEPING)) {
            //force eyes closed if asleep
            modifyTextureState(TextureReturnState.APPLY_BLINK);
        } else if (((LivingEntity) entity).hasEffect(MobEffects.BLINDNESS)) {
            //force eyes closed if blinded
            modifyTextureState(doesBlink2() ? TextureReturnState.APPLY_BLINK2 : TextureReturnState.APPLY_BLINK);
        } else {
            //do regular blinking
            setBlink(((LivingEntity) entity).tickCount, Math.abs(entity.etf$getUuid().hashCode()));
        }
        return identifierOfCurrentState();
    }

    private void setBlink(int currentTime, int hash) {
        int uuidHash = hash % (blinkFrequency * 2) + 20 + blinkFrequency;
        int timeModulated = Math.abs((currentTime % uuidHash));

        if (timeModulated <= blinkLength + blinkLength) {
            if (doesBlink2()) {
                if (timeModulated >= (blinkLength / 1.5) && timeModulated <= blinkLength + 1 + (blinkLength / 3)) {
                    modifyTextureState(TextureReturnState.APPLY_BLINK);
                } else {
                    modifyTextureState(TextureReturnState.APPLY_BLINK2);
                }
            } else {
                modifyTextureState(TextureReturnState.APPLY_BLINK);
            }
        }
    }

    public void setGUIBlink() {
//        System.out.println("set blink");
        blinkFrequency = 100;
        blinkLength = 40;
        guiBlink = true;
    }

    public boolean isEmissive() {
        return this.emissiveIdentifier != null;
    }

    public boolean isEnchanted() {
        return this.enchantIdentifier != null;
    }

    public boolean canPatch() {
        return ETFRenderContext.isAllowedToPatch() && this.thisIdentifier_Patched != null;
    }

    public boolean doesBlink() {
        return this.blinkIdentifier != null;
    }

    @NotNull
    public ETFSprite getSprite(@NotNull TextureAtlasSprite originalSprite) {
        if (atlasSprite == null) {
            atlasSprite = new ETFSprite(originalSprite, this);
        }
        return atlasSprite;
    }

    public boolean doesBlink2() {
        return this.blink2Identifier != null;
    }

    @Override
    public String toString() {
        return "[" + this.thisIdentifier.toString() + ", emissive=" + isEmissive() + ", blinks=" + doesBlink() + "]";
    }

    public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, ModelPart modelPart) {
        renderEmissive(matrixStack, vertexConsumerProvider, modelPart, ETFManager.getEmissiveMode());
    }

    public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, ModelPart modelPart, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        VertexConsumer vertexC = getEmissiveVertexConsumer(vertexConsumerProvider, null, modeToUsePossiblyManuallyChosen);
        if (vertexC != null) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
            modelPart.render(matrixStack, vertexC, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY);
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Model model) {
        renderEmissive(matrixStack, vertexConsumerProvider, model, ETFManager.getEmissiveMode());
    }

    public void renderEmissive(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        VertexConsumer vertexC = getEmissiveVertexConsumer(vertexConsumerProvider, model, modeToUsePossiblyManuallyChosen);
        if (vertexC != null) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
            model.renderToBuffer(matrixStack, vertexC, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    @Nullable
    public VertexConsumer getEmissiveVertexConsumer(MultiBufferSource vertexConsumerProvider, @Nullable Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        ETFRenderContext.preventRenderLayerTextureModify();
        VertexConsumer wrapped = getEmissiveVertexConsumerWrapped(vertexConsumerProvider, model, modeToUsePossiblyManuallyChosen);
        ETFRenderContext.allowRenderLayerTextureModify();
        return wrapped;

    }

    @Nullable
    private VertexConsumer getEmissiveVertexConsumerWrapped(MultiBufferSource vertexConsumerProvider, @Nullable Model model, ETFConfig.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        if (isEmissive()) {
            ResourceLocation emissiveToUse = getEmissiveIdentifierOfCurrentState();

            if (emissiveToUse != null) {

                if (modeToUsePossiblyManuallyChosen == ETFConfig.EmissiveRenderModes.BRIGHT) {
                    return vertexConsumerProvider.getBuffer(RenderType.beaconBeam(emissiveToUse, true));
                } else {
                    if (model == null) {
                        return vertexConsumerProvider.getBuffer(RenderType.entityCutoutNoCull /*RenderLayer.getEntityTranslucent*/(emissiveToUse));
                    } else {
                        return vertexConsumerProvider.getBuffer(model.renderType(emissiveToUse));
                    }
                }
            }
        }
        //return null for any fail
        return null;
    }

    private void modifyTextureState(TextureReturnState givenState) {
        switch (givenState) {
            case APPLY_BLINK ->
                    currentTextureState = currentTextureState == TextureReturnState.NORMAL_PATCHED ? TextureReturnState.BLINK_PATCHED : TextureReturnState.BLINK;
            case APPLY_BLINK2 -> currentTextureState = switch (currentTextureState) {
                case NORMAL_PATCHED, BLINK_PATCHED -> TextureReturnState.BLINK2_PATCHED;
                default -> TextureReturnState.BLINK2;
            };
        }
    }

    @NotNull
    private ResourceLocation identifierOfCurrentState() {
        return switch (currentTextureState) {
            case NORMAL -> thisIdentifier;
            case NORMAL_PATCHED -> thisIdentifier_Patched;
            case BLINK -> blinkIdentifier;
            case BLINK_PATCHED -> blinkIdentifier_Patched;
            case BLINK2 -> blink2Identifier;
            case BLINK2_PATCHED -> blink2Identifier_Patched;
            default ->
                //ETFUtils.logError("identifierOfCurrentState failed, it should not have, returning default");
                    thisIdentifier;
        };
    }

    @Nullable
    public ResourceLocation getEmissiveIdentifierOfCurrentState() {
        return switch (currentTextureState) {
            case NORMAL, NORMAL_PATCHED -> emissiveIdentifier;
            case BLINK, BLINK_PATCHED -> emissiveBlinkIdentifier;
            case BLINK2, BLINK2_PATCHED -> emissiveBlink2Identifier;
            default ->
                //ETFUtils.logError("identifierOfCurrentState failed, it should not have, returning default");
                    null;
        };
    }

    @Nullable
    public ResourceLocation getEnchantIdentifierOfCurrentState() {
        return switch (currentTextureState) {
            case NORMAL, NORMAL_PATCHED -> enchantIdentifier;
            case BLINK, BLINK_PATCHED -> enchantBlinkIdentifier;
            case BLINK2, BLINK2_PATCHED -> enchantBlink2Identifier;
            default ->
                //ETFUtils.logError("identifierOfCurrentState failed, it should not have, returning default");
                    null;
        };
    }

    public void assertPatchedTextures() {
        if (!this.isEmissive() || hasPatched) {
            return;
        }
        hasPatched = true;

        ResourceManager files = Minecraft.getInstance().getResourceManager();
        //should this process cancel itself due to presence of PBR textures
        if ((ETFVersionDifferenceHandler.isThisModLoaded("iris") || ETFVersionDifferenceHandler.isThisModLoaded("oculus")) &&
                //do pbr files exist?
                (files.getResource(ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_s.png")).isPresent() ||
                        files.getResource(ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_n.png")).isPresent())
        ) {
            //here the setting to cancel is enabled, iris is present, and pbr files exist, so cancel patching
            return;
        }

        //should patching cancel due to presence of animation mods and animated textures
        if (((ETFVersionDifferenceHandler.isThisModLoaded("animatica")) && (
                doesAnimaticaVersionExist(thisIdentifier)
                        || doesAnimaticaVersionExist(emissiveIdentifier)
        ) || (ETFVersionDifferenceHandler.isThisModLoaded("moremcmeta") &&
                (files.getResource(ETFUtils2.replaceIdentifier(thisIdentifier, ".png", ".png.mcmeta")).isPresent() ||
                        files.getResource(ETFUtils2.replaceIdentifier(thisIdentifier, ".png", ".png.moremcmeta")).isPresent())
        )
        )) {
            //here the setting to cancel is enabled, animation mod is present, and mcmeta files exist, so cancel patching
            return;
        }

        //here we will 'patch' the base texture to prevent z-fighting when it occurs

        //null depending on existence
        NativeImage newBaseTexture = ETFUtils2.getNativeImageElseNull(thisIdentifier);
        NativeImage newBlinkTexture = ETFUtils2.getNativeImageElseNull(blinkIdentifier);
        NativeImage newBlink2Texture = ETFUtils2.getNativeImageElseNull(blink2Identifier);

        boolean didPatch = false;

        //patch out emissive textures for shader z fighting fix
        if (this.emissiveIdentifier != null) {// && ETF.config().getConfig().enableEmissiveTextures) {
            //create patched texture
            NativeImage emissiveImage = ETFUtils2.getNativeImageElseNull(emissiveIdentifier);
            try {
                patchTextureToRemoveZFightingWithOtherTexture(newBaseTexture, emissiveImage);
                didPatch = true;
                //no errors here means it all , and we have a patched texture in originalCopyToPatch
                //thisIdentifier_Patched = new Identifier(PATCH_NAMESPACE_PREFIX + thisIdentifier.getNamespace(), thisIdentifier.getPath());
                //ETFUtils2.registerNativeImageToIdentifier(originalCopyToPatch, thisIdentifier_Patched);

                if (doesBlink() && emissiveBlinkIdentifier != null) {
                    NativeImage emissiveBlinkImage = ETFUtils2.getNativeImageElseNull(emissiveBlinkIdentifier);
                    patchTextureToRemoveZFightingWithOtherTexture(newBlinkTexture, emissiveBlinkImage);
                    //no errors here means it all worked, and we have a patched texture in
                    //blinkIdentifier_Patched = new Identifier(PATCH_NAMESPACE_PREFIX + blinkIdentifier.getNamespace(), blinkIdentifier.getPath());
                    //ETFUtils2.registerNativeImageToIdentifier(blinkCopyToPatch, blinkIdentifier_Patched);

                    if (doesBlink2() && emissiveBlink2Identifier != null) {
                        NativeImage emissiveBlink2Image = ETFUtils2.getNativeImageElseNull(emissiveBlink2Identifier);
                        patchTextureToRemoveZFightingWithOtherTexture(newBlink2Texture, emissiveBlink2Image);
                        //no errors here means it all worked, and we have a patched texture in
                        //blink2Identifier_Patched = new Identifier(PATCH_NAMESPACE_PREFIX + blink2Identifier.getNamespace(), blink2Identifier.getPath());
                        //ETFUtils2.registerNativeImageToIdentifier(blink2CopyToPatch, blinkIdentifier_Patched);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //save successful patches after any iris or other future patching reasons
            if (didPatch) {
                thisIdentifier_Patched = new ResourceLocation(PATCH_NAMESPACE_PREFIX + thisIdentifier.getNamespace(), thisIdentifier.getPath());
                ETFUtils2.registerNativeImageToIdentifier(newBaseTexture, thisIdentifier_Patched);
                ETFManager.getInstance().ETF_TEXTURE_CACHE.put(thisIdentifier_Patched, this);
                if (doesBlink()) {
                    blinkIdentifier_Patched = new ResourceLocation(PATCH_NAMESPACE_PREFIX + blinkIdentifier.getNamespace(), blinkIdentifier.getPath());
                    ETFUtils2.registerNativeImageToIdentifier(newBlinkTexture, blinkIdentifier_Patched);
                    ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blinkIdentifier_Patched, this);
                    if (doesBlink2()) {
                        blink2Identifier_Patched = new ResourceLocation(PATCH_NAMESPACE_PREFIX + blink2Identifier.getNamespace(), blink2Identifier.getPath());
                        ETFUtils2.registerNativeImageToIdentifier(newBlink2Texture, blink2Identifier_Patched);
                        ETFManager.getInstance().ETF_TEXTURE_CACHE.put(blink2Identifier_Patched, this);
                    }
                }
            }
        }
    }

    public enum TextureReturnState {
        NORMAL,
        NORMAL_PATCHED,
        BLINK,
        BLINK_PATCHED,
        BLINK2,
        BLINK2_PATCHED,
        APPLY_PATCH,
        APPLY_BLINK,
        APPLY_BLINK2;


        @Override
        public String toString() {
            return switch (this) {
                case NORMAL -> "normal";
                case BLINK -> "blink";
                case BLINK2 -> "blink2";
                case NORMAL_PATCHED -> "normal_patched";
                case BLINK_PATCHED -> "blink_patched";
                case BLINK2_PATCHED -> "blink2_patched";
                case APPLY_BLINK -> "apply_blink";
                case APPLY_BLINK2 -> "apply_blink2";
                case APPLY_PATCH -> "apply_patch";
            };
        }
    }

}
