package mehdi.bad.addons.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class AuctionUtils {

    private static Map<String, Double> itemPrices = new HashMap<>();

    public static void initItemsPrices() {
        itemPrices.clear();
        String itemPricesUrl = "http://moulberry.codes/lowestbin.json";
        JsonElement jsonElement = getJson(itemPricesUrl);

        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String id = entry.getKey();
                double price = entry.getValue().getAsDouble();
                itemPrices.put(id, price);
            }
        } else {
            ChatLib.chat("§cFailed to fetch and parse item prices JSON.");
        }
    }

    public static String formatPrice(double value) {
        String result;
        if (value >= 1000000) {
            result = String.format("%.2fm", value / 1000000);
        } else if (value >= 1000) {
            result = String.format("%.1fk", value / 1000);
        } else {
            result = String.format("%.0f", value);
        }
        return result;
    }

    public static String getPlayerNameFromUUID(String uuid) {
        String itemPricesUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
        JsonElement jsonElement = getJson(itemPricesUrl);

        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String id = entry.getKey();
                if (id.equalsIgnoreCase("name")) return entry.getValue().getAsString();
            }
        } else {
            ChatLib.chat("§cFailed to fetch player uuid.");
        }
        return null;
    }

    public static double getPrice(String id, double defaultPrice) {
        return itemPrices.getOrDefault(id, defaultPrice);
    }

    public static JsonElement getJson(String jsonUrl) {
        try {
            URL url = new URL(jsonUrl);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Connection", "close");
            JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
            JsonElement element = new Gson().fromJson(reader, JsonElement.class);
            return element;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getTotalAuctionPages() {
        return getJson("https://api.hypixel.net/skyblock/auctions").getAsJsonObject().get("totalPages").getAsInt();
    }
}
