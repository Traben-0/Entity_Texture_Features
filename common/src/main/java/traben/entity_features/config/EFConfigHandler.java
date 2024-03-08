package traben.entity_features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import traben.entity_features.EFCommon;
import traben.entity_features.config.gui.EFMainConfigScreen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import static traben.entity_texture_features.ETF.CONFIG_DIR;


public class EFConfigHandler<T extends EFConfig> {


    private final Supplier<T> newConfigSupplier;
    private final String configFileName;
    private final Class<T> configClass;
    private T CONFIG;

    private final String logID;

    public EFConfigHandler(Supplier<T> newConfigSupplier, String configFileName, String logID) {
        this.newConfigSupplier = newConfigSupplier;
        this.logID = logID;
        this.configFileName = configFileName.endsWith(".json") ? configFileName : configFileName + ".json";

        CONFIG = newConfigSupplier.get();
        //noinspection unchecked
        this.configClass = (Class<T>) CONFIG.getClass();
        this.loadFromFile();

        if (!(CONFIG instanceof EFConfig.NoGUI)) {
            EFMainConfigScreen.registerConfigHandler(this);
        }
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
        final EFConfigHandler<?> that = (EFConfigHandler<?>) o;
        return getConfig().getClass().equals(that.getConfig().getClass());
    }

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
        File config = new File(CONFIG_DIR, configFileName);
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(toJson());
            fileWriter.close();
        } catch (IOException e) {
            EFCommon.logError(logID,"Config file could not be saved: " + e.getMessage());
        }
    }

    public String toJson() {
        return toJson(CONFIG);
    }
    public String toJson(Object config) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(config);
    }

    public void loadFromFile() {
        T loaded;
        try {
            File config = new File(CONFIG_DIR, configFileName);
            if (config.exists()) {
                try {
                    FileReader fileReader = new FileReader(config);
                    loaded = (fromJson(fileReader));
                    fileReader.close();
                    saveToFile();
                } catch (IOException e) {
                    EFCommon.log(logID,"Config could not be loaded, using defaults");
                    loaded = newConfigSupplier.get();
                    saveToFile();
                    EFCommon.configHadLoadError = true;
                }
            } else {
                loaded = newConfigSupplier.get();
                saveToFile();
            }
            if (loaded == null) {
                EFCommon.log(logID,"Config was null, using defaults");
                loaded = newConfigSupplier.get();
                saveToFile();
                EFCommon.configHadLoadError = true;
            }
        } catch (Exception e) {
            EFCommon.logError(logID,"Config was corrupt or broken, using defaults");
            loaded = newConfigSupplier.get();
            saveToFile();
            EFCommon.configHadLoadError = true;
        }
        CONFIG = loaded;
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




}
