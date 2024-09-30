package mehdi.bad.addons.Features.General;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashMap;

public class WhitelistedBedsConfig {

    public static HashMap<String, String> whitelistedBeds = new HashMap<>();

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File configFile = new File("config/BadAddons/bedsWhitelist.json");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs(); // Create parent directories if they don't exist
        }
        try (Writer writer = new FileWriter(configFile)) {
            gson.toJson(whitelistedBeds, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        Gson gson = new Gson();

        File configFile = new File("config/BadAddons/bedsWhitelist.json");

        if (configFile.exists() && configFile.length() > 0) {
            try (Reader reader = new FileReader(configFile)) {
                whitelistedBeds = gson.fromJson(reader, HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}