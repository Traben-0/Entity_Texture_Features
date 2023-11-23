package traben.entity_texture_features.features.property_reading;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.texture_handlers.ETFDirectory;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.UUID;

public class TrueRandomProvider implements ETFApi.ETFVariantSuffixProvider {


    private final int[] suffixes;
    private final String packname;

    private TrueRandomProvider(String secondPack, int suffixes) {
        this.suffixes = new int[suffixes];
        for (int i = 0; i < suffixes; i++) {
            this.suffixes[i] = i + 1;
        }
        this.packname = secondPack;
    }

    @Nullable
    public static TrueRandomProvider of(Identifier vanillaIdentifier) {


        ResourceManager resources = MinecraftClient.getInstance().getResourceManager();

        Identifier second = ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, 2));
        if (second != null) {
            String secondPack = resources.getResource(second).map(Resource::getResourcePackName).orElse(null);
            String vanillaPack = resources.getResource(vanillaIdentifier).map(Resource::getResourcePackName).orElse(null);

            if (secondPack != null
                    && secondPack.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(new String[]{secondPack, vanillaPack}))) {
                int totalTextureCount = 2;
                while (ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, totalTextureCount + 1))
                        != null) {
                    totalTextureCount++;
                }
                return new TrueRandomProvider(secondPack, totalTextureCount);
            }
        }
        return null;
    }

    public String getPackName() {
        return packname;
    }

    @SuppressWarnings("unused")
    @Override
    public IntOpenHashSet getAllSuffixes() {
        return new IntOpenHashSet(suffixes);
    }

    @Override
    public int size() {
        return 1;
    }

    @SuppressWarnings("unused")
    @Override
    public int getSuffixForEntity(Entity entityToBeTested, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
        return getSuffixForETFEntity((ETFEntity) entityToBeTested, isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain);

    }

    @SuppressWarnings("unused")
    @Override
    public int getSuffixForBlockEntity(BlockEntity entityToBeTested, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
        return getSuffixForETFEntity((ETFEntity) entityToBeTested, isThisTheFirstTestForEntity, cacheToMarkEntitiesWhoseVariantCanChangeAgain);
    }

    @Override
    public int getSuffixForETFEntity(ETFEntity entityToBeTested, boolean isThisTheFirstTestForEntity, Object2BooleanOpenHashMap<UUID> cacheToMarkEntitiesWhoseVariantCanChangeAgain) {
        if (entityToBeTested == null) return 0;
        int randomSeededByUUID = Math.abs(entityToBeTested.etf$getUuid().hashCode());
        return suffixes[randomSeededByUUID % suffixes.length];
    }


}
