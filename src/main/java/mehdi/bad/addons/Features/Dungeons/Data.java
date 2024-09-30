package mehdi.bad.addons.Features.Dungeons;

import java.util.HashMap;

public class Data {
    public static Data blankRoom = new Data("Unknown", "normal", 0, new int[]{0, 0});
    private static HashMap<String, Data> dataMap = new HashMap<>();

    public String name;
    public String type;
    public int secrets;
    public int[] ids = {0, 0};

    public Data() {
    }

    public Data(String name, String type, int secrets, int[] ids) {
        this.name = name;
        this.type = type;
        this.secrets = secrets;
    }

    public void addId(int x, int y) {
        ids[0] = x;
        ids[1] = y;
        dataMap.put(x + "," + y, this);
    }

    public static Data getDataFromId(int x, int y) {
        return dataMap.get(x + "," + y);
    }

    public static Data getDataFromDecodedString(String inputString) {
        String[] parts = inputString.split(" ");
        if (parts.length >= 2) {
            try {
                String s = parts[2];
                int x = Integer.parseInt(s.split(",")[0]);
                int y = Integer.parseInt(s.split(",")[1]);
                return getDataFromId(x, y);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

}