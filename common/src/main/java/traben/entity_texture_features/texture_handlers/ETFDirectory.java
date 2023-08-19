package traben.entity_texture_features.texture_handlers;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Optional;

public enum ETFDirectory {
    DOES_NOT_EXIST(null),
    ETF(new String[]{"textures", "etf/random"}),
    OLD_OPTIFINE(new String[]{"textures/entity", "optifine/mob"}),
    OPTIFINE(new String[]{"textures", "optifine/random"}),
    VANILLA(null);

    @SuppressWarnings("StaticCollection")
    private static Object2ReferenceOpenHashMap<@NotNull Identifier, @NotNull ETFDirectory> ETF_DIRECTORY_CACHE = null;// = new Object2ReferenceOpenHashMap<>();
    private final String[] replaceStrings;

    ETFDirectory(String[] replaceStrings) {
        this.replaceStrings = replaceStrings;
    }

    public static void resetCache() {
        ETF_DIRECTORY_CACHE = new Object2ReferenceOpenHashMap<>();
    }

    public static Object2ReferenceOpenHashMap<@NotNull Identifier, @NotNull ETFDirectory> getCache() {
        if (ETF_DIRECTORY_CACHE == null) {
            ETF_DIRECTORY_CACHE = new Object2ReferenceOpenHashMap<>();
        }
        return ETF_DIRECTORY_CACHE;
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    @Nullable
    public static Identifier getDirectoryVersionOf(Identifier vanillaIdentifier) {
        ETFDirectory directory = getDirectoryOf(vanillaIdentifier);
        return switch (directory) {
            case DOES_NOT_EXIST -> null;
            case VANILLA -> vanillaIdentifier;
            default ->
                    getIdentifierAsDirectory(vanillaIdentifier,directory);
                    //ETFUtils2.replaceIdentifier(vanillaIdentifier, directory.replaceStrings[0], directory.replaceStrings[1]);
        };
    }

    @NotNull
    public static ETFDirectory getDirectoryOf(Identifier vanillaIdentifier) {
        Object2ReferenceOpenHashMap<@NotNull Identifier, @NotNull ETFDirectory> cache = getCache();
        if (!cache.containsKey(vanillaIdentifier)) {
            cache.put(vanillaIdentifier, findDirectoryOf(vanillaIdentifier));
        }
        return cache.get(vanillaIdentifier);
    }

    @NotNull
    private static ETFDirectory findDirectoryOf(Identifier vanillaIdentifier) {
        //check already directory'd textures
        if (vanillaIdentifier.getPath().contains("etf/random/entity")) {
            //Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(vanillaIdentifier);
            if (ETFUtils2.isExistingResource(vanillaIdentifier)) {
                return ETF;
            }
        } else if (vanillaIdentifier.getPath().contains("optifine/random/entity")) {
            //Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(vanillaIdentifier);
            if (ETFUtils2.isExistingResource(vanillaIdentifier)) {
                return OPTIFINE;
            }
        } else if (vanillaIdentifier.getPath().contains("optifine/mob")) {
            //Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(vanillaIdentifier);
            if (ETFUtils2.isExistingResource(vanillaIdentifier)) {
                return OLD_OPTIFINE;
            }
        }

        //it is not cached and does not need to be
        //may either be properties or image
        ObjectArrayList<ETFDirectory> foundDirectories = new ObjectArrayList<>();
        ResourceManager resources = MinecraftClient.getInstance().getResourceManager();

        if (ETFUtils2.isExistingResource(getIdentifierAsDirectory(vanillaIdentifier, VANILLA)))
            foundDirectories.add(VANILLA);
        if (ETFUtils2.isExistingResource(getIdentifierAsDirectory(vanillaIdentifier, OLD_OPTIFINE)))
            foundDirectories.add(OLD_OPTIFINE);
        if (ETFUtils2.isExistingResource(getIdentifierAsDirectory(vanillaIdentifier, OPTIFINE)))
            foundDirectories.add(OPTIFINE);
        if (ETFUtils2.isExistingResource(getIdentifierAsDirectory(vanillaIdentifier, ETF)))
            foundDirectories.add(ETF);

        //these are here as these will be 90%+ cases and will be faster
        if (foundDirectories.isEmpty()) {
            return DOES_NOT_EXIST;
        } else if (foundDirectories.size() == 1) {
            return foundDirectories.get(0);
        } else {
            //must be multiple
            //find the one in the highest resource-pack
            Object2ReferenceOpenHashMap<String, ETFDirectory> resourcePackNames = new Object2ReferenceOpenHashMap<>();

            for (ETFDirectory directory :
                    foundDirectories) {
                //map result already has internal 0123 order of pack directories ironed out only need to check pack order
                try {
                    if (ETFUtils2.isExistingResource(getIdentifierAsDirectory(vanillaIdentifier, directory))) {
                        resourcePackNames.put(resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, directory)).getResourcePackName(), directory);
                    }
                }catch(Exception ignored){}
            }

            String[] strArray = resourcePackNames.keySet().toArray(new String[0]);
            String returnedPack = ETFUtils2.returnNameOfHighestPackFromTheseMultiple(strArray);
            if (returnedPack != null) {
                return resourcePackNames.get(returnedPack);
            } else {
                //should exist
                return VANILLA;
            }
        }
    }

    @NotNull
    public static Identifier getIdentifierAsDirectory(Identifier identifier, ETFDirectory directory) {
        if (directory.doesReplace()) {
            return new Identifier(identifier.getNamespace(), identifier.getPath().replace(directory.replaceStrings[0], directory.replaceStrings[1]));
        } else {
            return identifier;
        }
    }

    public boolean doesReplace() {
        return this.replaceStrings != null;
    }


}
