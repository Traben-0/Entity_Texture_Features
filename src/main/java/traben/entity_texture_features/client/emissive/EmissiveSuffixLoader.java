package traben.entity_texture_features.client.emissive;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETFUtils;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class EmissiveSuffixLoader {
    public static final Identifier LOCATION = new Identifier("optifine/emissive.properties");

    private static final ResourceManager RESOURCE_MANAGER = MinecraftClient.getInstance().getResourceManager();

    private static String[] emissiveSuffixes = null;

    public static void load() {
        if(RESOURCE_MANAGER.containsResource(LOCATION)) {
            try (Resource resource = RESOURCE_MANAGER.getResource(LOCATION)) {
                InputStream propertiesInputStream = resource.getInputStream();
                Properties properties = new Properties();
                properties.load(propertiesInputStream);

                Set<String> builder = new HashSet<>();

                if (properties.contains("suffix.emissive")) {
                    builder.add(properties.getProperty("suffix.emissive"));
                }

                if (ETFConfigData.alwaysCheckVanillaEmissiveSuffix) {
                    builder.add("_e");
                }

                emissiveSuffixes = builder.toArray(new String[0]);
                if (emissiveSuffixes.length == 0) {
                    ETFUtils.modMessage("No emissive suffix specified, using default emissive suffix _e", false);
                    emissiveSuffixes = new String[]{"_e"};
                }
            } catch (Exception e) {
                ETFUtils.modWarn(String.format("Failed to load emissive.properties, using default emissive suffix _e : %s", e), false);

                emissiveSuffixes = new String[]{"_e"};
            }
        } else {
            emissiveSuffixes = new String[]{"_e"};
        }
    }

    @Nullable
    public static String[] getEmissiveSuffixes() {
        return emissiveSuffixes;
    }
}
