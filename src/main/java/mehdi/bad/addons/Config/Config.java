package mehdi.bad.addons.Config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Setting.*;

import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Config {

    private static final String fileName = "config/BadAddons/BadAddons.cfg";

    public static void save() {
        try {
            HashMap var0 = new HashMap();
            Iterator var1 = BadAddons.settings.iterator();

            while (var1.hasNext()) {
                Setting var2 = (Setting) var1.next();
                if (!(var2 instanceof FolderSetting)) {
                    var0.put(var2.name, var2.get(Object.class));
                }
            }

            String var4 = (new Gson()).toJson(var0);
            Files.write(Paths.get("config/BadAddons/BadAddons.cfg"), var4.getBytes(StandardCharsets.UTF_8));
        } catch (Exception var3) {
            System.out.println("Error while saving config file");
            var3.printStackTrace();
        }

    }

    public static ArrayList collect(Class var0) {
        Field[] declaredFields = var0.getDeclaredFields();
        ArrayList arrayList = new ArrayList();
        Field[] fields = declaredFields;
        int var4 = declaredFields.length;

        for (int i = 0; i < var4; ++i) {
            Field field = fields[i];
            Property property = field.getAnnotation(Property.class);
            if (property != null) {
                switch (property.type()) {
                    case BOOLEAN:
                        arrayList.add(new BooleanSetting(property, field, property.type()));
                        break;
                    case COLOR:
                        arrayList.add(new ColorSetting(property, field));
                        break;
                    case NUMBER:
                        arrayList.add(new NumberSetting(property, field));
                        break;
                    case SELECT:
                        arrayList.add(new SelectSetting(property, field));
                        break;
                    case FOLDER:
                        arrayList.add(new FolderSetting(property, field));
                        break;
                    case TEXT:
                        arrayList.add(new TextSetting(property, field));
                }
            }
        }

        Iterator arrayIterator = arrayList.iterator();

        while (arrayIterator.hasNext()) {
            Setting var10 = (Setting) arrayIterator.next();
            if (!var10.annotation.parent().equals("")) {
                var10.parent = getSetting(var10.annotation.parent(), arrayList);
                if (var10.parent != null) {
                    var10.parent.sons.add(var10);
                }
            }
        }

        ArrayList var9 = new ArrayList();
        Iterator var11 = arrayList.iterator();

        while (var11.hasNext()) {
            Setting var12 = (Setting) var11.next();
            if (var12.parent == null) {
                dfs(var9, var12);
            }
        }

        return var9;
    }

    public static void load() {
        try {
            File var0 = new File("config/BadAddons/BadAddons.cfg");
            if (var0.exists()) {
                BufferedReader var1 = Files.newBufferedReader(Paths.get("config/BadAddons/BadAddons.cfg"));
                Set<Map.Entry<String, JsonElement>> set = new JsonParser().parse(var1).getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : set) {
                    Setting setting = Config.getSetting(entry.getKey(), BadAddons.settings);
                    if (setting != null) {
                        if (setting instanceof NumberSetting || setting instanceof SelectSetting) {
                            setting.set(entry.getValue().getAsInt());
                        } else if (setting instanceof BooleanSetting) {
                            setting.set(entry.getValue().getAsBoolean()); // HOW DID U CODE LIKE THAT :<
                        } else {
                            setting.set(entry.getValue().getAsString());
                        }
                    }
                }
            }
        } catch (Exception var7) {
            System.out.println("Error while loading config file");
            var7.printStackTrace();
        }

    }

    public static Setting getSetting(String var0, ArrayList var1) {
        Iterator var2 = var1.iterator();

        Setting var3;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            var3 = (Setting) var2.next();
        } while (!var3.name.equals(var0));

        return var3;
    }

    private static void dfs(ArrayList var0, Setting var1) {
        var0.add(var1);
        Iterator var2 = var1.sons.iterator();

        Setting var3;
        while (var2.hasNext()) {
            var3 = (Setting) var2.next();
            if (!(var3 instanceof FolderSetting)) {
                dfs(var0, var3);
            }
        }

        var2 = var1.sons.iterator();

        while (var2.hasNext()) {
            var3 = (Setting) var2.next();
            if (var3 instanceof FolderSetting) {
                dfs(var0, var3);
            }
        }

    }
}
