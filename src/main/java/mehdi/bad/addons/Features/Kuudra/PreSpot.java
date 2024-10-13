package mehdi.bad.addons.Features.Kuudra;

import net.minecraft.util.Vec3;

public class PreSpot {
    public static final PreSpot NOSPOT = null;
    private String name;
    private Vec3 initialPos;
    public PreSpot(String name, Vec3 initialPos) {
        this.name = name;
        this.initialPos = initialPos;
    }

    public String getName() {
        return name;
    }

    public Vec3 getInitialPos() {
        return initialPos;
    }
}