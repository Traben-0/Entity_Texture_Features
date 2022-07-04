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

    private static final Object2ReferenceOpenHashMap<@NotNull Identifier, @NotNull ETFDirectory> ETF_DIRECTORY_CACHE = new Object2ReferenceOpenHashMap<>();
    private final String[] replaceStrings;

    ETFDirectory(String[] replaceStrings) {
        this.replaceStrings = replaceStrings;
    }

    public static void clear() {
        ETF_DIRECTORY_CACHE.clear();
    }

    @Nullable
    public static Identifier getDirectoryVersionOf(Identifier vanillaIdentifier) {
        ETFDirectory directory = getDirectoryOf(vanillaIdentifier);
        return switch (directory) {
            case DOES_NOT_EXIST -> null;
            case VANILLA -> vanillaIdentifier;
            default ->
                    ETFUtils2.replaceIdentifier(vanillaIdentifier, directory.replaceStrings[0], directory.replaceStrings[1]);
        };
    }

    @NotNull
    public static ETFDirectory getDirectoryOf(Identifier vanillaIdentifier) {
        if (!ETF_DIRECTORY_CACHE.containsKey(vanillaIdentifier)) {
            ETF_DIRECTORY_CACHE.put(vanillaIdentifier, findDirectoryOf(vanillaIdentifier));
        }
        return ETF_DIRECTORY_CACHE.get(vanillaIdentifier);
    }

    @NotNull
    private static ETFDirectory findDirectoryOf(Identifier vanillaIdentifier) {
        //it is not cached and does not need to be
        //may either be properties or image
        ObjectArrayList<ETFDirectory> foundDirectories = new ObjectArrayList<>();
        ResourceManager resources = MinecraftClient.getInstance().getResourceManager();

        if (resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, VANILLA)).isPresent())
            foundDirectories.add(VANILLA);
        if (resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, OLD_OPTIFINE)).isPresent())
            foundDirectories.add(OLD_OPTIFINE);
        if (resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, OPTIFINE)).isPresent())
            foundDirectories.add(OPTIFINE);
        if (resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, ETF)).isPresent())
            foundDirectories.add(ETF);

        //these are here as these will be 90%+ cases and will be faster
        if (foundDirectories.size() == 0) {
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
                Optional<Resource> resource = resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, directory));
                resource.ifPresent(value -> resourcePackNames.put(value.getResourcePackName(), directory));
            }

            String returnedPack = ETFUtils2.returnNameOfHighestPackFrom(resourcePackNames.keySet());
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
