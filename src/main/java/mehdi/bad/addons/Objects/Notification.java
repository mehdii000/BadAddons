package mehdi.bad.addons.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class Notification {

    private final String title;
    private final String description;
    private final long endTime;
    private int smoothAnimation;
    private boolean visible = true;
    private static final int WIDTH = 250;
    private static final int HEIGHT = 50;
    private static final int MARGIN = 10;
    private static final int FADE_DURATION = 1000; // 1 second fade
    private static final int REMOVE_DURATION = 500; // 0.5 seconds for removal

    public Notification(String title, String description, long endTime) {
        this.title = title;
        this.description = description;
        this.endTime = endTime;
    }

    public void draw(RenderGameOverlayEvent.Post event, int index) {
        if (!visible) return;

        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();
        int notificationY = smoothAnimation + MARGIN * index; // Start from the top

        drawBackground(width / 2 - WIDTH / 2, notificationY, WIDTH, HEIGHT);
        drawCenteredString(title, width / 2, notificationY + 15);
        drawCenteredString(description, width / 2, notificationY + 45);

        updateAnimation(index);
    }

    private void drawBackground(int x, int y, int width, int height) {
        // Draw the black box
        Gui.drawRect(x, y, x + width, y + height, 0xFF000000); // Solid black background
        // Draw the white outline
        Gui.drawRect(x, y, x + width, y + 1, 0xFFFFFFFF); // Top border
        Gui.drawRect(x, y, x + 1, y + height, 0xFFFFFFFF); // Left border
        Gui.drawRect(x, y + height - 1, x + width, y + height, 0xFFFFFFFF); // Bottom border
        Gui.drawRect(x + width - 1, y, x + width, y + height, 0xFFFFFFFF); // Right border
    }

    private void drawCenteredString(String text, int x, int y) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2, y, 0xFFFFFF);
    }

    public void updateAnimation(int index) {
        int targetY = index * (HEIGHT + MARGIN) + MARGIN;

        // Easing effect for the animation
        if (smoothAnimation < targetY) {
            smoothAnimation += Math.max(1, (targetY - smoothAnimation) / 4);
        } else {
            smoothAnimation = targetY;
        }

        // Fade-out logic
        if (System.currentTimeMillis() >= endTime - FADE_DURATION) {
            float fadeProgress = (endTime - System.currentTimeMillis()) / (float) FADE_DURATION;
            if (fadeProgress <= 0) {
                if (fadeProgress > -1) {
                    smoothAnimation += Math.min(1, (targetY + HEIGHT - smoothAnimation) / REMOVE_DURATION);
                } else {
                    visible = false;
                }
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public long getEndTime() {
        return endTime;
    }
}
