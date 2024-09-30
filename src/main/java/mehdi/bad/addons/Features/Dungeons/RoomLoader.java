package mehdi.bad.addons.Features.Dungeons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mehdi.bad.addons.BadAddons;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RoomLoader {
    public static ArrayList<Data> maps = new ArrayList<>();

    public RoomLoader() {
        load();
    }

    public static void load() {
        try {
            ResourceLocation rooms = new ResourceLocation(BadAddons.MODID + ":dungeons/legalrooms.json");
            InputStream input = BadAddons.mc.getResourceManager().getResource(rooms).getInputStream();
            JsonParser parser = new JsonParser();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            JsonArray jsonArray = parser.parse(reader).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                Data data = new Data(); // Create a new Data object for each room
                JsonObject roomObject = element.getAsJsonObject();

                // "name" and "type" fields remain the same
                data.name = roomObject.get("name").getAsString();
                data.type = roomObject.get("type").getAsString();

                // "secrets" field remains the same
                data.secrets = roomObject.get("secrets").getAsInt();

                // Extract "id" array as you have two values in it
                JsonArray idArray = roomObject.getAsJsonArray("id");
                for (JsonElement idElement : idArray) {
                    String[] idParts = idElement.getAsString().split(",");
                    int x = Integer.parseInt(idParts[0]);
                    int y = Integer.parseInt(idParts[1]);
                    data.addId(x, y);
                }

                //System.out.println("Loaded Room: " + data.name + " with " + data.secrets + " /id:" + data.ids[0] + "," + data.ids[1]);
                maps.add(data);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}