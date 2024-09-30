package mehdi.bad.addons.Objects;

import mehdi.bad.addons.utils.RenderUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TitleManager {
	
	public static String title = null;
	public static long endTime = 0;
	
	public static void pushTitle(String s, int millisDur) {
		endTime = System.currentTimeMillis() + 10 + millisDur;
		title = s;
	}
	
	@SubscribeEvent
	public void onRenderEntity(RenderGameOverlayEvent e) {
		if (e.type != ElementType.TEXT) return;
		if (title != null) {
			RenderUtils.drawScaledCenteredString("§r§a" + title, RenderUtils.getScreenWidth() / 2, RenderUtils.getScreenHeight() / 2 - 32, 1);
			if (System.currentTimeMillis() > endTime) {
				endTime = 0;
				title = null;
			}
		}
	}
	
}
