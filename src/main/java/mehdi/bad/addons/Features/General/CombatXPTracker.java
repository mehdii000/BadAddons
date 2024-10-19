package mehdi.bad.addons.Features.General;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Config.modules.MovableModule;
import mehdi.bad.addons.Events.TickEndEvent;
import mehdi.bad.addons.utils.AuctionUtils;
import mehdi.bad.addons.utils.MinecraftUtils;
import mehdi.bad.addons.utils.RenderUtils;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CombatXPTracker extends MovableModule {

    private String xpEta = "§4NONE";
    private int initialXp = -1;

    public CombatXPTracker() {
        super("CombatTracker", 500, 410, 50, 15, false);
    }

    @SubscribeEvent
    public void onTickEnd(TickEndEvent e) {
        if (!Configs.CombatXPTracker) return;
        if (SkyblockUtils.isInDungeon()) return;

        // Fetching tab list from Minecraft
        String[] tabList = MinecraftUtils.fetchTabList();
        String xpValue = null;

        // Loop through each line to find the one that contains "Combat ##:"
        for (String line : tabList) {
            if (line.matches(".*Combat \\d{2}:.*")) {  // Check if the line contains "Combat ##:"
                // Split from ":" to "/"
                String[] parts = line.split(":|/");
                if (parts.length >= 2) {
                    xpValue = parts[1].trim();  // Get the middle part (XP value) and trim spaces
                }
                break;
            }
        }

        // If we found an XP value, calculate the XP difference
        if (xpValue != null) {
            int parsedXp = parseStringToInt(xpValue);  // Parse XP once for reuse
            if (initialXp < 0 || parsedXp - initialXp < 0) {
                initialXp = parsedXp;  // Initialize the XP tracker if necessary
            }

            xpEta = ":DIAMOND_SWORD: §7Xp: §b+" + AuctionUtils.formatPrice(parsedXp - initialXp);
        } else {
            xpEta = ":BARRIER:  §c/tab §7-> §cSkills §7-> §cCombatXP";
        }
    }

    // Parse a string like "3,094,243" into an integer
    private int parseStringToInt(String numberString) {
        // Remove control codes and commas
        String cleanedString = StringUtils.stripControlCodes(numberString.replace(",", ""));
        double number = Double.parseDouble(cleanedString);  // Parse as a double to handle decimals
        return (int) number;  // Truncate the decimal part
    }

    @Override
    public void render() {
        if (!Configs.CombatXPTracker) return;
        if (SkyblockUtils.isInDungeon()) return;
        int textWidth = RenderUtils.getStringWidthWithItem(xpEta);
        int textHeight = BadAddons.mc.fontRendererObj.FONT_HEIGHT;

        float scaleX = (float) getWidth() / textWidth;
        float scaleY = (float) getHeight() / textHeight;
        float factor = Math.min(scaleX, scaleY) * 0.9f;

        int centeredX = getX() + (getWidth() - (int)(textWidth * factor)) / 2;
        int centeredY = getY() + (getHeight() - (int)(textHeight * factor)) / 2;

        // Apply scaling
        GlStateManager.scale(factor, factor, 1);
        RenderUtils.renderStringWithItems(xpEta, centeredX / factor, centeredY / factor, 1, true);
        GlStateManager.scale(1 / factor, 1 / factor, 1); // Revert scaling
    }


}
