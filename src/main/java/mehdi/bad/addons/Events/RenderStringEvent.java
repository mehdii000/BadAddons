package mehdi.bad.addons.Events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderStringEvent extends Event {
    private final Minecraft mc = Minecraft.getMinecraft();
    private String string;
    private float x;
    private float y;
    private int color;

    public RenderStringEvent(String string, float x, float y, int color) {
        this.string = string;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getString() {
        return this.string;
    }

    public void setString(String string) {
        this.string = string;
    }

}