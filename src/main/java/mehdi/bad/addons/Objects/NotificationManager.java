package mehdi.bad.addons.Objects;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationManager {

    static List<Notification> notifications = new ArrayList<>();

    public static void pushNotification(String title, String description, int durationTicks) {
        long endTime = System.currentTimeMillis() + durationTicks; // Convert ticks to milliseconds
        notifications.add(new Notification(title, description, endTime));
    }

    public static void removeNotification(Notification notification) {
        notifications.remove(notification);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT)
            return;

        int index = 0;
        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            if (notification.getEndTime() > System.currentTimeMillis()) {
                notification.draw(event, index);
                notification.updateAnimation(index);
                index++;
            } else {
                iterator.remove();
            }
        }
    }
}