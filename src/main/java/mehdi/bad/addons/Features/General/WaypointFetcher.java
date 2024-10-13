package mehdi.bad.addons.Features.General;

import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.MathUtils;
import mehdi.bad.addons.utils.RealRenderUtils;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WaypointFetcher {

    private int[] waypointToRender = null;
    private long timeOfRendering = -1;

    @SubscribeEvent
    public void onReadChat(ClientChatReceivedEvent e) {
        if (Configs.FetchWaypoints && e.type == 0) {
            String message = StringUtils.stripControlCodes(e.message.getUnformattedText());
            waypointToRender = parseCoordinates(message);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent e) {
        if (System.currentTimeMillis() > timeOfRendering) {
            timeOfRendering = -1;
            if (waypointToRender != null) waypointToRender = null;
        }
        if (Configs.FetchWaypoints && waypointToRender != null && timeOfRendering > 0) {
            RealRenderUtils.renderWaypointText("§bWAYPOINT §c" + MathUtils.formatTicks(System.currentTimeMillis()-timeOfRendering), waypointToRender[0], waypointToRender[1], waypointToRender[2], e.partialTicks);
        }
    }

    public int[] parseCoordinates(String message) {
        String regex = ".*x:\\s*(-?\\d+),\\s*y:\\s*(-?\\d+),\\s*z:\\s*(-?\\d+).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            ChatLib.chat("§e[BA] §bFound coordinates in chat!");

            try {
                int x = Integer.parseInt(matcher.group(1)); // Get first group (x value)
                int y = Integer.parseInt(matcher.group(2)); // Get second group (y value)
                int z = Integer.parseInt(matcher.group(3)); // Get third group (z value)
                return new int[]{x, y, z}; // Return the values in an integer array
            } catch (NumberFormatException e) {
                ChatLib.chat("§e[BA] §cError: One of the coordinates is not a valid integer!");
                return null; // Return null if there's an error in parsing the integers
            }
        } else {
            ChatLib.chat("§e[BA] §cError: Coordinates not found in the message!");
        }
        return null;
    }

}
