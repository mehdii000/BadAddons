package mehdi.bad.addons.Features.Dungeons;

import java.awt.*;

public class Door {

    public boolean normallyVisible;

    public String type;

    public int x;

    public boolean explored;

    public int z;

    public Door(int var1, int var2) {
        this.x = var1;
        this.z = var2;
        this.type = "normal";
        this.explored = true;
        this.normallyVisible = true;
    }

    public Color getColor() {
        switch (this.type) {
            case "wither":
                return new Color(19, 19, 19, 255);
            case "blood":
                return new Color(231, 0, 0, 255);
            case "entrance":
                return new Color(20, 133, 0, 255);
            default:
                return new Color(92, 52, 14, 255);
        }
    }

}
