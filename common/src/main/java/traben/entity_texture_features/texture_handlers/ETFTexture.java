package traben.entity_texture_features.texture_handlers;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreen;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;


//can either refer to a vanilla identifier or a variant
public class ETFTexture {


    //the vanilla texture this is associated with
    //might be itself
    //private final Identifier vanillaIdentifier;

    private final static String PATCH_NAMESPACE_PREFIX = "etf_patched_";
    private static final Random randomBlink = new Random();
    //this variants id , might be vanilla
    public final Identifier thisIdentifier;
    private final Object2ReferenceOpenHashMap<Identifier, Identifier> FEATURE_TEXTURE_MAP = new Object2ReferenceOpenHashMap<>();
    private final int variantNumber;

    public int getVariantNumber(){
        return variantNumber;
    }
    public TextureReturnState currentTextureState = TextureReturnState.NORMAL;
    //a variation of thisIdentifier but with emissive texture pixels removed for z-fighting solution
    private Identifier thisIdentifier_Patched = null;
    //the emissive version of this texture
    private Identifier emissiveIdentifier = null;
    private Identifier emissiveBlinkIdentifier = null;
    private Identifier emissiveBlink2Identifier = null;
    private Identifier blinkIdentifier = null;
    private Identifier blink2Identifier = null;
    private Identifier blinkIdentifier_Patched = null;
    private Identifier blink2Identifier_Patched = null;
    private Integer blinkLength = ETFConfigData.blinkLength;
    private Integer blinkFrequency = ETFConfigData.blinkFrequency;

    // private final TextureSource source;
    private boolean canPatch = true;

    public ETFTexture(Identifier variantIdentifier, boolean allowedToPatch) {

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
        canPatch = allowedToPatch;
        setupBlinking();
        setupEmissives();


    }

    //alternative initiator for already known textures used for players
    public ETFTexture(@NotNull Identifier modifiedSkinIdentifier,
                      @Nullable Identifier blinkIdentifier,
                      @Nullable Identifier blink2Identifier,
                      @Nullable Identifier emissiveIdentifier,
                      @Nullable Identifier blinkEmissiveIdentifier,
                      @Nullable Identifier blink2EmissiveIdentifier,
                      @Nullable Identifier modifiedSkinPatchedIdentifier,
                      @Nullable Identifier modifiedSkinBlinkPatchedIdentifier,
                      @Nullable Identifier modifiedSkinBlink2PatchedIdentifier) {

        //ALL input already tested and confirmed existing
        this.variantNumber = 0;
        this.thisIdentifier = modifiedSkinIdentifier;
        this.blinkIdentifier = blinkIdentifier;
        this.blink2Identifier = blink2Identifier;
        this.emissiveIdentifier = emissiveIdentifier;
        this.emissiveBlinkIdentifier = blinkEmissiveIdentifier;
        this.emissiveBlink2Identifier = blink2EmissiveIdentifier;
        this.thisIdentifier_Patched = modifiedSkinPatchedIdentifier;
        this.blinkIdentifier_Patched = modifiedSkinBlinkPatchedIdentifier;
        this.blink2Identifier_Patched = modifiedSkinBlink2PatchedIdentifier;
        //setupBlinking(); neither required
        //setupEmissives();
        //createPatchedTextures();
    }

