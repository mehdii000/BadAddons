package mehdi.bad.addons.Features.Kuudra;

import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Objects.NotificationManager;
import mehdi.bad.addons.Objects.Pathfinding;
import mehdi.bad.addons.utils.GuiUtils;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class SupplyHighlight {

	public static BlockPos pathStart, pathEnd;
	public static Pathfinding.Path path;
	public final String SUPPLY_REGEX = "recovered one of Elle's supplies! ";
	  
	@SubscribeEvent
	  public void onChatStuff(ClientChatReceivedEvent event) {
	    String message = event.message.getUnformattedText();
	    if (message.contains(SUPPLY_REGEX) && Configs.SupplyCount) {
			NotificationManager.pushNotification("§a§l" + message.split(SUPPLY_REGEX)[0], "§bGAMING", 2500);
	    }
	}

	@SubscribeEvent
	public void onWorldRender(RenderWorldLastEvent e) {
		if (path != null) {
			path.getPath().forEach(node -> {
				GuiUtils.drawBoundingBoxAtBlock(node, Color.RED, 0.25f);
			});
		}
	}

}