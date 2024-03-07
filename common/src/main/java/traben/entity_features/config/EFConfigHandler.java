package traben.entity_features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import traben.entity_features.EFCommon;
import traben.entity_features.config.gui.EFMainConfigScreen;
import traben.entity_texture_features.ETFClientCommon;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import static traben.entity_texture_features.ETFClientCommon.CONFIG_DIR;

public class EFConfigHandler<T extends EFConfig> {


    private final Supplier<T> newConfigSupplier;

    public T getConfig() {
        if(CONFIG == null){
            loadFromFile();
        }
        return CONFIG;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EFConfigHandler<?> that = (EFConfigHandler<?>) o;
        return getConfig().getClass().equals(that.getConfig().getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(newConfigSupplier, CONFIG, configFileName, configClass);
    }

    public void setConfig(final T CONFIG) {
        this.CONFIG = CONFIG;
    }

    private T CONFIG;
    private final String configFileName;

    private final Class<T> configClass;

    public EFConfigHandler(Supplier<T> newConfigSupplier, String configFileName){
        this.newConfigSupplier = newConfigSupplier;
        this.configFileName = configFileName.endsWith(".json") ? configFileName : configFileName+".json";

        CONFIG = newConfigSupplier.get();
        //noinspection unchecked
        this.configClass = (Class<T>) CONFIG.getClass();
        this.loadFromFile();

        if(!(CONFIG instanceof EFConfig.NoGUI)){
            EFMainConfigScreen.registerConfigHandler(this);
        }
    }

    public void saveToFile(){
        File config = new File(CONFIG_DIR, configFileName);
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(CONFIG.toJson());
            fileWriter.close();
        } catch (IOException e) {
            EFCommon.logError("Config file could not be saved: "+e.getMessage());
        }
    }

    public void loadFromFile(){
        T loaded;
        try {
            File config = new File(ETFClientCommon.CONFIG_DIR, configFileName);
            if (config.exists()) {
                try {
                    FileReader fileReader = new FileReader(config);
                    loaded = (fromJson(fileReader));
                    fileReader.close();
                    saveToFile();
                } catch (IOException e) {
                    EFCommon.log("Config could not be loaded, using defaults");
                    loaded = newConfigSupplier.get();
                    saveToFile();
                    EFCommon.configHadLoadError = true;
                }
            } else {
                loaded = newConfigSupplier.get();
                saveToFile();
            }
            if (loaded == null) {
                EFCommon.log("Config was null, using defaults");
                loaded = newConfigSupplier.get();
                saveToFile();
                EFCommon.configHadLoadError = true;
            }
        } catch (Exception e) {
            EFCommon.logError("Config was corrupt or broken, using defaults");
            loaded = newConfigSupplier.get();
            saveToFile();
            EFCommon.configHadLoadError = true;
        }
        CONFIG = loaded;
    }

    public T fromJson(String json){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, configClass);
    }

    public T fromJson(FileReader json){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, configClass);
    }

    public T copyOfConfig() {
        return fromJson(CONFIG.toJson());
    }



}
