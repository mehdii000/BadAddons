package mehdi.bad.addons.utils;

import java.awt.Color;

public class RainbowUtils {
	private static final int TICKS_PER_CYCLE = 100;
    private static final double TWO_PI = 2 * Math.PI;
    private static final double TIME_PERIOD = 5000.0; // Time period in milliseconds

    public static Color getRainbow() {
        long ticks = System.currentTimeMillis() / 10; // Assuming 100 ticks per second
        double time = ticks % TIME_PERIOD;
        double angle = (time / TIME_PERIOD) * TWO_PI;
        float hue = (float) (Math.sin(angle) + 1) / 2; // Map [-1, 1] to [0, 1]
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
}