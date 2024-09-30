package mehdi.bad.addons.Features.Dungeons;

import java.awt.*;

public class Room {
    public int x;
    public int z;
    public String name;
    public String type;
    public int[] ids;
    public int secrets;
    public String checkmark;
    public int[] size;
    public boolean explored;

    public String fullId;

    public Room(int x, int z, Data data) {
        this.x = x;
        this.z = z;
        this.secrets = data.secrets;
        this.name = data.name;
        this.type = data.type;
        this.ids = data.ids;
        this.size = new int[]{3, 3};
        this.checkmark = "";
        this.explored = true;
        this.fullId = getFullId();
    }

    private String getFullId() {
        return ids[0]+","+ids[1];
    }

    public Color getColor() {
        switch (this.type) {
            case "puzzle":
                return new Color(117, 0, 133, 255);
            case "blood":
                return new Color(255, 0, 0, 255);
            case "trap":
                return new Color(216, 127, 51, 255);
            case "yellow":
                return new Color(254, 223, 0, 255);
            case "fairy":
                return new Color(224, 0, 255, 255);
            case "entrance":
                return new Color(20, 133, 0, 255);
            case "rare":
            default:
                return new Color(107, 58, 17, 255);
        }
    }

}