    //alternative initiator for already known textures used for MooShroom's mushrooms
    public ETFTexture(@NotNull Identifier modifiedSkinIdentifier,
                      @Nullable Identifier emissiveIdentifier) {

        //ALL input already tested and confirmed existing
        this.variantNumber = 0;
        this.thisIdentifier = modifiedSkinIdentifier;
        this.emissiveIdentifier = emissiveIdentifier;
        //setupBlinking(); neither required
        //setupEmissives();
        //createPatchedTextures();
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
                        if (otherImage.getOpacity(x, y) != 0) {
                            baseImage.setColor(x, y, 0);
                        }
                    }
                }
            }
            //return baseImage;
        } catch (Exception e) {
            throw new IndexOutOfBoundsException("additional texture is not the correct size, ETF has crashed in the patching stage");
        }
    }

    private void setupBlinking() {
        if (ETFConfigData.enableBlinking) {
            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
            Optional<Resource> vanillaR1 = resourceManager.getResource(thisIdentifier);
            if (vanillaR1.isPresent()) {
                Identifier possibleBlinkIdentifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink.png");
                Optional<Resource> blinkR1 = resourceManager.getResource(possibleBlinkIdentifier);
                if (blinkR1.isPresent()) {

                    String blink1PackName = blinkR1.get().getResourcePackName();
                    //ObjectSet<String> packs = new ObjectOpenHashSet<>();
                    // packs.add(blink1PackName);
                    // packs.add(vanillaR1.get().getResourcePackName());

                    if (blink1PackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{blink1PackName, vanillaR1.get().getResourcePackName()}))) {
                        //is higher or same pack
                        blinkIdentifier = possibleBlinkIdentifier;


                        Identifier possibleBlink2Identifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink2.png");
                        Optional<Resource> blinkR2 = resourceManager.getResource(possibleBlink2Identifier);
                        if (blinkR2.isPresent()) {
                            String blink2PackName = blinkR2.get().getResourcePackName();
                            if (blink1PackName.equals(blink2PackName)) {
                                blink2Identifier = possibleBlink2Identifier;
                            }
                        }

                        //read possible blinking properties
                        Identifier propertyIdentifier = ETFUtils2.replaceIdentifier(possibleBlinkIdentifier, ".png", ".properties");
                        Properties blinkingProps = ETFUtils2.readAndReturnPropertiesElseNull(propertyIdentifier);
                        if (blinkingProps != null) {
                            Optional<Resource> propertyResource = resourceManager.getResource(propertyIdentifier);
                            if (propertyResource.isPresent()) {
                                String propertyResourcePackName = propertyResource.get().getResourcePackName();
                                //packs.clear();
                                //packs.add(propertyResourcePackName);
                                //packs.add(blink1PackName);

                                if (propertyResourcePackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{propertyResourcePackName, blink1PackName}))) {
                                    blinkLength = blinkingProps.containsKey("blinkLength") ?
                                            Integer.parseInt(blinkingProps.getProperty("blinkLength").replaceAll("\\D", "")) :
                                            ETFConfigData.blinkLength;
                                    blinkFrequency = blinkingProps.containsKey("blinkFrequency") ?
                                            Integer.parseInt(blinkingProps.getProperty("blinkFrequency").replaceAll("\\D", "")) :
                                            ETFConfigData.blinkFrequency;

                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public boolean exists(){
        return isBuilt || MinecraftClient.getInstance().getResourceManager().getResource(thisIdentifier).isPresent();
    }

    private boolean isBuilt = false;

    public void buildTrimTexture( ArmorTrim trim, boolean leggings){
//        trim=minecraft:trims/models/armor/rib_gold
//        trim2=minecraft:trims/models/armor/rib_leggings_gold
        try {
            String mat = trim.getMaterial().value().assetName();
            String namespace = trim.getPattern().value().assetId().getNamespace();
            String pattern = trim.getPattern().value().assetId().getPath() + (leggings ? "_leggings" : "");

            NativeImage patternImg = ETFUtils2.getNativeImageElseNull(new Identifier(namespace, "textures/trims/models/armor/" + pattern + ".png"));

            NativeImage matImg = ETFUtils2.getNativeImageElseNull(new Identifier(namespace, "textures/trims/color_palettes/" + mat + ".png"));
            NativeImage palletteImg = ETFUtils2.getNativeImageElseNull(new Identifier(namespace, "textures/trims/color_palettes/trim_palette.png"));

            if (matImg != null && palletteImg != null && patternImg != null) {
                Int2IntOpenHashMap palletteMap = new Int2IntOpenHashMap();
                for (int i = 0; i < palletteImg.getWidth(); i++) {
                    for (int j = 0; j < palletteImg.getHeight(); j++) {
                        palletteMap.put(palletteImg.getColor(i,j),matImg.getColor(i,j));
                    }
                }
                try (NativeImage newImage = ETFUtils2.emptyNativeImage(patternImg.getWidth(),patternImg.getHeight())){
                    for (int i = 0; i < patternImg.getWidth(); i++) {
                        for (int j = 0; j < patternImg.getHeight(); j++) {
                            int colour = patternImg.getColor(i,j);
                            if(palletteMap.containsKey(colour)) {
                                newImage.setColor(i, j, palletteMap.get(colour));
                            }else{
                                newImage.setColor(i, j, colour);
                            }
                        }
                    }
                    ETFUtils2.registerNativeImageToIdentifier(newImage,thisIdentifier);
                }catch (Exception b){
                    // make empty
                    thisIdentifier_Patched = ETFManager.getErrorETFTexture().thisIdentifier;
                }
            }else{
                // make empty
                thisIdentifier_Patched = ETFManager.getErrorETFTexture().thisIdentifier;
            }
        }catch (Exception e){
            // make empty
            thisIdentifier_Patched = ETFManager.getErrorETFTexture().thisIdentifier;

        }
        isBuilt = true;
    }

    private void setupEmissives() {

        if (ETFConfigData.enableEmissiveTextures) {
            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();

            for (String possibleEmissiveSuffix :
                    ETFManager.getInstance().EMISSIVE_SUFFIX_LIST) {
                Optional<Resource> vanillaR1 = resourceManager.getResource(thisIdentifier);
                if(vanillaR1.isEmpty() && thisIdentifier.getPath().contains("textures/trims/models/armor/")){
                    //create this armor trim as an identifier because fuck Sprites, all my homies hate Sprites

                    //try get an armor trims base texture just to match what texture pack level it is
                    ResourcePack pack;
                    vanillaR1 = resourceManager.getResource(new Identifier(thisIdentifier.getNamespace(),thisIdentifier.getPath().replaceAll("_(.*?)(?=\\.png)","")));
                    if(vanillaR1.isPresent()){
                        pack = vanillaR1.get().getPack();
                    }else{
                        pack = MinecraftClient.getInstance().getDefaultResourcePack();
                    }
                    //create resource object sufficient for following code
                    vanillaR1 = Optional.of(new Resource(pack, null));
                }
                if (vanillaR1.isPresent()) {
                    Identifier possibleEmissiveIdentifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", possibleEmissiveSuffix + ".png");
                    Optional<Resource> emissiveR1 = resourceManager.getResource(possibleEmissiveIdentifier);
                    if (emissiveR1.isPresent()) {

                        String emissivePackName = emissiveR1.get().getResourcePackName();
//                        ObjectSet<String> packs = new ObjectOpenHashSet<>();
//                        packs.add(emissivePackName);
//                        packs.add(vanillaR1.get().getResourcePackName());
                        if (emissivePackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{emissivePackName, vanillaR1.get().getResourcePackName()}))) {
                            //is higher or same pack
                            emissiveIdentifier = possibleEmissiveIdentifier;
                            Identifier possibleEmissiveBlinkIdentifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink" + possibleEmissiveSuffix + ".png");
                            Optional<Resource> emissiveBlinkR1 = resourceManager.getResource(possibleEmissiveBlinkIdentifier);
                            if (emissiveBlinkR1.isPresent()) {

                                String emissiveBlinkPackName = emissiveBlinkR1.get().getResourcePackName();
                                //packs.clear();
                                //packs.add(emissiveBlinkPackName);
                                //packs.add(vanillaR1.get().getResourcePackName());
                                if (emissiveBlinkPackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{emissiveBlinkPackName, vanillaR1.get().getResourcePackName()}))) {
                                    //is higher or same pack
                                    emissiveBlinkIdentifier = possibleEmissiveBlinkIdentifier;
                                    Identifier possibleEmissiveBlink2Identifier = ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_blink2" + possibleEmissiveSuffix + ".png");
                                    Optional<Resource> emissiveBlink2R1 = resourceManager.getResource(possibleEmissiveBlink2Identifier);
                                    if (emissiveBlink2R1.isPresent()) {
                                        String emissiveBlink2PackName = emissiveBlink2R1.get().getResourcePackName();
                                        // packs.clear();
                                        // packs.add(emissiveBlink2PackName);
                                        // packs.add(vanillaR1.get().getResourcePackName());
                                        if (emissiveBlink2PackName.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{emissiveBlink2PackName, vanillaR1.get().getResourcePackName()}))) {
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
            if (isEmissive())
                createPatchedTextures();
        }
    }

    public String eSuffix = null;

    private void createPatchedTextures() {
        if (this.canPatch/* && ETFVersionDifferenceHandler.isFabric() && !ETFConfigData.removePixelsUnderEmissive*/) {
            return;
        }
        //here we will 'patch' the base texture to prevent z-fighting with various shaders

        //null depending on existence
        NativeImage newBaseTexture = ETFUtils2.getNativeImageElseNull(thisIdentifier);
        NativeImage newBlinkTexture = ETFUtils2.getNativeImageElseNull(blinkIdentifier);
        NativeImage newBlink2Texture = ETFUtils2.getNativeImageElseNull(blink2Identifier);

        boolean didPatch = false;
        ResourceManager files = MinecraftClient.getInstance().getResourceManager();
        //should this process cancel itself due to presence of PBR textures
        if (ETFConfigData.dontPatchPBRTextures &&
                (ETFVersionDifferenceHandler.isThisModLoaded("iris") || ETFVersionDifferenceHandler.isThisModLoaded("oculus")) &&
                //do pbr files exist?
                (files.getResource(ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_s.png")).isPresent() ||
                        files.getResource(ETFUtils2.replaceIdentifier(thisIdentifier, ".png", "_n.png")).isPresent())
        ) {
            //here the setting to cancel is enabled, iris is present, and pbr files exist, so cancel patching
            return;
        }
        //should patching cancel due to presence of animation mods and animated textures
        if (ETFConfigData.dontPatchAnimatedTextures && (
                (ETFVersionDifferenceHandler.isThisModLoaded("animatica")) && (
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


        //we need to move iris pbr textures, these are texture_n.png   and texture_s.png
        //todo this does not currently work with iris pbr as currently textureManager registered textures are not valid for pbr
//
//        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
//            Identifier identifier_n = new Identifier(thisIdentifier.toString().replace(".png", "_n.png"));
//            Optional<Resource> base_nResource = manager.getResource(identifier_n);
//            if (base_nResource.isPresent()) {
//                NativeImage ntexture = ETFUtils2.getNativeImageElseNull(identifier_n);
//                ETFUtils2.registerNativeImageToIdentifier(ntexture, new Identifier(PATCH_NAMESPACE_PREFIX + identifier_n.getNamespace(), identifier_n.getPath()));
//                //System.out.println("one, "+identifier_n.toString());
//            }
//            Identifier identifier_s = new Identifier(thisIdentifier.toString().replace(".png", "_s.png"));
//            Optional<Resource> base_sResource = manager.getResource(identifier_s);
//            if (base_sResource.isPresent()) {
//                NativeImage stexture = ETFUtils2.getNativeImageElseNull(identifier_s);
//                ETFUtils2.registerNativeImageToIdentifier(stexture, new Identifier(PATCH_NAMESPACE_PREFIX + identifier_s.getNamespace(), identifier_s.getPath()));
//                //System.out.println("two, "+identifier_s.toString());
//            }
//
//            if (doesBlink()) {
//                Identifier identifier_blink_n = new Identifier(blinkIdentifier.toString().replace(".png", "_n.png"));
//                Optional<Resource> blink_nResource = manager.getResource(identifier_blink_n);
//                if (blink_nResource.isPresent()) {
//                    NativeImage blinkn = ETFUtils2.getNativeImageElseNull(identifier_blink_n);
//                    ETFUtils2.registerNativeImageToIdentifier(blinkn, new Identifier(PATCH_NAMESPACE_PREFIX + identifier_blink_n.getNamespace(), identifier_blink_n.getPath()));
//
//                }
//                Identifier identifier_blink_s = new Identifier(blinkIdentifier.toString().replace(".png", "_s.png"));
//                Optional<Resource> blink_sResource = manager.getResource(identifier_blink_s);
//                if (blink_sResource.isPresent()) {
//                    NativeImage blinks = ETFUtils2.getNativeImageElseNull(identifier_blink_s);
//                    ETFUtils2.registerNativeImageToIdentifier(blinks, new Identifier(PATCH_NAMESPACE_PREFIX + identifier_blink_s.getNamespace(), identifier_blink_s.getPath()));
//
//                }
//                if (doesBlink2()) {
//                    Identifier identifier_blink2_n = new Identifier(blink2Identifier.toString().replace(".png", "_n.png"));
//                    Optional<Resource> blink2_nResource = manager.getResource(identifier_blink2_n);
//                    if (blink2_nResource.isPresent()) {
//                        NativeImage blink2n = ETFUtils2.getNativeImageElseNull(identifier_blink2_n);
//                        ETFUtils2.registerNativeImageToIdentifier(blink2n, new Identifier(PATCH_NAMESPACE_PREFIX + identifier_blink2_n.getNamespace(), identifier_blink2_n.getPath()));
//
//                    }
//                    Identifier identifier_blink2_s = new Identifier(blink2Identifier.toString().replace(".png", "_s.png"));
//                    Optional<Resource> blink2_sResource = manager.getResource(identifier_blink2_s);
//                    if (blink2_sResource.isPresent()) {
//                        NativeImage blink2s = ETFUtils2.getNativeImageElseNull(identifier_blink2_s);
//                        ETFUtils2.registerNativeImageToIdentifier(blink2s, new Identifier(PATCH_NAMESPACE_PREFIX + identifier_blink2_s.getNamespace(), identifier_blink2_s.getPath()));
//
//                    }
//                }
//            }


        //patch out emissive textures for shader z fighting fix
        if (this.emissiveIdentifier != null && ETFConfigData.enableEmissiveTextures) {
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
            } catch (Exception ignored) {
                //
            }

            //save successful patches after any iris or other future patching reasons
            if (didPatch) {
                thisIdentifier_Patched = new Identifier(PATCH_NAMESPACE_PREFIX + thisIdentifier.getNamespace(), thisIdentifier.getPath());
                ETFUtils2.registerNativeImageToIdentifier(newBaseTexture, thisIdentifier_Patched);
                if (doesBlink()) {
                    blinkIdentifier_Patched = new Identifier(PATCH_NAMESPACE_PREFIX + blinkIdentifier.getNamespace(), blinkIdentifier.getPath());
                    ETFUtils2.registerNativeImageToIdentifier(newBlinkTexture, blinkIdentifier_Patched);
                    if (doesBlink2()) {
                        blink2Identifier_Patched = new Identifier(PATCH_NAMESPACE_PREFIX + blink2Identifier.getNamespace(), blink2Identifier.getPath());
                        ETFUtils2.registerNativeImageToIdentifier(newBlink2Texture, blink2Identifier_Patched);
                    }
                }
            }
        }
    }

    //animatica registers the animated version of the texture as "texture.png-anim"
    private static boolean doesAnimaticaVersionExist(Identifier identifier){
        if(identifier == null) return false;
        String idString = identifier.toString();
        //check if its already an anim and animatica has already gotten to replacing it before etf sees it
        if(idString.endsWith("-anim")) return true;
        //check if animatica has registered its animated version of this texture
        return MinecraftClient.getInstance().getTextureManager().getOrDefault(new Identifier(idString+"-anim"),null) != null;
    }

    @NotNull
    Identifier getFeatureTexture(Identifier vanillaFeatureTexture) {

        if (FEATURE_TEXTURE_MAP.containsKey(vanillaFeatureTexture)) {
            return FEATURE_TEXTURE_MAP.get(vanillaFeatureTexture);
        }
        //otherwise we need to find what it is and add to map
        ETFDirectory directory = ETFDirectory.getDirectoryOf(thisIdentifier);
        if (variantNumber != 0) {
            Identifier possibleFeatureVariantIdentifier =
                    ETFDirectory.getIdentifierAsDirectory(
                            ETFUtils2.replaceIdentifier(
                                    vanillaFeatureTexture,
                                    ".png",
                                    variantNumber + ".png")
                            , directory);
            Optional<Resource> possibleResource = MinecraftClient.getInstance().getResourceManager().getResource(possibleFeatureVariantIdentifier);
            if (possibleResource.isPresent()) {
                //feature variant exists so return
                FEATURE_TEXTURE_MAP.put(vanillaFeatureTexture, possibleFeatureVariantIdentifier);
                return possibleFeatureVariantIdentifier;
            }
        }
        //System.out.println("feature="+vanillaFeatureTexture.toString()+thisIdentifier.toString()+directory.toString());
        //here we have no number and are likely vanilla texture or something went wrong in which case vanilla anyway
        //ETFUtils2.logWarn("getFeatureTexture() either vanilla or failed");
        ETFDirectory tryDirectory = ETFDirectory.getDirectoryOf(vanillaFeatureTexture);
        if (tryDirectory == directory || tryDirectory == ETFDirectory.VANILLA) {
            //if same directory as main texture or is vanilla texture use it
            Identifier tryDirectoryVariant = ETFDirectory.getIdentifierAsDirectory(vanillaFeatureTexture, tryDirectory);
            FEATURE_TEXTURE_MAP.put(vanillaFeatureTexture, tryDirectoryVariant);
            return tryDirectoryVariant;
        }
        //final fallback just use vanilla
        FEATURE_TEXTURE_MAP.put(vanillaFeatureTexture, vanillaFeatureTexture);
        return vanillaFeatureTexture;

    }

    @NotNull
    public Identifier getTextureIdentifier(ETFEntity entity) {
        return getTextureIdentifier(entity, false);
    }

    @NotNull
    public Identifier getTextureIdentifier(@Nullable ETFEntity entity, boolean forcePatchedTexture) {

        if (isPatched() && (forcePatchedTexture || (ETFConfigData.enableEmissiveTextures && ETFVersionDifferenceHandler.areShadersInUse()) || MinecraftClient.getInstance().currentScreen instanceof ETFConfigScreen)) {
            //patched required
            currentTextureState = TextureReturnState.NORMAL_PATCHED;
            return getBlinkingIdentifier(entity);
        }
        currentTextureState = TextureReturnState.NORMAL;
        //regular required
        return getBlinkingIdentifier(entity);
    }

    @NotNull
    private Identifier getBlinkingIdentifier(@Nullable ETFEntity entity) {
        if (!doesBlink() || entity == null || !ETFConfigData.enableBlinking) {
            return identifierOfCurrentState();
        }

        //force eyes closed if asleep
        if (entity.getPose() == EntityPose.SLEEPING) {
            modifyTextureState(TextureReturnState.APPLY_BLINK);
            return identifierOfCurrentState();
        }
        //force eyes closed if blinded
        else if (entity.entity() instanceof LivingEntity alive && alive.hasStatusEffect(StatusEffects.BLINDNESS)) {
            modifyTextureState(doesBlink2() ? TextureReturnState.APPLY_BLINK2 : TextureReturnState.APPLY_BLINK);
            return identifierOfCurrentState();
        } else {
            //do regular blinking
            if (entity.getWorld() != null) {
                UUID id = entity.getUuid();
                if (!ETFManager.getInstance().ENTITY_BLINK_TIME.containsKey(id)) {
                    ETFManager.getInstance().ENTITY_BLINK_TIME.put(id, entity.getWorld().getTime() + blinkLength + 1);
                    return identifierOfCurrentState();
                }
                long nextBlink = ETFManager.getInstance().ENTITY_BLINK_TIME.getLong(id);
                long currentTime = entity.getWorld().getTime();

                if (currentTime >= nextBlink - blinkLength && currentTime <= nextBlink + blinkLength) {
                    if (doesBlink2()) {
                        if (currentTime >= nextBlink - (blinkLength / 3) && currentTime <= nextBlink + (blinkLength / 3)) {
                            modifyTextureState(TextureReturnState.APPLY_BLINK);
                            return identifierOfCurrentState();
                        }
                        modifyTextureState(TextureReturnState.APPLY_BLINK2);
                        return identifierOfCurrentState();
                    } else if (!(currentTime > nextBlink)) {
                        modifyTextureState(TextureReturnState.APPLY_BLINK);
                        return identifierOfCurrentState();
                    }
                } else if (currentTime > nextBlink + blinkLength) {
                    //calculate new next blink
                    ETFManager.getInstance().ENTITY_BLINK_TIME.put(id, currentTime + randomBlink.nextInt(blinkFrequency) + 20);
                }
            }
        }
        return identifierOfCurrentState();
    }

    public boolean isEmissive() {
        return this.emissiveIdentifier != null;
    }

    public boolean isPatched() {
        return this.thisIdentifier_Patched != null;
    }

    public boolean doesBlink() {
        return this.blinkIdentifier != null;
    }

    private ETFSprite atlasSprite = null;

    @NotNull
    public ETFSprite getSprite(@NotNull Sprite originalSprite,@NotNull ETFSprite.SpriteSource source){
        if (atlasSprite == null){
            atlasSprite = new ETFSprite(originalSprite, this,source);
        }
        return atlasSprite;
    }

    public boolean doesBlink2() {
        return this.blink2Identifier != null;
    }

    @Override
    public String toString() {
        return "ETFTexture{texture=" + this.thisIdentifier.toString() +/*", vanilla="+this.vanillaIdentifier.toString()+*/", emissive=" + isEmissive() + ", patched=" + isPatched() + "}";
    }

    public void renderEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, ModelPart modelPart) {
        renderEmissive(matrixStack, vertexConsumerProvider, modelPart, ETFManager.getEmissiveMode());
    }


    public void renderEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, ModelPart modelPart, ETFManager.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        VertexConsumer vertexC = getEmissiveVertexConsumer(vertexConsumerProvider, null, modeToUsePossiblyManuallyChosen);
        if (vertexC != null) {
            modelPart.render(matrixStack, vertexC, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV);
        }
    }

    public void renderEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Model model) {
        renderEmissive(matrixStack, vertexConsumerProvider, model, ETFManager.getEmissiveMode());
    }

    public void renderEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Model model, ETFManager.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        VertexConsumer vertexC = getEmissiveVertexConsumer(vertexConsumerProvider, model, modeToUsePossiblyManuallyChosen);
        if (vertexC != null) {
            model.render(matrixStack, vertexC, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
    }

    @Nullable
    public VertexConsumer getEmissiveVertexConsumer(VertexConsumerProvider vertexConsumerProvider, @Nullable Model model, ETFManager.EmissiveRenderModes modeToUsePossiblyManuallyChosen) {
        if (isEmissive()) {
            // block entity variants
            //removed in rework may return
//                if (irisDetected && ETFConfigData.fullBrightEmissives) {
//                    return vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true));
//                } else {
//                    return vertexConsumerProvider.getBuffer(RenderLayer.getItemEntityTranslucentCull(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString)));
//                }
            Identifier emissiveToUse = getEmissiveIdentifierOfCurrentState();

            if (emissiveToUse != null) {

                if (modeToUsePossiblyManuallyChosen == ETFManager.EmissiveRenderModes.BRIGHT) {
                    return vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissiveToUse, !ETFVersionDifferenceHandler.areShadersInUse()));
                } else {
                    if (model == null) {
                        return vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull /*RenderLayer.getEntityTranslucent*/(emissiveToUse));
                    } else {
                        return vertexConsumerProvider.getBuffer(model.getLayer(emissiveToUse));
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
            //shouldn't ever call but may need in future
//            case APPLY_PATCH -> currentTextureState= switch (currentTextureState){
//                    case BLINK ->  TextureReturnState.BLINK_PATCHED;
//                    case BLINK2 -> TextureReturnState.BLINK2_PATCHED;
//                    default -> TextureReturnState.NORMAL_PATCHED;
//                };
            //default -> {}
        }
    }

    @NotNull
    private Identifier identifierOfCurrentState() {
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
    public Identifier getEmissiveIdentifierOfCurrentState() {
        return switch (currentTextureState) {
            case NORMAL, NORMAL_PATCHED -> emissiveIdentifier;
            case BLINK, BLINK_PATCHED -> emissiveBlinkIdentifier;
            case BLINK2, BLINK2_PATCHED -> emissiveBlink2Identifier;
            default ->
                //ETFUtils.logError("identifierOfCurrentState failed, it should not have, returning default");
                    null;
        };
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
