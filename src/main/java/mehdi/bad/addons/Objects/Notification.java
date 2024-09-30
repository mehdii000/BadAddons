package mehdi.bad.addons.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

public class Notification {

    private String title;
    private String description;
    private long endTime;
    private int smoothAnimation = 0;
    private boolean visible = true;
    private static final int WIDTH = 150;
    private static final int HEIGHT = 35;
    private static final int MARGIN = 5;
    private static final int FADE_DURATION = 10;
    private static final int REMOVE_DURATION = 10;

    public Notification(String title, String description, long endTime) {
        this.title = title;
        this.description = description;
        this.endTime = endTime;
    }

    public void draw(RenderGameOverlayEvent.Post event) {
        if (!visible) return;

        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();
        int notificationX = width / 2 - WIDTH / 2;
        int notificationY = MARGIN + smoothAnimation;

        drawBackground(notificationX, notificationY, WIDTH, HEIGHT, 0.6f);
        drawCenteredString("§f" + title, notificationX + WIDTH / 2, notificationY + 12, 1);
        drawCenteredString(description, notificationX + WIDTH / 2, notificationY + 26, 0.8f);

        updateAnimation();
    }

    private void drawBackground(int x, int y, int width, int height, float alpha) {
        int backgroundColor = new Color(0, 0, 0, alpha).getRGB();
        Gui.drawRect(x, y, x + width, y + height, backgroundColor);
    }

    private void drawCenteredString(String text, int x, int y, float scale) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2, y, 0xFFFFFF);
    }

    private void updateAnimation() {
        int currentIndex = NotificationManager.notifications.indexOf(this);
        int targetY = currentIndex * (HEIGHT + MARGIN) + MARGIN;
        if (smoothAnimation < targetY) {
            smoothAnimation += Math.max(1, (targetY - smoothAnimation) / 4);
        } else {
            smoothAnimation = targetY;
        }

        if (System.currentTimeMillis() >= endTime - FADE_DURATION) {
            float fadeProgress = (endTime - System.currentTimeMillis()) / (float) FADE_DURATION;
            if (fadeProgress <= 0) {
                if (fadeProgress > -1) {
                    smoothAnimation += Math.min(1, (targetY + HEIGHT - smoothAnimation) / REMOVE_DURATION);
                } else {
                    visible = false;
                    NotificationManager.removeNotification(this);
                }
            }
        }
    }

    public long getEndTime() {
        return endTime;
    }
}