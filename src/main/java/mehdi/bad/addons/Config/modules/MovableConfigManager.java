package mehdi.bad.addons.Config.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

public class MovableConfigManager {

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File configFile = new File("config/BadAddons/modulesPositions.json");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs(); // Create parent directories if they don't exist
        }
        try (Writer writer = new FileWriter(configFile)) {
            HashMap<String, HashMap<String, Integer>> modulePosMap = new HashMap<>();
            // Assuming MovablesManager.modules is a list of MovableModule objects
            for (MovableModule module : MovablesManager.modules) {
                HashMap<String, Integer> posMap = new HashMap<>();
                posMap.put("x", module.getX());
                posMap.put("y", module.getY());
                posMap.put("width", module.getWidth());
                posMap.put("height", module.getHeight());
                modulePosMap.put(module.getName(), posMap);
            }
            gson.toJson(modulePosMap, writer);
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void load() {
        Gson gson = new Gson();
        File configFile = new File("config/BadAddons/modulesPositions.json");

        if (configFile.exists() && configFile.length() > 0) {
            try (Reader reader = new FileReader(configFile)) {
                Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {}.getType();
                HashMap<String, HashMap<String, Integer>> modulePosMap = gson.fromJson(reader, type);

                // Assuming MovablesManager.modules is mutable and can be updated with loaded data
                for (MovableModule module : MovablesManager.modules) {
                    if (modulePosMap.containsKey(module.getName())) {
                        HashMap<String, Integer> pos = modulePosMap.get(module.getName());
                        module.setPosition(pos.get("x"), pos.get("y")); // Assuming setPosition(int x, int y)
                        module.setDimensions(pos.get("width"), pos.get("height"));
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading configuration: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Configuration file not found or empty, loading defaults.");
        }
    }
}