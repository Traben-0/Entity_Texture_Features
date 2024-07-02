package traben.tconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import traben.entity_texture_features.ETFVersionDifferenceManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;




public class TConfigHandler<T extends TConfig> {


    private final Supplier<T> newConfigSupplier;
    private final String configFileName;
    private final Class<T> configClass;
    private final String logID;
    private T CONFIG;

    public TConfigHandler(Supplier<T> newConfigSupplier, String configFileName, String logID) {
        this.newConfigSupplier = newConfigSupplier;
        this.logID = logID;
        this.configFileName = configFileName.endsWith(".json") ? configFileName : configFileName + ".json";
        //noinspection unchecked
        this.configClass = Objects.requireNonNull((Class<T>) newConfigSupplier.get().getClass());
        //needs class set above
        this.loadFromFile();

    }

    public T getConfig() {
        if (CONFIG == null) {
            loadFromFile();
        }
        return CONFIG;
    }

    public void setConfig(final T CONFIG) {
        this.CONFIG = CONFIG;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TConfigHandler<?> that = (TConfigHandler<?>) o;
        return getConfig().getClass().equals(that.getConfig().getClass());
    }

    @SuppressWarnings("unused")
    public boolean configEquals(final Object that) {
        if (CONFIG == that) return true;
        if (that == null || CONFIG.getClass() != that.getClass()) return false;
        return toJson().equals(toJson(that));
    }

    @Override
    public int hashCode() {
        return Objects.hash(newConfigSupplier, CONFIG, configFileName, configClass);
    }

    public void saveToFile() {
        if (ETFVersionDifferenceManager.getConfigDirectory() == null) return;

        File config = new File(ETFVersionDifferenceManager.getConfigDirectory().toFile(), configFileName);
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(toJson());
            fileWriter.close();
        } catch (IOException e) {
            TConfigLog.logError(logID, "Config file could not be saved: " + e.getMessage());
        }
    }

    public String toJson() {
        return toJson(CONFIG);
    }

    public String toJson(Object config) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(config);
    }

    public void loadFromFile() {
        if (ETFVersionDifferenceManager.getConfigDirectory() == null) {
            CONFIG = newConfigSupplier.get();
        }
        try {
            File config = new File(ETFVersionDifferenceManager.getConfigDirectory().toFile(), configFileName);
            if (config.exists()) {
                try {
                    FileReader fileReader = new FileReader(config);
                    CONFIG = fromJson(fileReader);
                    fileReader.close();
                    saveToFile();
                } catch (IOException e) {
                    TConfigLog.log(logID, "Config could not be loaded, using defaults");
                    CONFIG = newConfigSupplier.get();
                    saveToFile();
//                    EFCommon.configHadLoadError = true;
                }
            } else {
                CONFIG = newConfigSupplier.get();
                saveToFile();
            }
            if (CONFIG == null) {
                TConfigLog.log(logID, "Config was null, using defaults");
                CONFIG = newConfigSupplier.get();
                saveToFile();
//                EFCommon.configHadLoadError = true;
            }
        } catch (Exception e) {
            TConfigLog.logError(logID, "Config was corrupt or broken, using defaults");
            e.printStackTrace();
            CONFIG = newConfigSupplier.get();
            saveToFile();
//            EFCommon.configHadLoadError = true;
        }
    }

    public T fromJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, configClass);
    }

    public T fromJson(FileReader json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, configClass);
    }

    public T copyOfConfig() {
        return fromJson(toJson());
    }

    public boolean doesGUI() {
        return getConfig().doesGUI();
    }

}
